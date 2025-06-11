package Problema_7.network.jsonprotocol;

import Problema_7.model.domain.CharityCase;
import Problema_7.model.domain.Donation;
import Problema_7.model.domain.Donor;
import Problema_7.model.domain.Volunteer;
import Problema_7.network.utils.TextUtils;
import Problema_7.services.Exceptions;
import Problema_7.services.IObserver;
import Problema_7.services.IServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;

import static Problema_7.network.jsonprotocol.RequestType.LOGIN;

public class VolunteerJsonWorker implements Runnable, IObserver {
    private final IServices server;
    private final Socket connection;
    private BufferedReader input;
    private PrintWriter output;
    private final Gson gsonFormatter ;
    private volatile boolean connected;
    private static final Logger logger = LogManager.getLogger(VolunteerJsonWorker.class);
    private Volunteer volunteer;

    public VolunteerJsonWorker(IServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        this.gsonFormatter = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .create();

        try {
            this.output = new PrintWriter(connection.getOutputStream(), true);
            this.input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            this.connected = true;
        } catch (IOException e) {
            logger.error("Error initializing worker: ", e);
        }
    }

    @Override
    public void run() {
        while (connected) {
            try {
                String requestLine = input.readLine();
                if (requestLine == null) {
                    logger.info("Client closed connection");
                    break;
                }

                Request request = gsonFormatter.fromJson(requestLine, Request.class);
                logger.debug("Received request: {}", request.getRequestType());

                Response response = handleRequest(request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException e) {
                if (connected) {
                    logger.error("Reading error: ", e);
                }
                break;
            }
        }
        closeConnection();
    }

    @Override
    public void charitableCaseUpdated(CharityCase charityCase) throws Exceptions {
        Response resp = JsonProtocolUtils.createCasesUpdatedResponse(new CharityCase[]{charityCase});
        logger.debug("Notifying client about updated charity case: {}", charityCase);
        try {
            sendResponse(resp);
        } catch (IOException e) {
            logger.error("Error sending updated charity case response", e);
            throw new Exceptions("Error sending update to client", e);
        }
    }

    @Override
    public void newDonationAdded(Donation donation) throws Exceptions {
        Response resp = JsonProtocolUtils.createNewDonationAlertResponse(donation);
        logger.debug("Notifying client about new donation: {}", donation);
        try {
            sendResponse(resp);
        }
        catch (IOException e) {
            logger.error("Error sending new donation alert response", e);
            throw new Exceptions("Error sending update to client", e);
        }
    }

    @Override
    public void charitableCasesUpdated(List<CharityCase> updatedCases) throws Exceptions {
        Response resp = JsonProtocolUtils.createCasesUpdatedResponse(updatedCases.toArray(new CharityCase[0]));
        logger.debug("Notifying client about multiple updated charity cases");
        try {
            sendResponse(resp);
        } catch (IOException e) {
            logger.error("Error sending updated charity cases response", e);
            throw new Exceptions("Error notifying client", e);
        }
    }

    private Response handleRequest(Request request) {
        try {
            switch (request.getRequestType()) {
                case LOGIN:
                    return handleLogin(request);
                case LOGOUT:
                    return handleLogout();
                case MAKE_DONATION:
                    return handleDonation(request);
                case GET_CHARITABLE_CASES:
                    return handleGetCharitableCases();
                case SEARCH_DONOR:
                    return handleSearchDonor(request);
                case ADD_DONOR:
                    return handleAddDonor(request);
                default:
                    return JsonProtocolUtils.createErrorResponse("Unknown request type");
            }
        } catch (Exceptions e) {
            return JsonProtocolUtils.createErrorResponse(e.getMessage());
        }
    }

    private Response handleLogin(Request request) throws Exceptions {
        // Removed DTO usage, directly mapping the data
        Volunteer volunteerRequest = request.getVolunteer();
        volunteerRequest.setPassword(TextUtils.simpleDecode(volunteerRequest.getPassword()));

        server.login(volunteerRequest, this);
        volunteer = volunteerRequest;  // Set the volunteer
        return JsonProtocolUtils.createLoginSuccessResponse(volunteer);
    }

    private Response handleLogout() throws Exceptions {
        server.logout(volunteer, this);
        connected = false;
        return JsonProtocolUtils.createOkResponse();
    }

    private Response handleDonation(Request request) throws Exceptions {
        // Removed DTO usage, directly mapping the data
        Donation donationRequest = request.getDonation();
        server.newDonation(donationRequest, this);
        return JsonProtocolUtils.createDonationRegisteredResponse(donationRequest);
    }

    private Response handleGetCharitableCases() throws Exceptions {
        List<CharityCase> cases = server.getCharitableCases();
        return JsonProtocolUtils.createCharitableCasesResponse(cases.toArray(new CharityCase[0]));
    }

    private Response handleSearchDonor(Request request) throws Exceptions {
        String searchTerm = request.getSearchTerm();
        List<Donor> donors = server.searchDonor(searchTerm);
        return JsonProtocolUtils.createDonorSearchResponse(donors.toArray(new Donor[0]));
    }

    private Response handleAddDonor(Request request) throws Exceptions {
        // Removed DTO usage, directly mapping the data
        Donor donorRequest = request.getDonor();
        Donor addedDonor = server.addDonor(donorRequest.getSurname(), donorRequest.getName(), donorRequest.getAddress(), donorRequest.getPhoneNumber(), this);
        Response response = JsonProtocolUtils.createDonorAddedResponse(addedDonor);
        return response;
    }

    private void sendResponse(Response response) throws IOException {
        String responseLine = gsonFormatter.toJson(response);
        logger.debug("Sending response: {}", responseLine);
        synchronized (output) {
            output.println(responseLine);
        }
    }

    private void closeConnection() {
        connected = false;
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            logger.info("Connection closed for volunteer: {}", volunteer != null ? volunteer.getUsername() : "unknown");
        } catch (IOException e) {
            logger.error("Error closing connection: ", e);
        }
    }
}
