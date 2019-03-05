package com.espressoprogrammer.library.api;

import com.espressoprogrammer.library.dto.DateReadingSession;
import com.espressoprogrammer.library.dto.ReadingSession;
import com.espressoprogrammer.library.dto.ReadingSessionProgress;
import com.espressoprogrammer.library.service.BooksException;
import com.espressoprogrammer.library.service.ReadingSessionsException;
import com.espressoprogrammer.library.service.ReadingSessionsService;
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

@RestController
public class ReadingSessionsController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ReadingSessionsService readingSessionsService;

    @Autowired
    private HttpStatusConverter httpStatusConverter;

    @GetMapping(value = "/users/{user}/books/{bookUuid}/reading-sessions")
    public ResponseEntity<List<ReadingSession>> getUserReadingSessions(@PathVariable("user") String user,
                                                                       @PathVariable("bookUuid") String bookUuid)  {
        try {
            logger.debug("Look for reading sessions for user {}", user);

            List<ReadingSession> userReadingSessions = readingSessionsService.getUserReadingSessions(user, bookUuid);
            return new ResponseEntity<>(userReadingSessions, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Error on looking for reading sessions", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/users/{user}/books/{bookUuid}/current-reading-session")
    public ResponseEntity<ReadingSession> getUserCurrentReadingSession(@PathVariable("user") String user,
                                                                       @PathVariable("bookUuid") String bookUuid)  {
        try {
            logger.debug("Look for current reading sessions for user {}", user);

            ReadingSession currentReadingSession = readingSessionsService.getUserCurrentReadingSession(user, bookUuid);
            return new ResponseEntity<>(currentReadingSession, HttpStatus.OK);
        } catch (BooksException ex) {
            logger.error("Error on looking for reading sessions", ex);
            return new ResponseEntity(httpStatusConverter.from(ex));
        } catch (ReadingSessionsException ex) {
            logger.error("Error on looking for reading sessions", ex);
            return new ResponseEntity(httpStatusConverter.from(ex));
        } catch (Exception ex) {
            logger.error("Error on looking for reading sessions", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/users/{user}/books/{bookUuid}/reading-sessions")
    public ResponseEntity<ReadingSession> createUserReadingSession(@PathVariable("user") String user,
                                                                   @PathVariable("bookUuid") String bookUuid,
                                                                   @RequestBody ReadingSession readingSession)  {
        try {
            logger.debug("Add new reading session for user {}", user);

            ReadingSession persistedReadingSession = readingSessionsService.createUserReadingSession(user, bookUuid, readingSession);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.LOCATION, String.format("/users/%s/books/%s/reading-sessions/%s",
                user,
                bookUuid,
                persistedReadingSession.getUuid()));
            return new ResponseEntity(persistedReadingSession, httpHeaders, HttpStatus.CREATED);
        } catch (ReadingSessionsException ex) {
            logger.error("Error on adding new reading session", ex);
            return new ResponseEntity(httpStatusConverter.from(ex));
        } catch (Exception ex) {
            logger.error("Error on adding new reading session", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/users/{user}/books/{bookUuid}/reading-sessions/{uuid}")
    public ResponseEntity<ReadingSession> getUserReadingSession(@PathVariable("user") String user,
                                                                @PathVariable("bookUuid") String bookUuid,
                                                                @PathVariable("uuid") String uuid)  {
        try {
            logger.debug("Look for reading session for user {} with uuid {} ", user, uuid);

            ReadingSession readingSession = readingSessionsService.getUserReadingSession(user, bookUuid, uuid);
            return new ResponseEntity(readingSession, HttpStatus.OK);
        } catch (ReadingSessionsException ex) {
            logger.error("Error on looking for reading session", ex);
            return new ResponseEntity(httpStatusConverter.from(ex));
        } catch (Exception ex) {
            logger.error("Error on looking for reading session", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value= "/users/{user}/books/{bookUuid}/reading-sessions/{uuid}")
    public ResponseEntity deleteUserReadingSession(@PathVariable("user") String user,
                                                   @PathVariable("bookUuid") String bookUuid,
                                                   @PathVariable("uuid") String uuid)  {
        try {
            logger.debug("Delete a reading session for user {} with uuid {} ", user, uuid);

            readingSessionsService.deleteUserReadingSession(user, bookUuid, uuid);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ReadingSessionsException ex) {
            logger.error("Error on deleting reading sessions", ex);
            return new ResponseEntity(httpStatusConverter.from(ex));
        } catch (Exception ex) {
            logger.error("Error on deleting reading sessions", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/users/{user}/books/{bookUuid}/reading-sessions/{uuid}/date-reading-sessions")
    public ResponseEntity<List<DateReadingSession>> getDateReadingSessions(@PathVariable("user") String user,
                                                                           @PathVariable("bookUuid") String bookUuid,
                                                                           @PathVariable("uuid") String uuid)  {
        try {
            logger.debug("Look for date reading sessions for user {} with uuid {}", user, uuid);

            ReadingSession optionalReadingSession = readingSessionsService.getUserReadingSession(user, bookUuid, uuid);
            return new ResponseEntity<>(optionalReadingSession.getDateReadingSessions(), HttpStatus.OK);
        } catch (ReadingSessionsException ex) {
            logger.error("Error on looking for date reading sessions", ex);
            return new ResponseEntity(httpStatusConverter.from(ex));
        } catch (Exception ex) {
            logger.error("Error on looking for date reading sessions", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/users/{user}/books/{bookUuid}/reading-sessions/{uuid}/date-reading-sessions")
    public ResponseEntity<DateReadingSession> createDateReadingSession(@PathVariable("user") String user,
                                                   @PathVariable("bookUuid") String bookUuid,
                                                   @PathVariable("uuid") String uuid,
                                                   @RequestBody DateReadingSession dateReadingSession)  {
        try {
            logger.debug("Add new date reading session for user {} with uuid {} ", user, uuid);

            DateReadingSession persistedDateReadingSession = readingSessionsService.createDateReadingSession(user, bookUuid, uuid, dateReadingSession);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.LOCATION,
                String.format("/users/%s/reading-sessions/%s/date-reading-sessions/%s",
                    user, uuid, dateReadingSession.getDate()));
            return new ResponseEntity(persistedDateReadingSession, httpHeaders, HttpStatus.CREATED);
        } catch (ReadingSessionsException ex) {
            logger.error("Error on adding new date reading session", ex);
            return new ResponseEntity(httpStatusConverter.from(ex));
        } catch (Exception ex) {
            logger.error("Error on adding new date reading session", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/users/{user}/books/{bookUuid}/reading-sessions/{uuid}/date-reading-sessions/{date}")
    public ResponseEntity<DateReadingSession> getDateReadingSession(@PathVariable("user") String user,
                                                                    @PathVariable("bookUuid") String bookUuid,
                                                                    @PathVariable("uuid") String uuid,
                                                                    @PathVariable("date") String date)  {
        try {
            logger.debug("Look for date reading session for user {} with uuid {} and date {}", user, uuid, date);

            DateReadingSession dateReadingSession = readingSessionsService.getDateReadingSession(user, bookUuid, uuid, date);
            return new ResponseEntity<>(dateReadingSession, HttpStatus.OK);
        } catch (ReadingSessionsException ex) {
            logger.error("Error on looking for date reading session", ex);
            return new ResponseEntity(httpStatusConverter.from(ex));
       } catch (Exception ex) {
            logger.error("Error on looking for date reading session", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/users/{user}/books/{bookUuid}/reading-sessions/{uuid}/date-reading-sessions/{date}")
    public ResponseEntity updateDateReadingSession(@PathVariable("user") String user,
                                                   @PathVariable("bookUuid") String bookUuid,
                                                   @PathVariable("uuid") String uuid,
                                                   @PathVariable("date") String date,
                                                   @RequestBody DateReadingSession dateReadingSession)  {
        try {
            logger.debug("Update date reading session for user {} with uuid {} and date {}", user, uuid, date);

            readingSessionsService.updateDateReadingSession(user, bookUuid, uuid, date, dateReadingSession);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ReadingSessionsException ex) {
            logger.error("Error on looking for date reading session", ex);
            return new ResponseEntity(httpStatusConverter.from(ex));
        } catch (Exception ex) {
            logger.error("Error on looking for date reading session", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/users/{user}/books/{bookUuid}/reading-sessions/{uuid}/date-reading-sessions/{date}")
    public ResponseEntity deleteDateReadingSession(@PathVariable("user") String user,
                                                   @PathVariable("bookUuid") String bookUuid,
                                                   @PathVariable("uuid") String uuid,
                                                   @PathVariable("date") String date) {
        try {
            logger.debug("Delete date reading session for user {} with uuid {} and date {}", user, uuid, date);

            readingSessionsService.deleteDateReadingSession(user, bookUuid, uuid, date);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ReadingSessionsException ex) {
            logger.error("Error on deleting date reading session", ex);
            return new ResponseEntity(httpStatusConverter.from(ex));
        } catch (Exception ex) {
            logger.error("Error on deleting date reading session", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/users/{user}/books/{bookUuid}/reading-sessions/{uuid}/progress")
    public ResponseEntity<ReadingSessionProgress> getUserReadingSessionProgress(@PathVariable("user") String user,
                                                                                @PathVariable("bookUuid") String bookUuid,
                                                                                @PathVariable("uuid") String uuid)  {
        try {
            logger.debug("Look for reading session progress for user {} with uuid {} ", user, uuid);

            ReadingSessionProgress readingSessionProgress = readingSessionsService.getUserReadingSessionProgress(user, bookUuid, uuid);
            return new ResponseEntity<>(readingSessionProgress, HttpStatus.OK);
        } catch (ReadingSessionsException ex) {
            logger.error("Error on looking for reading session progress", ex);
            return new ResponseEntity(httpStatusConverter.from(ex));
        } catch (Exception ex) {
            logger.error("Error on looking for reading session progress", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
