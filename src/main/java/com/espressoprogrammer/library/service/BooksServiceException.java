package com.espressoprogrammer.library.service;

public class BooksServiceException extends Exception {

    public enum Reason {
        BOOK_ALREADY_EXISTS,
        BOOK_NOT_FOUND,
        BOOK_HAS_READING_SESSION
    }

    private Reason reason;

    public BooksServiceException(Reason reason) {
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
