package Problema_7.services;

public class Exceptions extends Exception {
    public Exceptions() {
    }

    public Exceptions(String message) {
        super(message);
    }

    public Exceptions(String message, Throwable cause) {
        super(message, cause);
    }
}