package com.espressoprogrammer.library.service;

import com.espressoprogrammer.library.dto.ReadingSession;
import com.espressoprogrammer.library.persistence.ReadingSessionsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReadingSessionsController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ReadingSessionsDao readingSessionsDao;

    @GetMapping(value = "/users/{user}/reading-sessions")
    public ResponseEntity<List<ReadingSession>> getUserReadingSessions(@PathVariable("user") String user)  {
        try {
            logger.debug("Look for reading sessions for user {}", user);

            List<ReadingSession> userBooks = readingSessionsDao.getUserReadingSessions(user);
            return new ResponseEntity<>(userBooks, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Error on looking for reading sessions", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
