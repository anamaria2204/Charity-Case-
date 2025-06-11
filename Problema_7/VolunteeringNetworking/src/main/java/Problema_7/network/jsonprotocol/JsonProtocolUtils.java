package Problema_7.network.jsonprotocol;

import Problema_7.model.domain.CharityCase;
import Problema_7.model.domain.Donation;
import Problema_7.model.domain.Donor;
import Problema_7.model.domain.Volunteer;

import java.util.Arrays;
import java.util.List;

public class JsonProtocolUtils {


    public static Response createOkResponse() {
        return Response.ok();
    }

    public static Response createErrorResponse(String errorMessage) {
        return Response.error(errorMessage);
    }

    public static Response createLoginSuccessResponse(Volunteer volunteer) {
        return Response.loginSuccess(volunteer);
    }

    public static Response createLoginFailedResponse(String reason) {
        return Response.loginFailed(reason);
    }

    public static Response createCharitableCasesResponse(CharityCase[] cases) {
        List<CharityCase> casesList = Arrays.asList(cases);
        return Response.charitableCases(casesList);
    }

    public static Response createDonorSearchResponse(Donor[] donors) {
        List<Donor> donorsList = Arrays.asList(donors);
        return Response.donorSearchResults(donorsList);
    }

    public static Response createDonorAddedResponse(Donor donor) {
        return Response.donorAdded(donor);
    }

    public static Response createDonationRegisteredResponse(Donation donation) {
        return Response.donationRegistered(donation);
    }

    public static Response createCasesUpdatedResponse(CharityCase[] updatedCases) {
        List<CharityCase> casesList = Arrays.asList(updatedCases);
        return Response.casesUpdated(casesList);
    }

    public static Response createNewDonationAlertResponse(Donation donation) {
        return Response.newDonationAlert(donation);
    }

    public static Response createDonorListUpdatedResponse(List<Donor> donors) {
        return Response.donorsUpdated(donors);
    }

    public static Response createDonorDetailsResponse(List<Donor> donors) {
        return Response.donorDetails(donors);
    }

    // ================== REQUEST METHODS ================== //

    public static Request createLoginRequest(String username, String password) {
        Volunteer volunteer = new Volunteer(username, password);
        return Request.login(volunteer);
    }

    public static Request createLogoutRequest() {
        return Request.logout();
    }

    public static Request createGetCharitableCasesRequest() {
        return Request.getCharitableCases();
    }

    public static Request createSearchDonorRequest(String searchTerm) {
        return Request.searchDonor(searchTerm);
    }

    public static Request createAddDonorRequest(Donor donor) {
        return Request.addDonor(donor);
    }

    public static Request createMakeDonationRequest(Donation donation) {
        return Request.makeDonation(donation);
    }

    public static Request createGetAllDonorsRequest() {
        return Request.getUpdatedDonors();
    }

}