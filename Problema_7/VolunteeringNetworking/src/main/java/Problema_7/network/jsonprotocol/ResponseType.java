package Problema_7.network.jsonprotocol;

public enum ResponseType {

    OK(0),
    ERROR(1),

    LOGIN_SUCCESS(2),
    LOGIN_FAILED(3),
    CHARITABLE_CASES_LIST(4),
    DONOR_DETAILS(5),
    DONOR_SEARCH_RESULTS(6),
    DONOR_ADDED(7),
    DONATION_REGISTERED(8),

    CHARITABLE_CASES_UPDATED(9),
    NEW_DONATION_ALERT(10),
    DONORS_UPDATED(11),
    NEW_DONOR_ALERT(12);

    private int value;

    ResponseType(int value) {
        this.value = value;
    }

    // Metodă statică pentru a obține RequestType din valoarea numerică
    public static ResponseType fromValue(int value) {
        for (ResponseType type : ResponseType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unexpected value: " + value);
    }

    // Obține valoarea numerică
    public int getValue() {
        return value;
    }
}
