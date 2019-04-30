package com.espressoprogrammer.library.service;

import com.espressoprogrammer.library.dto.Book;
import com.espressoprogrammer.library.dto.DateReadingSession;
import com.espressoprogrammer.library.dto.ReadingSession;
import com.espressoprogrammer.library.dto.ReadingSessionProgress;
import com.espressoprogrammer.library.persistence.BooksDao;
import com.espressoprogrammer.library.persistence.ReadingSessionsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.springframework.util.StringUtils.isEmpty;

@Service
public class ReadingSessionsService {
    private static final String ISO_DATE_REGEXP = "[\\d]{4}-[\\d]{2}-[\\d]{2}";
    private static final String ISO_DATE_PATTERN = "yyyy-MM-dd";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BooksDao booksDao;

    @Autowired
    private ReadingSessionsDao readingSessionsDao;

    private DateTimeFormatter isoDateFormatter;

    @PostConstruct
    public void init() {
        isoDateFormatter = DateTimeFormatter.ofPattern(ISO_DATE_PATTERN);
    }

    public List<ReadingSession> getUserReadingSessions(String user, String bookUuid)  {
        logger.debug("Look for reading sessions for user {}", user);

        return readingSessionsDao.getUserReadingSessions(user, bookUuid);
    }

    public ReadingSession getUserCurrentReadingSession(String user, String bookUuid) throws BooksException, ReadingSessionsException {
        logger.debug("Look for current reading sessions for user {}", user);

        Optional<Book> optionalBook = booksDao.getUserBook(user, bookUuid);
        if(!optionalBook.isPresent()) {
            throw new BooksException(BooksException.Reason.BOOK_NOT_FOUND);
        }

        List<ReadingSession> userBooks = readingSessionsDao.getUserReadingSessions(user, bookUuid);
        if(userBooks.isEmpty()) {
            throw new ReadingSessionsException(ReadingSessionsException.Reason.READING_SESSION_NOT_FOUND);
        }
        return userBooks.get(0);
    }

    public ReadingSession createUserReadingSession(String user, String bookUuid, ReadingSession readingSession) throws BooksException, ReadingSessionsException {
        logger.debug("Add new reading session for user {}", user);

        Optional<Book> optionalBook = booksDao.getUserBook(user, bookUuid);
        if(!optionalBook.isPresent()) {
            throw new BooksException(BooksException.Reason.BOOK_NOT_FOUND);
        }

        List<ReadingSession> userReadingSessions = readingSessionsDao.getUserReadingSessions(user, bookUuid);
        if(!userReadingSessions.isEmpty()) {
            throw new ReadingSessionsException(ReadingSessionsException.Reason.READING_SESSION_ALREADY_EXISTS);
        }

        ReadingSession createdReadingSession = readingSession;
        if(!CollectionUtils.isEmpty(createdReadingSession.getDateReadingSessions())) {
            createdReadingSession = new ReadingSession(null,
                    bookUuid,
                    readingSession.getDeadline(),
                    Collections.emptyList());
        }

        return readingSessionsDao.createUserReadingSession(user, bookUuid, createdReadingSession);
    }

    public ReadingSession getUserReadingSession(String user, String bookUuid, String uuid) throws ReadingSessionsException {
        logger.debug("Look for reading session for user {} with uuid {} ", user, uuid);

        Optional<ReadingSession> optionalReadingSession = readingSessionsDao.getUserReadingSession(user, bookUuid, uuid);
        if(!optionalReadingSession.isPresent()) {
            throw new ReadingSessionsException(ReadingSessionsException.Reason.READING_SESSION_NOT_FOUND);
        }

        return optionalReadingSession.get();
    }

    public String deleteUserReadingSession(String user, String bookUuid, String uuid) throws ReadingSessionsException {
        logger.debug("Delete a reading session for user {} with uuid {} ", user, uuid);

        Optional<String> optionalUuid = readingSessionsDao.deleteUserReadingSession(user, bookUuid, uuid);
        if(!optionalUuid.isPresent()) {
            throw new ReadingSessionsException(ReadingSessionsException.Reason.READING_SESSION_NOT_FOUND);
        }

        return optionalUuid.get();
    }

    public DateReadingSession createDateReadingSession(String user, String bookUuid, String uuid, DateReadingSession dateReadingSession) throws ReadingSessionsException {
        logger.debug("Add new date reading session for user {} with uuid {} ", user, uuid);

        if(!isValidDateReadingSession(dateReadingSession)) {
            throw new ReadingSessionsException(ReadingSessionsException.Reason.DATE_READING_SESSION_INVALID);
        }

        Optional<ReadingSession> optionalReadingSession = readingSessionsDao.getUserReadingSession(user, bookUuid, uuid);
        if(!optionalReadingSession.isPresent()) {
            throw new ReadingSessionsException(ReadingSessionsException.Reason.READING_SESSION_NOT_FOUND);
        }

        ReadingSession readingSession = optionalReadingSession.get();
        for(DateReadingSession existingDateReadingSession : readingSession.getDateReadingSessions()) {
            if(existingDateReadingSession.getDate().equals(dateReadingSession.getDate())) {
                throw new ReadingSessionsException(ReadingSessionsException.Reason.DATE_READING_SESSION_ALREADY_EXISTS);
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

        return dateReadingSession;
    }

    public DateReadingSession getDateReadingSession(String user, String bookUuid, String uuid, String date) throws ReadingSessionsException {
        logger.debug("Look for date reading session for user {} with uuid {} and date {}", user, uuid, date);

        Optional<ReadingSession> optionalReadingSession = readingSessionsDao.getUserReadingSession(user, bookUuid, uuid);
        if(!optionalReadingSession.isPresent()) {
            throw new ReadingSessionsException(ReadingSessionsException.Reason.READING_SESSION_NOT_FOUND);
        }

        for(DateReadingSession dateReadingSession : optionalReadingSession.get().getDateReadingSessions()) {
            if(dateReadingSession.getDate().equals(date)) {
                return dateReadingSession;
            }
        }

        throw new ReadingSessionsException(ReadingSessionsException.Reason.DATE_READING_SESSION_NOT_FOUND);
    }

    public String updateDateReadingSession(String user, String bookUuid, String uuid, String date, DateReadingSession dateReadingSession) throws ReadingSessionsException {
        logger.debug("Update date reading session for user {} with uuid {} and date {}", user, uuid, date);

        if(!isValidDateReadingSession(dateReadingSession)) {
            throw new ReadingSessionsException(ReadingSessionsException.Reason.DATE_READING_SESSION_INVALID);
        }

        Optional<ReadingSession> optionalReadingSession = readingSessionsDao.getUserReadingSession(user, bookUuid, uuid);
        if(!optionalReadingSession.isPresent()) {
            throw new ReadingSessionsException(ReadingSessionsException.Reason.READING_SESSION_NOT_FOUND);
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
            return date;
        }

        throw new ReadingSessionsException(ReadingSessionsException.Reason.DATE_READING_SESSION_NOT_FOUND);
    }


    public String deleteDateReadingSession(String user, String bookUuid, String uuid, String date) throws ReadingSessionsException {
        logger.debug("Delete date reading session for user {} with uuid {} and date {}", user, uuid, date);

        Optional<ReadingSession> optionalReadingSession = readingSessionsDao.getUserReadingSession(user, bookUuid, uuid);
        if(!optionalReadingSession.isPresent()) {
            throw new ReadingSessionsException(ReadingSessionsException.Reason.READING_SESSION_NOT_FOUND);
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
            return date;
        }

        throw new ReadingSessionsException(ReadingSessionsException.Reason.DATE_READING_SESSION_NOT_FOUND);
    }

    public ReadingSessionProgress getUserReadingSessionProgress(String user, String bookUuid, String uuid) throws BooksException, ReadingSessionsException {
        logger.debug("Look for reading session progress for user {} with uuid {} ", user, uuid);

        Optional<Book> optionalBook = booksDao.getUserBook(user, bookUuid);
        if(!optionalBook.isPresent()) {
            throw new BooksException(BooksException.Reason.BOOK_NOT_FOUND);
        }

        Optional<ReadingSession> optionalReadingSession = readingSessionsDao.getUserReadingSession(user, bookUuid, uuid);
        if(!optionalReadingSession.isPresent()) {
            throw new ReadingSessionsException(ReadingSessionsException.Reason.READING_SESSION_NOT_FOUND);
        }

        ReadingSession readingSession = optionalReadingSession.get();
        List<DateReadingSession> dateReadingSessions = new ArrayList<>(readingSession.getDateReadingSessions());
        if(dateReadingSessions.isEmpty()) {
            throw new ReadingSessionsException(ReadingSessionsException.Reason.DATE_READING_SESSION_NOT_FOUND);
        }

        dateReadingSessions.sort(Comparator.comparing(DateReadingSession::getDate));
        DateReadingSession firstDateReadingSession = dateReadingSessions.get(0);
        LocalDate firstReadDate = LocalDate.parse(firstDateReadingSession.getDate());
        DateReadingSession lastDateReadingSession = dateReadingSessions.get(dateReadingSessions.size() - 1);
        LocalDate lastReadDate = LocalDate.parse(lastDateReadingSession.getDate());

        dateReadingSessions.sort(Comparator.comparing(DateReadingSession::getLastReadPage));
        DateReadingSession lastReadPageSession = dateReadingSessions.get(dateReadingSessions.size() - 1);
        int lastReadPage = lastReadPageSession.getLastReadPage();

        BigDecimal averagePagesPerDay = new BigDecimal(lastReadPage)
                .divide(new BigDecimal(dateReadingSessions.size()), RoundingMode.HALF_UP);

        Book book = optionalBook.get();

        BigDecimal readPercentage = new BigDecimal(lastReadPage)
            .multiply(new BigDecimal(100))
            .divide(new BigDecimal(book.getPages()), RoundingMode.HALF_UP);

        int remainingPages = book.getPages() - lastReadPage;
        if(remainingPages > 0 && remainingPages < averagePagesPerDay.intValue()) {
            remainingPages = averagePagesPerDay.intValue();
        }
        BigDecimal estimatedReadDaysLeft = new BigDecimal(remainingPages)
                .divide(averagePagesPerDay, RoundingMode.HALF_UP);

        long readPeriodDays = ChronoUnit.DAYS.between(firstReadDate, lastReadDate) + 1;
        BigDecimal multiplyFactor = new BigDecimal(readPeriodDays)
                .divide(new BigDecimal(dateReadingSessions.size()), RoundingMode.HALF_UP);
        BigDecimal estimatedDaysLeft = estimatedReadDaysLeft.multiply(multiplyFactor);
        String estimatedFinishDate = estimatedReadDaysLeft.intValue() > 0
                ? LocalDate.now().plusDays(estimatedDaysLeft.intValue()).toString()
                : null;

        ReadingSessionProgress readingSessionProgress = new ReadingSessionProgress(book.getUuid(),
                lastReadPage,
                book.getPages(),
                readPercentage.intValue(),
                averagePagesPerDay.intValue(),
                estimatedReadDaysLeft.intValue(),
                estimatedDaysLeft.intValue(),
                estimatedFinishDate,
                readingSession.getDeadline());

        return readingSessionProgress;
    }

    private boolean isValidDateReadingSession(DateReadingSession dateReadingSession) {
        return !isEmpty(dateReadingSession.getDate())
                && Pattern.matches(ISO_DATE_REGEXP, dateReadingSession.getDate())
                && isISODate(dateReadingSession.getDate())
                && null != dateReadingSession.getLastReadPage()
                && dateReadingSession.getLastReadPage() > 0;
    }

    private boolean isISODate(String date) {
        try {
            LocalDate.parse(date, isoDateFormatter);
        } catch(Exception ex) {
            return false;
        }
        return true;
    }

}
