package Problema_7.network.jsonprotocol;
import Problema_7.model.domain.Donation;
import Problema_7.model.domain.Donor;
import Problema_7.model.domain.Volunteer;


public class Request {
    private RequestType requestType;
    private Volunteer volunteer;
    private Donor donor;
    private Donation donation;
    private String searchTerm;
    private long donorId;

    public Request() {
    }

    public Request(RequestType requestType, Volunteer volunteer, Donor donor, Donation donation, String searchTerm, long donorId) {
        this.requestType = requestType;
        this.volunteer = volunteer;
        this.donor = donor;
        this.donation = donation;
        this.searchTerm = searchTerm;
        this.donorId = donorId;
    }

    // Getteri și setteri (necesari pentru Gson)

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public Donor getDonor() {
        return donor;
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
    }

    public Donation getDonation() {
        return donation;
    }

    public void setDonation(Donation donation) {
        this.donation = donation;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public long getDonorId() {
        return donorId;
    }

    public void setDonorId(long donorId) {
        this.donorId = donorId;
    }

    // Metode statice pentru creare rapidă a instanțelor (similar C#)
    public static Request login(Volunteer volunteer) {
        return new Request(RequestType.LOGIN, volunteer, null, null, null, 0);
    }

    public static Request logout() {
        return new Request(RequestType.LOGOUT, null, null, null, null, 0);
    }

    public static Request getCharitableCases() {
        return new Request(RequestType.GET_CHARITABLE_CASES, null, null, null, null, 0);
    }

    public static Request searchDonor(String searchTerm) {
        return new Request(RequestType.SEARCH_DONOR, null, null, null, searchTerm, 0);
    }

    public static Request addDonor(Donor donor) {
        return new Request(RequestType.ADD_DONOR, null, donor, null, null, 0);
    }

    public static Request makeDonation(Donation donation) {
        return new Request(RequestType.MAKE_DONATION, null, null, donation, null, 0);
    }

    public static Request getDonorDetails() {
        return new Request(RequestType.GET_DONOR_DETAILS, null, null, null, null, 0);
    }

    public static Request getUpdatedDonors() {
        return new Request(RequestType.GET_UPDATED_DONORS, null, null, null, null, 0);
    }

    public static Request createGetDonorDetailsRequest(long donorId) {
        return new Request(RequestType.GET_DONOR_DETAILS, null, null, null, null, donorId);
    }

    public static Request createGetUpdatedDonorsRequest(long donorId) {
        return new Request(RequestType.GET_ALL_DONORS, null, null, null, null, donorId);
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestType=" + requestType +
                ", volunteer=" + volunteer +
                ", donor=" + donor +
                ", donation=" + donation +
                ", searchTerm='" + searchTerm + '\'' +
                ", donorId=" + donorId +
                '}';
    }
}
