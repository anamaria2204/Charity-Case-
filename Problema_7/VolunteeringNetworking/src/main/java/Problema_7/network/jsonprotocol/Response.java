package Problema_7.network.jsonprotocol;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import Problema_7.model.domain.CharityCase;
import Problema_7.model.domain.Donation;
import Problema_7.model.domain.Donor;
import Problema_7.model.domain.Volunteer;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

public class Response {
    @SerializedName("Type")
    private int typeValue;

    private transient ResponseType type;

    @SerializedName("ErrorMessage")
    private String errorMessage;

    @SerializedName("Data")
    private Object data;

    private Response() {}

    public Response(int typeValue, String errorMessage, Object data) {
        this.typeValue = typeValue;
        this.type = ResponseType.fromValue(typeValue);
        this.errorMessage = errorMessage;
        this.data = data;
    }

    public ResponseType getType() {
        if (type == null) {
            type = ResponseType.fromValue(typeValue);
        }
        return type;
    }

    public void setType(int typeValue) {
        this.typeValue = typeValue;
        this.type = ResponseType.fromValue(typeValue);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public <T> T getDataAs(Class<T> clazz) throws JsonParseException {
        if (data == null) {
            throw new IllegalStateException("Data is null or undefined.");
        }

        if (data instanceof JsonElement) {
            return new Gson().fromJson((JsonElement) data, clazz);
        }

        if (clazz.isInstance(data)) {
            return clazz.cast(data);
        }

        throw new ClassCastException("Cannot cast Data of type " + data.getClass() + " to " + clazz);
    }


    public <T> T getDataAs2(Type typeOfT) throws JsonParseException {
        if (data == null) {
            throw new IllegalStateException("Data is null or undefined.");
        }

        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

        String json = gson.toJson(data);
        return gson.fromJson(json, typeOfT);
    }

    public static Response ok() {
        return create(ResponseType.OK);
    }

    public static Response error(String errorMessage) {
        Response response = new Response();
        response.type = ResponseType.ERROR;
        response.typeValue = response.type.getValue();
        response.errorMessage = errorMessage;
        return response;
    }

    public static Response loginSuccess(Volunteer volunteer) {
        return create(ResponseType.LOGIN_SUCCESS, volunteer);
    }

    public static Response loginFailed(String reason) {
        Response response = new Response();
        response.type = ResponseType.LOGIN_FAILED;
        response.typeValue = response.type.getValue();
        response.errorMessage = reason;
        return response;
    }

    public static Response charitableCases(List<CharityCase> cases) {
        return create(ResponseType.CHARITABLE_CASES_LIST, cases);
    }

    public static Response donorDetails(List<Donor> donors) {
        return create(ResponseType.DONOR_DETAILS, donors);
    }

    public static Response donorSearchResults(List<Donor> donors) {
        return create(ResponseType.DONOR_SEARCH_RESULTS, donors);
    }

    public static Response donorAdded(Donor donor) {
        return create(ResponseType.DONOR_ADDED, donor);
    }

    public static Response donationRegistered(Donation donation) {
        return create(ResponseType.DONATION_REGISTERED, donation);
    }

    public static Response casesUpdated(List<CharityCase> updatedCases) {
        return create(ResponseType.CHARITABLE_CASES_UPDATED, updatedCases);
    }

    public static Response newDonationAlert(Donation donation) {
        return create(ResponseType.NEW_DONATION_ALERT, donation);
    }

    public static Response donorsUpdated(List<Donor> donors) {
        return create(ResponseType.DONORS_UPDATED, donors);
    }

    public static Response newDonorAlert(Donor donor) {
        return create(ResponseType.NEW_DONOR_ALERT, donor);
    }

    private static Response create(ResponseType type) {
        Response response = new Response();
        response.type = type;
        response.typeValue = type.getValue();
        response.data = null;
        return response;
    }

    private static <T> Response create(ResponseType type, T data) {
        Response response = new Response();
        response.type = type;
        response.typeValue = type.getValue();
        response.data = data;
        return response;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Response{");
        sb.append("type=").append(getType());

        if (errorMessage != null && !errorMessage.isEmpty()) {
            sb.append(", errorMessage='").append(errorMessage).append('\'');
        }

        if (data != null) {
            sb.append(", data=").append(data.toString());
        }

        sb.append('}');
        return sb.toString();
    }
}

