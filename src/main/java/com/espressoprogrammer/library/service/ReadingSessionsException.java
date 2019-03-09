package com.espressoprogrammer.library.service;

public class ReadingSessionsException extends Exception {

    public enum Reason {
        READING_SESSION_NOT_FOUND,
        DATE_READING_SESSION_NOT_FOUND,
        READING_SESSION_ALREADY_EXISTS,
        DATE_READING_SESSION_ALREADY_EXISTS,
        DATE_READING_SESSION_INVALID
    }

    private Reason reason;

    public ReadingSessionsException(Reason reason) {
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
