package com.espressoprogrammer.library.service;

public class BooksException extends Exception {

    public enum Reason {
        BOOK_ALREADY_EXISTS,
        BOOK_NOT_FOUND,
        BOOK_HAS_READING_SESSION,
        BOOK_INVALID
    }

    private Reason reason;

    public BooksException(Reason reason) {
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
