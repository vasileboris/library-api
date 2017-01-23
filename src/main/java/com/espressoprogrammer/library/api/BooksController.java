package com.espressoprogrammer.library.api;

import com.espressoprogrammer.library.dto.Book;
import com.espressoprogrammer.library.dto.ErrorCause;
import com.espressoprogrammer.library.dto.ErrorResponse;
import com.espressoprogrammer.library.persistence.BooksDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static com.espressoprogrammer.library.dto.ErrorResponse.Type.DATA_VALIDATION;
import static java.util.Arrays.asList;

@RestController
public class BooksController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BooksDao booksDao;

    @GetMapping(value = "/users/{user}/books")
    public ResponseEntity<List<Book>> getUserBooks(@PathVariable("user") String user,
                                                   @RequestParam(value = "searchText", required = false) String searchText)  {
        try {
            logger.debug("Look for books for user {}", user);

            List<Book> userBooks = booksDao.getUserBooks(user, searchText);
            return new ResponseEntity<>(userBooks, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Error on looking for books", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/users/{user}/books")
    public ResponseEntity createUserBook(@PathVariable("user") String user,
                                         @RequestBody Book book)  {
        try {
            logger.debug("Add new book for user {}", user);

            if(hasTheBook(user, book)) {
                ErrorResponse errorResponse = new ErrorResponse(DATA_VALIDATION,
                    asList(new ErrorCause(asList("isbn10", "isbn13"), "book.isbn.exists")));

                return new ResponseEntity(errorResponse ,HttpStatus.FORBIDDEN);
            }

            String uuid = booksDao.createUserBook(user, book);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.LOCATION, String.format("/users/%s/books/%s", user, uuid));
            return new ResponseEntity(httpHeaders, HttpStatus.CREATED);
        } catch (Exception ex) {
            logger.error("Error on adding new book", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/users/{user}/books/{uuid}")
    public ResponseEntity<Book> getUserBook(@PathVariable("user") String user,
                                            @PathVariable("uuid") String uuid)  {
        try {
            logger.debug("Look for book for user {} with uuid {} ", user, uuid);

            Optional<Book> optionalBook = booksDao.getUserBook(user, uuid);
            if(!optionalBook.isPresent()) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(optionalBook.get(), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Error on looking for books", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/users/{user}/books/{uuid}")
    public ResponseEntity updateUserBook(@PathVariable("user") String user,
                                         @PathVariable("uuid") String uuid,
                                         @RequestBody Book book)  {
        try {
            logger.debug("Update book for user {} with uuid {} ", user, uuid);

            if(hasTheBook(user, book)) {
                ErrorResponse errorResponse = new ErrorResponse(DATA_VALIDATION,
                    asList(new ErrorCause(asList("isbn10", "isbn13"), "book.isbn.exists")));

                return new ResponseEntity(errorResponse ,HttpStatus.FORBIDDEN);
            }

            Optional<String> optionalBook = booksDao.updateUserBook(user, uuid, book);
            if(!optionalBook.isPresent()) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Error on looking for books", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value= "/users/{user}/books/{uuid}")
    public ResponseEntity<Book> deleteUserBook(@PathVariable("user") String user,
                                               @PathVariable("uuid") String uuid)  {
        try {
            logger.debug("Delete book for user {} with uuid {}", user, uuid);

            Optional<String> optionalBook = booksDao.deleteUserBook(user, uuid);
            if(!optionalBook.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Error on looking for books", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean hasTheBook(@PathVariable("user") String user, @RequestBody Book book) {
        List<Book> existingBooks = booksDao.getUserBooks(user);
        for(Book existingBook : existingBooks) {
            if(areDifferentBooks(book, existingBook) && haveTheSameISBN(book, existingBook)) {
                return true;
            }
        }
        return false;
    }

    private boolean areDifferentBooks(@RequestBody Book book, Book existingBook) {
        return !existingBook.getUuid().equals(book.getUuid());
    }

    private boolean haveTheSameISBN(@RequestBody Book book, Book existingBook) {
        return (!StringUtils.isEmpty(existingBook.getIsbn10()) && existingBook.getIsbn10().equals(book.getIsbn10()))
            || (!StringUtils.isEmpty(existingBook.getIsbn13()) && existingBook.getIsbn13().equals(book.getIsbn13()));
    }
}
