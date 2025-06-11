package Problema_7.network.jsonprotocol;

public enum RequestType {
    LOGIN(0),
    LOGOUT(1),
    GET_CHARITABLE_CASES(2),
    SEARCH_DONOR(3),
    ADD_DONOR(4),
    MAKE_DONATION(5),
    GET_DONOR_DETAILS(6),
    GET_UPDATED_DONORS(7),
    GET_ALL_DONORS(8);

    private int value;

    // Constructor pentru atribuirea valorii numerice fiecărui tip de request
    RequestType(int value) {
        this.value = value;
    }

    // Metodă statică pentru a obține RequestType din valoarea numerică
    public static RequestType fromValue(int value) {
        for (RequestType type : RequestType.values()) {
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
