package com.espressoprogrammer.library.service;

import com.espressoprogrammer.library.dto.Book;
import com.espressoprogrammer.library.dto.ReadingSession;
import com.espressoprogrammer.library.persistence.ReadingSessionsDao;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

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

    @PostMapping(value = "/users/{user}/reading-sessions")
    public ResponseEntity createUserReadingSession(@PathVariable("user") String user,
                                                   @RequestBody ReadingSession readingSession)  {
        try {
            logger.debug("Add new reading session for user {}", user);

            String uuid = readingSessionsDao.createUserReadingSession(user, readingSession);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.LOCATION, String.format("/users/%s/reading-sessions/%s", user, uuid));
            return new ResponseEntity(httpHeaders, HttpStatus.CREATED);
        } catch (Exception ex) {
            logger.error("Error on adding new reading session", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/users/{user}/reading-sessions/{uuid}")
    public ResponseEntity<ReadingSession> getUseReadingSession(@PathVariable("user") String user,
                                                               @PathVariable("uuid") String uuid)  {
        try {
            logger.debug("Look for reading session with uuid {} for user {}", uuid, user);

            Optional<ReadingSession> optionalReadingSession = readingSessionsDao.getUserReadingSession(user, uuid);
            if(optionalReadingSession.isPresent()) {
                return new ResponseEntity<>(optionalReadingSession.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            logger.error("Error on looking for books", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
