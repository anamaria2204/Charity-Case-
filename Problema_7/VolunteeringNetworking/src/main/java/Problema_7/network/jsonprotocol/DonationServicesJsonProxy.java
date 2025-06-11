package Problema_7.network.jsonprotocol;

import Problema_7.model.domain.CharityCase;
import Problema_7.model.domain.Donation;
import Problema_7.model.domain.Donor;
import Problema_7.model.domain.Volunteer;
import Problema_7.services.Exceptions;
import Problema_7.services.IObserver;
import Problema_7.services.IServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

public class DonationServicesJsonProxy implements IServices {
    private final String host;
    private final int port;

    private IObserver client;

    private Socket connection;
    private BufferedReader input;
    private PrintWriter output;
    private final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .create();
    private boolean finished = false;
    private static final Logger logger = LogManager.getLogger(DonationServicesJsonProxy.class);

    private Queue<Response> responses = new LinkedList<>();
    private final Object responseLock = new Object();  // Object used for synchronization

    public DonationServicesJsonProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void initializeConnection() throws IOException {
        connection = new Socket(host, port);
        output = new PrintWriter(connection.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        startReaderThread();
    }

    private void startReaderThread() {
        Thread readerThread = new Thread(() -> {
            while (!finished) {
                try {
                    String line = input.readLine();
                    if (line != null) {
                        logger.debug("Received raw response: {}", line);
                        Response resp = gson.fromJson(line, Response.class);

                        if (isUpdate(resp.getType())) {
                            try {
                                handleUpdate(resp);
                            } catch (Exceptions e) {
                                logger.error("Error handling update: {}",e.getMessage());
                            }
                        } else {
                            synchronized (responseLock) {
                                responses.add(resp);
                                responseLock.notify();
                            }
                        }
                    }
                } catch (IOException e) {
                    logger.error("Error in reader thread: {}", e.getMessage());
                }
            }
        });
        readerThread.setDaemon(true);
        readerThread.start();
    }

    private boolean isUpdate(ResponseType type) {
        return type == ResponseType.NEW_DONATION_ALERT
            || type == ResponseType.CHARITABLE_CASES_UPDATED
            || type == ResponseType.DONORS_UPDATED;
    }

    private void handleUpdate(Response resp) throws Exceptions {
        if (resp.getType() == ResponseType.NEW_DONATION_ALERT) {
            client.newDonationAdded(resp.getDataAs(Donation.class));
        } else if (resp.getType() == ResponseType.CHARITABLE_CASES_UPDATED) {
            List<CharityCase> cases = resp.getDataAs2(new TypeToken<List<CharityCase>>() {}.getType());
            client.charitableCasesUpdated(cases);
        }
    }

    private void sendRequest(Request request) throws IOException {
        String json = gson.toJson(request);
        output.println(json);
        output.flush();
        logger.debug("Sent request: {} ", request.getRequestType());
    }

    private Response readResponse() throws InterruptedException {
        synchronized (responseLock) {
            while (responses.isEmpty()) {
                logger.info("Waiting for response...");
                responseLock.wait();
            }
            Response response = responses.poll();
            logger.debug("Received response: {} ", response);
            return response;
        }
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            logger.error("Error closing connection: {}", e.getMessage());
        }
    }

    @Override
    public void login(Volunteer volunteer, IObserver client) throws Exceptions {
        try {
            initializeConnection();
            this.client = client;
            Request request = Request.login(volunteer);
            sendRequest(request);
            Response response = readResponse();
            if (response.getType() == ResponseType.LOGIN_SUCCESS) {
                logger.debug("Login successful");
            } else if (response.getType() == ResponseType.LOGIN_FAILED) {
                closeConnection();
                throw new Exceptions("Login failed: " + response.getErrorMessage());
            }
        } catch (IOException | InterruptedException e) {
            throw new Exceptions("Error while logging in: " + e.getMessage());
        }
    }

    @Override
    public void logout(Volunteer volunteer, IObserver client) throws Exceptions {
        try {
            sendRequest(Request.logout());
        } catch (IOException e) {
            throw new Exceptions("Error while logging out: " + e.getMessage());
        }
        try {
            Response response = readResponse();
            if (response.getType() != ResponseType.OK) {
                throw new Exceptions("Logout failed: " + response.getErrorMessage());
            }
        } catch (InterruptedException e) {
            throw new Exceptions("Error during logout: " + e.getMessage());
        }
    }

    @Override
    public void newDonation(Donation donation, IObserver client) throws Exceptions {
        try {
            sendRequest(Request.makeDonation(donation));
        } catch (IOException e) {
            throw new Exceptions("Error while making donation: " + e.getMessage());
        }
        try {
            Response response = readResponse();
            if (response.getType() != ResponseType.DONATION_REGISTERED) {
                throw new Exceptions("Register donation failed: " + response.getErrorMessage());
            }
        } catch (InterruptedException e) {
            throw new Exceptions("Error during donation registration: " + e.getMessage());
        }
    }

    @Override
    public Donor addDonor(String surname, String name, String address, String phone, IObserver client) throws Exceptions {
        Donor donor = new Donor(surname, name, address, phone);
        try {
            sendRequest(Request.addDonor(donor));
        } catch (IOException e) {
            throw new Exceptions("Error while adding donor: " + e.getMessage());
        }
        try {
            Response response = readResponse();
            if (response.getType() != ResponseType.DONOR_ADDED) {
                throw new Exceptions("Add donor failed: " + response.getErrorMessage());
            }
            return response.getDataAs2(new TypeToken<Donor>(){}.getType());
        } catch (InterruptedException e) {
            throw new Exceptions("Error adding donor: " + e.getMessage());
        }
    }

    @Override
    public List<CharityCase> getCharitableCases() throws Exceptions {
        try {
            sendRequest(Request.getCharitableCases());
        } catch (IOException e) {
            throw new Exceptions("Error while getting charitable cases: " + e.getMessage());
        }
        try {
            Response response = readResponse();
            logger.debug("Received response type for getCharitableCases: {}",  response.getType());
            if (response.getType() != ResponseType.CHARITABLE_CASES_LIST) {
                logger.debug("Expected CHARITABLE_CASES_LIST but got: {}", response.getType());
                throw new Exceptions("Get charitable cases failed: " + response.getErrorMessage());
            }
            List<CharityCase> cases = response.getDataAs2(new TypeToken<List<CharityCase>>(){}.getType());
            if (cases == null) {
                logger.error("No charitable cases found!");
            } else {
                logger.debug("Found {} ", cases.size());
            }
            return cases;
        } catch (InterruptedException e) {
            throw new Exceptions("Error retrieving charitable cases: " + e.getMessage());
        }
    }

    @Override
    public List<Donor> searchDonor(String searchTerm) throws Exceptions {
        try {
            sendRequest(Request.searchDonor(searchTerm));
        } catch (IOException e) {
            throw new Exceptions("Error while searching donor: " + e.getMessage());
        }
        try {
            Response response = readResponse();
            if (response.getType() != ResponseType.DONOR_SEARCH_RESULTS) {
                throw new Exceptions("Search donor failed: " + response.getErrorMessage());
            }
            List<Donor> donors = response.getDataAs2(new TypeToken<List<Donor>>(){}.getType());
            return donors;
        } catch (InterruptedException e) {
            throw new Exceptions("Error searching donor: " + e.getMessage());
        }
    }

    @Override
    public List<Donor> getAllDonor() throws Exceptions {
        try {
            sendRequest(Request.getUpdatedDonors());
        } catch (IOException e) {
            throw new Exceptions("Error while getting all donors: " + e.getMessage());
        }
        try {
            Response response = readResponse();
            if (response.getType() != ResponseType.DONOR_DETAILS) {
                throw new Exceptions("Get all donors failed: " + response.getErrorMessage());
            }
            return response.getDataAs2(List.class);
        } catch (InterruptedException e) {
            throw new Exceptions("Error retrieving all donors: " + e.getMessage());
        }
    }
}
