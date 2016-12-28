package com.espressoprogrammer.library.service;

import com.espressoprogrammer.library.dto.Book;
import com.espressoprogrammer.library.persistence.BooksDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BooksController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BooksDao booksDao;

    @RequestMapping(value = "/users/{user}/books", method= RequestMethod.GET)
    List<Book> getUserBooks(@PathVariable("user") String user)  {
        try {
            logger.debug("Looking for books for user {}", user);

            List<Book> userBooks = booksDao.getUserBooks(user);
            return userBooks;
        } catch (Exception ex) {
            logger.error("Error on looking for books", ex);
            throw new InternalServerError();
        }
    }

}
