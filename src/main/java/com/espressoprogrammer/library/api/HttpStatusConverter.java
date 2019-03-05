package com.espressoprogrammer.library.api;

import com.espressoprogrammer.library.service.BooksException;
import com.espressoprogrammer.library.service.ReadingSessionsException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class HttpStatusConverter {

    public HttpStatus from(BooksException ex) {
        switch (ex.getReason()) {
            case BOOK_ALREADY_EXISTS:
            case BOOK_HAS_READING_SESSION:
                return HttpStatus.FORBIDDEN;
            case BOOK_NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    public HttpStatus from(ReadingSessionsException ex) {
        switch (ex.getReason()) {
            case READING_SESSION_ALREADY_EXISTS:
            case DATE_READING_SESSION_ALREADY_EXISTS:
                return HttpStatus.FORBIDDEN;
            case READING_SESSION_NOT_FOUND:
            case DATE_READING_SESSION_NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
