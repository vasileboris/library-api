package com.espressoprogrammer.library.service;

import com.espressoprogrammer.library.dto.Book;
import com.espressoprogrammer.library.persistence.BooksDao;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class BooksController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BooksDao booksDao;

    @GetMapping(value = "/users/{user}/books")
    public ResponseEntity<List<Book>> getUserBooks(@PathVariable("user") String user)  {
        try {
            logger.debug("Look for books for user {}", user);

            List<Book> userBooks = booksDao.getUserBooks(user);
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

            if(hasUseTheBook(user, book)) {
                return new ResponseEntity(HttpStatus.FORBIDDEN);
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
            logger.debug("Look for book with uuid {} for user {}", uuid, user);

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
            logger.debug("Update book with uuid {} for user {}", uuid, user);

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
            logger.debug("Delete book with uuid {} for user {}", uuid, user);

            Optional<Book> optionalBook = booksDao.getUserBook(user, uuid);
            if(!optionalBook.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            booksDao.deleteUserBook(user, uuid);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Error on looking for books", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean hasUseTheBook(@PathVariable("user") String user, @RequestBody Book book) {
        List<Book> existingBooks = booksDao.getUserBooks(user);
        for(Book existingBook : existingBooks) {
            if((existingBook.getIsbn10() != null && existingBook.getIsbn10().equals(book.getIsbn10()))
                || (existingBook.getIsbn13() != null && existingBook.getIsbn13().equals(book.getIsbn13()))) {
                return true;
            }
        }
        return false;
    }
}
