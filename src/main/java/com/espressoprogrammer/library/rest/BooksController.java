package com.espressoprogrammer.library.rest;

import com.espressoprogrammer.library.dto.Book;
import com.espressoprogrammer.library.service.BooksService;
import com.espressoprogrammer.library.service.BooksException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BooksController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BooksService booksService;

    @Autowired
    private HttpStatusConverter httpStatusConverter;

    @GetMapping(value = "/users/{user}/books")
    public ResponseEntity<List<Book>> getUserBooks(@PathVariable("user") String user,
                                                   @RequestParam(value = "searchText", required = false) String searchText)  {
        try {
            logger.debug("Look for books for user {}", user);

            List<Book> userBooks = booksService.getUserBooks(user, searchText);
            return new ResponseEntity<>(userBooks, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Error on looking for books", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/users/{user}/books")
    public ResponseEntity<Book> createUserBook(@PathVariable("user") String user,
                                               @RequestBody Book book)  {
        try {
            logger.debug("Add new book for user {}", user);

            Book persistedBook = booksService.createUserBook(user, book);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.LOCATION, String.format("/users/%s/books/%s", user, persistedBook.getUuid()));
            return new ResponseEntity(persistedBook, httpHeaders, HttpStatus.CREATED);
        } catch (BooksException ex) {
            logger.error("Error on adding new book", ex);
            return new ResponseEntity(httpStatusConverter.from(ex));
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

            Book book = booksService.getUserBook(user, uuid);
            return new ResponseEntity(book, HttpStatus.OK);
        } catch (BooksException ex) {
            logger.error("Error on looking for books", ex);
            return new ResponseEntity(httpStatusConverter.from(ex));
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

            booksService.updateUserBook(user, uuid, book);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (BooksException ex) {
            logger.error("Error on updating book", ex);
            return new ResponseEntity(httpStatusConverter.from(ex));
        } catch (Exception ex) {
            logger.error("Error on updating book", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value= "/users/{user}/books/{uuid}")
    public ResponseEntity deleteUserBook(@PathVariable("user") String user,
                                               @PathVariable("uuid") String uuid)  {
        try {
            logger.debug("Delete book for user {} with uuid {}", user, uuid);

            booksService.deleteUserBook(user, uuid);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (BooksException ex) {
            logger.error("Error on deleting book", ex);
            return new ResponseEntity(httpStatusConverter.from(ex));
        } catch (Exception ex) {
            logger.error("Error on deleting book", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
