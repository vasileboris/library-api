package com.espressoprogrammer.library.service;

import com.espressoprogrammer.library.dto.Book;
import com.espressoprogrammer.library.dto.ReadingSession;
import com.espressoprogrammer.library.persistence.BooksDao;
import com.espressoprogrammer.library.persistence.ReadingSessionsDao;
import com.espressoprogrammer.library.service.BooksException.Reason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;


@Service
public class BooksService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BooksDao booksDao;

    @Autowired
    private ReadingSessionsDao readingSessionsDao;

    public List<Book> getUserBooks(String user, String searchText)  {
        logger.debug("Look for books for user {}", user);

        return booksDao.getUserBooks(user, searchText);
    }

    public Book createUserBook(String user, Book book) throws BooksException {
        logger.debug("Add new book for user {}", user);

        if(hasTheBook(user, book)) {
            throw new BooksException(Reason.BOOK_ALREADY_EXISTS);
        }

        return booksDao.createUserBook(user, book);
    }

    public Book getUserBook(String user, String uuid) throws BooksException {
        logger.debug("Look for book for user {} with uuid {} ", user, uuid);

        Optional<Book> optionalBook = booksDao.getUserBook(user, uuid);
        if(!optionalBook.isPresent()) {
            throw new BooksException(Reason.BOOK_NOT_FOUND);
        }

        return optionalBook.get();
    }

    public String updateUserBook(String user, String uuid, Book book) throws BooksException {
        logger.debug("Update book for user {} with uuid {} ", user, uuid);

        if(hasTheBook(user, book)) {
            throw new BooksException(Reason.BOOK_ALREADY_EXISTS);
        }

        Optional<String> optionalUuid = booksDao.updateUserBook(user, uuid, book);
        if(!optionalUuid.isPresent()) {
            throw new BooksException(Reason.BOOK_NOT_FOUND);
        }

        return optionalUuid.get();
    }

    public String deleteUserBook(String user, String uuid) throws BooksException {
        logger.debug("Delete book for user {} with uuid {}", user, uuid);

        List<ReadingSession> userReadingSessions = readingSessionsDao.getUserReadingSessions(user, uuid);
        for(ReadingSession userReadingSession : userReadingSessions) {
            if(!userReadingSession.getDateReadingSessions().isEmpty()) {
                throw new BooksException(Reason.BOOK_HAS_READING_SESSION);
            }
        }

        Optional<String> optionalUuid = booksDao.deleteUserBook(user, uuid);
        if(!optionalUuid.isPresent()) {
            throw new BooksException(Reason.BOOK_NOT_FOUND);
        }

        return optionalUuid.get();
    }

    private boolean hasTheBook(String user, Book book) {
        List<Book> existingBooks = booksDao.getUserBooks(user);
        for(Book existingBook : existingBooks) {
            if(areDifferentBooks(book, existingBook) && haveTheSameISBN(book, existingBook)) {
                return true;
            }
        }
        return false;
    }

    private boolean areDifferentBooks(Book book, Book existingBook) {
        return !existingBook.getUuid().equals(book.getUuid());
    }

    private boolean haveTheSameISBN(Book book, Book existingBook) {
        return (!StringUtils.isEmpty(existingBook.getIsbn10()) && existingBook.getIsbn10().equals(book.getIsbn10()))
                || (!StringUtils.isEmpty(existingBook.getIsbn13()) && existingBook.getIsbn13().equals(book.getIsbn13()));
    }

}
