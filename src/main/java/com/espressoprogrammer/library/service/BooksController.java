package com.espressoprogrammer.library.service;

import com.espressoprogrammer.library.dto.Book;
import com.espressoprogrammer.library.persistence.BooksDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BooksController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BooksDao booksDao;

    @GetMapping(value = "/users/{user}/books")
    ResponseEntity<List<Book>> getUserBooks(@PathVariable("user") String user)  {
        try {
            logger.debug("Looking for books for user {}", user);

            List<Book> userBooks = booksDao.getUserBooks(user);
            return new ResponseEntity<>(userBooks, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Error on looking for books", ex);
            throw new InternalServerError();
        }
    }

    @PostMapping(value = "/users/{user}/books")
    ResponseEntity createUserBook(@PathVariable("user") String user, @RequestBody Book book)  {
        try {
            logger.debug("Adding new book for user {}", user);

            String isbn = booksDao.createBook(user, book);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.LOCATION, String.format("/users/%s/books/%s", user, isbn));
            return new ResponseEntity(httpHeaders, HttpStatus.CREATED);
        } catch (Exception ex) {
            logger.error("Error on adding new book", ex);
            throw new InternalServerError();
        }
    }

}
