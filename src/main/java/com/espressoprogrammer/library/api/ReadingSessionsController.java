package com.espressoprogrammer.library.api;

import com.espressoprogrammer.library.dto.Book;
import com.espressoprogrammer.library.dto.DateReadingSession;
import com.espressoprogrammer.library.dto.ReadingSession;
import com.espressoprogrammer.library.dto.ReadingSessionProgress;
import com.espressoprogrammer.library.persistence.BooksDao;
import com.espressoprogrammer.library.persistence.ReadingSessionsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
public class ReadingSessionsController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BooksDao booksDao;

    @Autowired
    private ReadingSessionsDao readingSessionsDao;

    @GetMapping(value = "/users/{user}/books/{bookUuid}/reading-sessions")
    public ResponseEntity<List<ReadingSession>> getUserReadingSessions(@PathVariable("user") String user,
                                                                       @PathVariable("bookUuid") String bookUuid)  {
        try {
            logger.debug("Look for reading sessions for user {}", user);

            List<ReadingSession> userReadingSessions = readingSessionsDao.getUserReadingSessions(user, bookUuid);
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

            List<ReadingSession> userBooks = readingSessionsDao.getUserReadingSessions(user, bookUuid);
            if(userBooks.isEmpty()) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(userBooks.get(0), HttpStatus.OK);
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

            List<ReadingSession> userReadingSessions = readingSessionsDao.getUserReadingSessions(user, bookUuid);
            if(!userReadingSessions.isEmpty()) {
                return new ResponseEntity(HttpStatus.FORBIDDEN);
            }

            ReadingSession createdReadingSession = readingSession;
            if(!CollectionUtils.isEmpty(createdReadingSession.getDateReadingSessions())) {
                List<DateReadingSession> createdDateReadingSessions = new ArrayList<>(createdReadingSession.getDateReadingSessions());
                createdDateReadingSessions.sort((drs1, drs2) -> drs1.getDate().compareTo(drs2.getDate()));
                createdReadingSession = new ReadingSession(readingSession.getUuid(),
                    readingSession.getBookUuid(),
                    readingSession.getDeadline(),
                    createdDateReadingSessions);
            }

            ReadingSession persistedReadingSession = readingSessionsDao.createUserReadingSession(user, bookUuid, createdReadingSession);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.LOCATION, String.format("/users/%s/books/%s/reading-sessions/%s",
                user,
                bookUuid,
                persistedReadingSession.getUuid()));
            return new ResponseEntity(persistedReadingSession, httpHeaders, HttpStatus.CREATED);
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

            Optional<ReadingSession> optionalReadingSession = readingSessionsDao.getUserReadingSession(user, bookUuid, uuid);
            if(!optionalReadingSession.isPresent()) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(optionalReadingSession.get(), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Error on looking for reading session", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value= "/users/{user}/books/{bookUuid}/reading-sessions/{uuid}")
    public ResponseEntity<Book> deleteUserReadingSession(@PathVariable("user") String user,
                                                         @PathVariable("bookUuid") String bookUuid,
                                                         @PathVariable("uuid") String uuid)  {
        try {
            logger.debug("Delete a reading session for user {} with uuid {} ", user, uuid);

            Optional<String> optionalReadingSession = readingSessionsDao.deleteUserReadingSession(user, bookUuid, uuid);
            if(!optionalReadingSession.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Error on looking for reading sessions", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/users/{user}/books/{bookUuid}/reading-sessions/{uuid}/date-reading-sessions")
    public ResponseEntity<List<DateReadingSession>> getDateReadingSessions(@PathVariable("user") String user,
                                                                           @PathVariable("bookUuid") String bookUuid,
                                                                           @PathVariable("uuid") String uuid)  {
        try {
            logger.debug("Look for date reading sessions for user {} with uuid {}", user, uuid);

            Optional<ReadingSession> optionalReadingSession = readingSessionsDao.getUserReadingSession(user, bookUuid, uuid);
            if(!optionalReadingSession.isPresent()) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(optionalReadingSession.get().getDateReadingSessions(), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Error on looking for reading sessions", ex);
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

            Optional<ReadingSession> optionalReadingSession = readingSessionsDao.getUserReadingSession(user, bookUuid, uuid);
            if(!optionalReadingSession.isPresent()) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            ReadingSession readingSession = optionalReadingSession.get();
            for(DateReadingSession existingDateReadingSession : readingSession.getDateReadingSessions()) {
                if(existingDateReadingSession.getDate().equals(dateReadingSession.getDate())) {
                    return new ResponseEntity(HttpStatus.FORBIDDEN);
                }
            }

            List<DateReadingSession> updatedDateReadingSessions = new ArrayList<>(readingSession.getDateReadingSessions());
            updatedDateReadingSessions.add(dateReadingSession);
            updatedDateReadingSessions.sort(Comparator.comparing(DateReadingSession::getDate));
            readingSessionsDao.updateUserReadingSession(user,
                bookUuid,
                uuid,
                new ReadingSession(readingSession.getUuid(),
                    readingSession.getBookUuid(),
                    readingSession.getDeadline(),
                    updatedDateReadingSessions));

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.LOCATION,
                String.format("/users/%s/reading-sessions/%s/date-reading-sessions/%s",
                    user, uuid, dateReadingSession.getDate()));
            return new ResponseEntity(dateReadingSession, httpHeaders, HttpStatus.CREATED);
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

            Optional<ReadingSession> optionalReadingSession = readingSessionsDao.getUserReadingSession(user, bookUuid, uuid);
            if(!optionalReadingSession.isPresent()) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            for(DateReadingSession dateReadingSession : optionalReadingSession.get().getDateReadingSessions()) {
                if(dateReadingSession.getDate().equals(date)) {
                    return new ResponseEntity<>(dateReadingSession, HttpStatus.OK);
                }
            }

            return new ResponseEntity(HttpStatus.NOT_FOUND);
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

            Optional<ReadingSession> optionalReadingSession = readingSessionsDao.getUserReadingSession(user, bookUuid, uuid);
            if(!optionalReadingSession.isPresent()) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            boolean update = false;
            List<DateReadingSession> updateDateReadingSessions = new ArrayList<>();
            ReadingSession existingReadingSession = optionalReadingSession.get();
            for(DateReadingSession existingDateReadingSession : existingReadingSession.getDateReadingSessions()) {
                if(existingDateReadingSession.getDate().equals(date)) {
                    update = true;
                    updateDateReadingSessions.add(new DateReadingSession(date,
                        dateReadingSession.getLastReadPage(),
                        dateReadingSession.getBookmark()));
                } else {
                    updateDateReadingSessions.add(existingDateReadingSession);
                }
            }

            if(update) {
                readingSessionsDao.updateUserReadingSession(user, bookUuid, uuid, new ReadingSession(existingReadingSession.getUuid(),
                    bookUuid,
                    existingReadingSession.getDeadline(),
                    updateDateReadingSessions));
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity(HttpStatus.NOT_FOUND);
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

            Optional<ReadingSession> optionalReadingSession = readingSessionsDao.getUserReadingSession(user, bookUuid, uuid);
            if(!optionalReadingSession.isPresent()) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            boolean delete = false;
            List<DateReadingSession> updateDateReadingSessions = new ArrayList<>();
            ReadingSession existingReadingSession = optionalReadingSession.get();
            for(DateReadingSession existingDateReadingSession : existingReadingSession.getDateReadingSessions()) {
                if(existingDateReadingSession.getDate().equals(date)) {
                    delete = true;
                } else {
                    updateDateReadingSessions.add(existingDateReadingSession);
                }
            }

            if(delete) {
                readingSessionsDao.updateUserReadingSession(user, bookUuid, uuid, new ReadingSession(existingReadingSession.getUuid(),
                    bookUuid,
                    existingReadingSession.getDeadline(),
                    updateDateReadingSessions));
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            logger.error("Error on looking for date reading session", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/users/{user}/books/{bookUuid}/reading-sessions/{uuid}/progress")
    public ResponseEntity<ReadingSessionProgress> getUserReadingSessionProgress(@PathVariable("user") String user,
                                                                                @PathVariable("bookUuid") String bookUuid,
                                                                                @PathVariable("uuid") String uuid)  {
        try {
            logger.debug("Look for reading session progress for user {} with uuid {} ", user, uuid);

            Optional<Book> optionalBook = booksDao.getUserBook(user, bookUuid);
            if(!optionalBook.isPresent()) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            Optional<ReadingSession> optionalReadingSession = readingSessionsDao.getUserReadingSession(user, bookUuid, uuid);
            if(!optionalReadingSession.isPresent()) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            ReadingSession readingSession = optionalReadingSession.get();
            List<DateReadingSession> dateReadingSessions = new ArrayList<>(readingSession.getDateReadingSessions());
            if(dateReadingSessions.isEmpty()) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            dateReadingSessions.sort(Comparator.comparing(DateReadingSession::getDate));

            DateReadingSession firstDateReadingSession = dateReadingSessions.get(0);
            LocalDate firstReadDate = LocalDate.parse(firstDateReadingSession.getDate());

            DateReadingSession lastDateReadingSession = dateReadingSessions.get(dateReadingSessions.size() - 1);
            LocalDate lastReadDate = LocalDate.parse(lastDateReadingSession.getDate());
            int lastReadPage = lastDateReadingSession.getLastReadPage();

            BigDecimal averagePagesPerDay = new BigDecimal(lastReadPage)
                    .divide(new BigDecimal(dateReadingSessions.size()), RoundingMode.HALF_UP);

            Book book = optionalBook.get();
            BigDecimal estimatedReadDaysLeft = new BigDecimal(book.getPages() - lastReadPage)
                    .divide(averagePagesPerDay, RoundingMode.HALF_UP);
            long readPeriodDays = ChronoUnit.DAYS.between(firstReadDate, lastReadDate) + 1;
            BigDecimal multiplyFactor = new BigDecimal(readPeriodDays)
                    .divide(new BigDecimal(dateReadingSessions.size()), RoundingMode.HALF_UP);
            BigDecimal estimatedDaysLeft = estimatedReadDaysLeft.multiply(multiplyFactor);
            BigDecimal readPercentage = new BigDecimal(lastReadPage)
                    .multiply(new BigDecimal(100))
                    .divide(new BigDecimal(book.getPages()), RoundingMode.HALF_UP);

            ReadingSessionProgress readingSessionProgress = new ReadingSessionProgress(lastReadPage,
                    book.getPages(),
                    readPercentage.intValue(),
                    averagePagesPerDay.intValue(),
                    estimatedReadDaysLeft.intValue(),
                    estimatedDaysLeft.intValue(),
                    LocalDate.now().plusDays(estimatedDaysLeft.intValue()).toString());

            return new ResponseEntity<>(readingSessionProgress, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Error on looking for reading session progress", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
