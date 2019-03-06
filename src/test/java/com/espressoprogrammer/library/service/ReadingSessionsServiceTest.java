package com.espressoprogrammer.library.service;

import com.espressoprogrammer.library.dto.Book;
import com.espressoprogrammer.library.dto.DateReadingSession;
import com.espressoprogrammer.library.dto.ReadingSession;
import com.espressoprogrammer.library.persistence.BooksDao;
import com.espressoprogrammer.library.persistence.ReadingSessionsDao;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.espressoprogrammer.library.LibraryTestUtil.getTestBook;
import static com.espressoprogrammer.library.LibraryTestUtil.getTestDateReadingSession;
import static com.espressoprogrammer.library.LibraryTestUtil.getTestReadingSession;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadingSessionsServiceTest {
    private static final String JOHN_DOE_USER = "johndoe";
    private static final String BOOK_UUID = "1e4014b1-a551-4310-9f30-590c3140b695";

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    @MockBean
    private BooksDao booksDao;

    @MockBean
    private ReadingSessionsDao readingSessionsDao;

    @Autowired
    private ReadingSessionsService readingSessionsService;

    @Test
    public void getUserReadingSessions() throws Exception {
        List<ReadingSession> readingSessions = new ArrayList<>();
        readingSessions.add(getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json"));
        when(readingSessionsDao.getUserReadingSessions(JOHN_DOE_USER, BOOK_UUID)).thenReturn(readingSessions);

        List<ReadingSession> actualReadingSessions = readingSessionsService.getUserReadingSessions(JOHN_DOE_USER, BOOK_UUID);
        assertThat(actualReadingSessions).isEqualTo(readingSessions);
    }

    @Test
    public void getUserCurrentReadingSession() throws Exception {
        Book book = getTestBook(BOOK_UUID + ".json");
        when(booksDao.getUserBook(JOHN_DOE_USER, BOOK_UUID)).thenReturn(Optional.of(book));

        ArrayList<ReadingSession> readingSessions = new ArrayList<>();
        readingSessions.add(getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json"));
        when(readingSessionsDao.getUserReadingSessions(JOHN_DOE_USER, BOOK_UUID)).thenReturn(readingSessions);

        List<ReadingSession> actualReadingSessions = readingSessionsService.getUserReadingSessions(JOHN_DOE_USER, BOOK_UUID);
        assertThat(actualReadingSessions).isEqualTo(readingSessions);
    }

    @Test
    public void getMissingUserCurrentReadingSession() throws Exception {
        when(booksDao.getUserBook(JOHN_DOE_USER, BOOK_UUID)).thenReturn(Optional.empty());

        try {
            readingSessionsService.getUserCurrentReadingSession(JOHN_DOE_USER, BOOK_UUID);
            fail("It should fail with " + BooksException.Reason.BOOK_NOT_FOUND);
        } catch(BooksException ex) {
            assertThat(ex.getReason()).isEqualTo(BooksException.Reason.BOOK_NOT_FOUND);
        } catch (Exception ex) {
            fail("It should fail with " + BooksException.Reason.BOOK_NOT_FOUND);
        }
    }

    @Test
    public void createUserReadingSession() throws Exception {
        ReadingSession readingSession = getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695-request.json");
        when(readingSessionsDao.createUserReadingSession(JOHN_DOE_USER, BOOK_UUID, readingSession)).thenReturn(getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json"));

        ReadingSession actualReadingSession = readingSessionsService.createUserReadingSession(JOHN_DOE_USER, BOOK_UUID, readingSession);
        assertThat(actualReadingSession).isEqualTo(getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json"));
    }

    @Test
    public void createAdditionalUserReadingSession() throws Exception {
        when(readingSessionsDao.getUserReadingSessions(JOHN_DOE_USER, BOOK_UUID)).thenReturn(Arrays.asList(getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json")));

        try {
            readingSessionsService.createUserReadingSession(JOHN_DOE_USER, BOOK_UUID, getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695-request.json"));
            fail("It should fail with " + ReadingSessionsException.Reason.READING_SESSION_ALREADY_EXISTS);
        } catch(ReadingSessionsException ex) {
            assertThat(ex.getReason()).isEqualTo(ReadingSessionsException.Reason.READING_SESSION_ALREADY_EXISTS);
        } catch (Exception ex) {
            fail("It should fail with " + ReadingSessionsException.Reason.READING_SESSION_ALREADY_EXISTS);
        }
    }

    @Test
    public void getUserReadingSession() throws Exception {
        String uuid = "1e4014b1-a551-4310-9f30-590c3140b695";
        ReadingSession readingSession = getTestReadingSession(uuid + ".json");
        when(readingSessionsDao.getUserReadingSession(JOHN_DOE_USER, BOOK_UUID, uuid)).thenReturn(Optional.of(readingSession));

        ReadingSession actualReadingSession = readingSessionsService.getUserReadingSession(JOHN_DOE_USER, BOOK_UUID, uuid);
        assertThat(actualReadingSession).isEqualTo(readingSession);
    }

    @Test
    public void getMissingUserReadingSession() throws Exception {
        String uuid = "missing-uuid-1";
        when(readingSessionsDao.getUserReadingSession(JOHN_DOE_USER, BOOK_UUID, uuid)).thenReturn(Optional.empty());

        try {
            readingSessionsService.getUserReadingSession(JOHN_DOE_USER, BOOK_UUID, uuid);
            fail("It should fail with " + ReadingSessionsException.Reason.READING_SESSION_NOT_FOUND);
        } catch(ReadingSessionsException ex) {
            assertThat(ex.getReason()).isEqualTo(ReadingSessionsException.Reason.READING_SESSION_NOT_FOUND);
        } catch (Exception ex) {
            fail("It should fail with " + ReadingSessionsException.Reason.READING_SESSION_NOT_FOUND);
        }
    }

    @Test
    public void deleteUserReadingSession() throws Exception {
        ReadingSession readingSession = getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(readingSessionsDao.deleteUserReadingSession(JOHN_DOE_USER, BOOK_UUID, readingSession.getUuid())).thenReturn(Optional.of(readingSession.getUuid()));

        String actualUuid = readingSessionsService.deleteUserReadingSession(JOHN_DOE_USER, BOOK_UUID, readingSession.getUuid());
        assertThat(actualUuid).isEqualTo(readingSession.getUuid());
    }

    @Test
    public void deleteMissingUserReadingSession() throws Exception {
        ReadingSession readingSession = getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(readingSessionsDao.deleteUserReadingSession(JOHN_DOE_USER, BOOK_UUID, readingSession.getUuid())).thenReturn(Optional.empty());

        try {
            readingSessionsService.deleteUserReadingSession(JOHN_DOE_USER, BOOK_UUID, readingSession.getUuid());
            fail("It should fail with " + ReadingSessionsException.Reason.READING_SESSION_NOT_FOUND);
        } catch(ReadingSessionsException ex) {
            assertThat(ex.getReason()).isEqualTo(ReadingSessionsException.Reason.READING_SESSION_NOT_FOUND);
        } catch (Exception ex) {
            fail("It should fail with " + ReadingSessionsException.Reason.READING_SESSION_NOT_FOUND);
        }
    }

    @Test
    public void createDateReadingSession() throws Exception {
        ReadingSession readingSession = getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(readingSessionsDao.getUserReadingSession(JOHN_DOE_USER, BOOK_UUID,"1e4014b1-a551-4310-9f30-590c3140b695")).thenReturn(Optional.of(readingSession));

        DateReadingSession dateReadingSession = getTestDateReadingSession("1e4014b1-a551-4310-9f30-590c3140b695-new-date-reading-session.json");
        DateReadingSession actualReadingSession = readingSessionsService.createDateReadingSession(JOHN_DOE_USER, BOOK_UUID, "1e4014b1-a551-4310-9f30-590c3140b695", dateReadingSession);
        assertThat(actualReadingSession).isEqualTo(dateReadingSession);
    }

    @Test
    public void createDateReadingSessionExistingDate() throws Exception {
        ReadingSession readingSession = getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(readingSessionsDao.getUserReadingSession(JOHN_DOE_USER,
            BOOK_UUID, "1e4014b1-a551-4310-9f30-590c3140b695")).thenReturn(Optional.of(readingSession));

        try {
            DateReadingSession dateReadingSession = getTestDateReadingSession("1e4014b1-a551-4310-9f30-590c3140b695-existing-date-reading-session.json");
            readingSessionsService.createDateReadingSession(JOHN_DOE_USER, BOOK_UUID, "1e4014b1-a551-4310-9f30-590c3140b695", dateReadingSession);
            fail("It should fail with " + ReadingSessionsException.Reason.DATE_READING_SESSION_ALREADY_EXISTS);
        } catch(ReadingSessionsException ex) {
            assertThat(ex.getReason()).isEqualTo(ReadingSessionsException.Reason.DATE_READING_SESSION_ALREADY_EXISTS);
        } catch (Exception ex) {
            fail("It should fail with " + ReadingSessionsException.Reason.DATE_READING_SESSION_ALREADY_EXISTS);
        }
    }

    @Test
    public void getDateReadingSession() throws Exception {
        String uuid = "1e4014b1-a551-4310-9f30-590c3140b695";
        ReadingSession readingSession = getTestReadingSession(uuid + ".json");
        when(readingSessionsDao.getUserReadingSession(JOHN_DOE_USER, BOOK_UUID, uuid)).thenReturn(Optional.of(readingSession));
        String date = "2017-01-01";

        DateReadingSession actualDateReadingSession = readingSessionsService.getDateReadingSession(JOHN_DOE_USER, BOOK_UUID, uuid, date);
        assertThat(actualDateReadingSession).isEqualTo(readingSession.getDateReadingSessions().get(0));
    }

    @Test
    public void getMissingDateReadingSession() throws Exception {
        String uuid = "1e4014b1-a551-4310-9f30-590c3140b695";
        ReadingSession readingSession = getTestReadingSession(uuid + ".json");
        when(readingSessionsDao.getUserReadingSession(JOHN_DOE_USER, BOOK_UUID, uuid)).thenReturn(Optional.of(readingSession));
        String date = "2017-01-02";

        try {
            readingSessionsService.getDateReadingSession(JOHN_DOE_USER, BOOK_UUID, "1e4014b1-a551-4310-9f30-590c3140b695", date);
            fail("It should fail with " + ReadingSessionsException.Reason.DATE_READING_SESSION_NOT_FOUND);
        } catch(ReadingSessionsException ex) {
            assertThat(ex.getReason()).isEqualTo(ReadingSessionsException.Reason.DATE_READING_SESSION_NOT_FOUND);
        } catch (Exception ex) {
            fail("It should fail with " + ReadingSessionsException.Reason.DATE_READING_SESSION_NOT_FOUND);
        }
    }

    @Test
    public void updateDateReadingSession() throws Exception {
        ReadingSession readingSession = getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(readingSessionsDao.getUserReadingSession(JOHN_DOE_USER, BOOK_UUID, "1e4014b1-a551-4310-9f30-590c3140b695")).thenReturn(Optional.of(readingSession));

        String date = "2017-01-01";
        DateReadingSession dateReadingSession = getTestDateReadingSession("1e4014b1-a551-4310-9f30-590c3140b695-update-date-reading-session-request.json");
        String actualDate = readingSessionsService.updateDateReadingSession(JOHN_DOE_USER, BOOK_UUID, "1e4014b1-a551-4310-9f30-590c3140b695", date, dateReadingSession);
        assertThat(actualDate).isEqualTo(date);
    }

    @Test
    public void updateMissingDateReadingSession() throws Exception {
        ReadingSession readingSession = getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(readingSessionsDao.getUserReadingSession(JOHN_DOE_USER,
            BOOK_UUID, "1e4014b1-a551-4310-9f30-590c3140b695")).thenReturn(Optional.of(readingSession));

        String date = "2017-01-02";
        try {
            DateReadingSession dateReadingSession = getTestDateReadingSession("1e4014b1-a551-4310-9f30-590c3140b695-update-date-reading-session-request.json");
            readingSessionsService.updateDateReadingSession(JOHN_DOE_USER, BOOK_UUID, "1e4014b1-a551-4310-9f30-590c3140b695", date, dateReadingSession);
            fail("It should fail with " + ReadingSessionsException.Reason.DATE_READING_SESSION_NOT_FOUND);
        } catch(ReadingSessionsException ex) {
            assertThat(ex.getReason()).isEqualTo(ReadingSessionsException.Reason.DATE_READING_SESSION_NOT_FOUND);
        } catch (Exception ex) {
            fail("It should fail with " + ReadingSessionsException.Reason.DATE_READING_SESSION_NOT_FOUND);
        }
    }

    @Test
    public void deleteDateReadingSession() throws Exception {
        ReadingSession readingSession = getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(readingSessionsDao.getUserReadingSession(JOHN_DOE_USER,
            BOOK_UUID, "1e4014b1-a551-4310-9f30-590c3140b695")).thenReturn(Optional.of(readingSession));

        String date = "2017-01-01";
        String actualDate = readingSessionsService.deleteDateReadingSession(JOHN_DOE_USER, BOOK_UUID, "1e4014b1-a551-4310-9f30-590c3140b695", date);
        assertThat(actualDate).isEqualTo(date);
    }

    @Test
    public void deleteMissingDateReadingSession() throws Exception {
        ReadingSession readingSession = getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(readingSessionsDao.getUserReadingSession(JOHN_DOE_USER,
            BOOK_UUID, "1e4014b1-a551-4310-9f30-590c3140b695")).thenReturn(Optional.of(readingSession));

        String date = "2017-01-02";
        try {
            readingSessionsService.deleteDateReadingSession(JOHN_DOE_USER, BOOK_UUID, "1e4014b1-a551-4310-9f30-590c3140b695", date);
            fail("It should fail with " + ReadingSessionsException.Reason.DATE_READING_SESSION_NOT_FOUND);
        } catch(ReadingSessionsException ex) {
            assertThat(ex.getReason()).isEqualTo(ReadingSessionsException.Reason.DATE_READING_SESSION_NOT_FOUND);
        } catch (Exception ex) {
            fail("It should fail with " + ReadingSessionsException.Reason.DATE_READING_SESSION_NOT_FOUND);
        }
    }
}
