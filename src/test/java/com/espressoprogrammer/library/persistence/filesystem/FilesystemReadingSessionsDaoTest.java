package com.espressoprogrammer.library.persistence.filesystem;

import com.espressoprogrammer.library.dto.DateReadingSession;
import com.espressoprogrammer.library.dto.ReadingSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.espressoprogrammer.library.LibraryTestUtil.copyReadingSession;
import static com.espressoprogrammer.library.LibraryTestUtil.getReadingSession;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FilesystemReadingSessionsDaoTest {
    private static final String TMPDIR = "java.io.tmpdir";
    private static final String JOHN_DOE_USER = "johndoe";
    private static final String BOOK_UUID = "book-uuid-1";

    @Mock
    private FilesystemConfiguration filesystemConfiguration;

    @InjectMocks
    private FilesystemReadingSessionsDao readingSessionsDao;

    @Before
    public void init() throws Exception {
        when(filesystemConfiguration.getLibraryFolder()).thenReturn(System.getProperty(TMPDIR)
            + "/library-"
            + System.nanoTime());
    }

    @After
    public void clear() throws Exception {
        deleteLibraryFolder();
    }

    @Test
    public void getNoUserReadingSessions() throws Exception {
        assertThat(readingSessionsDao.getUserReadingSessions(JOHN_DOE_USER, BOOK_UUID)).isEmpty();
    }

    @Test
    public void getOneUserReadingSession() throws Exception {
        createReadingSessionsFolder(JOHN_DOE_USER);
        copyReadingSession("uuid-1.json", getUserReadingSessionsFolder(JOHN_DOE_USER));

        List<ReadingSession> readingSessions = readingSessionsDao.getUserReadingSessions(JOHN_DOE_USER, BOOK_UUID);
        assertThat(readingSessions)
            .hasSize(1)
            .contains(
                new ReadingSession("uuid-1",
                    "book-uuid-1",
                    "2017-01-31",
                    Arrays.asList(
                        new DateReadingSession("2017-01-01", 101, "bookmark-101")
                    )
                )
            );
    }

    @Test
    public void getTwoUserReadingSessions() throws Exception {
        createReadingSessionsFolder(JOHN_DOE_USER);
        copyReadingSession("uuid-1.json", getUserReadingSessionsFolder(JOHN_DOE_USER));
        copyReadingSession("uuid-2.json", getUserReadingSessionsFolder(JOHN_DOE_USER));

        List<ReadingSession> readingSessions = readingSessionsDao.getUserReadingSessions(JOHN_DOE_USER, BOOK_UUID);
        assertThat(readingSessions)
            .hasSize(2)
            .contains(
                new ReadingSession("uuid-1",
                    "book-uuid-1",
                    "2017-01-31",
                    Arrays.asList(
                        new DateReadingSession("2017-01-01", 101, "bookmark-101")
                    )
                ),
                new ReadingSession("uuid-2",
                    "book-uuid-1",
                    "2017-01-31",
                    Arrays.asList(
                        new DateReadingSession("2017-02-01", 201, "bookmark-201"),
                        new DateReadingSession("2017-02-02", 202, "bookmark-202")
                    )
                )
            );
    }

    @Test
    public void createUserReadingSession() throws Exception {
        List<ReadingSession> readingSessions = readingSessionsDao.getUserReadingSessions(JOHN_DOE_USER, BOOK_UUID);
        assertThat(readingSessions).isEmpty();

        ReadingSession readingSession = readingSessionsDao.createUserReadingSession(JOHN_DOE_USER, BOOK_UUID, getReadingSession("uuid-1.json"));
        assertThat(readingSession).isNotNull();
        assertThat(readingSession.getUuid()).isNotNull();

        readingSessions = readingSessionsDao.getUserReadingSessions(JOHN_DOE_USER, BOOK_UUID);
        assertThat(readingSessions.get(0).getUuid()).isNotNull();
        assertThat(readingSessions)
            .hasSize(1)
            .contains(
                new ReadingSession(readingSessions.get(0).getUuid(),
                    "book-uuid-1",
                    "2017-01-31",
                    Arrays.asList(
                        new DateReadingSession("2017-01-01", 101, "bookmark-101")
                    )
                )
            );
    }

    @Test
    public void getUserReadingSession() throws Exception {
        ReadingSession readingSession = readingSessionsDao.createUserReadingSession(JOHN_DOE_USER,
            BOOK_UUID,
            getReadingSession("uuid-1.json"));
        assertThat(readingSession).isNotNull();
        assertThat(readingSession.getUuid()).isNotNull();

        Optional<ReadingSession> optionalReadingSession = readingSessionsDao.getUserReadingSession(JOHN_DOE_USER,
            BOOK_UUID,
            readingSession.getUuid());
        assertThat(optionalReadingSession.isPresent()).isTrue();
        assertThat(optionalReadingSession.get()).isEqualTo(
            new ReadingSession(optionalReadingSession.get().getUuid(),
                "book-uuid-1",
                "2017-01-31",
                Arrays.asList(
                    new DateReadingSession("2017-01-01", 101, "bookmark-101")
                )
            )
        );
    }

    @Test
    public void getUserMissingReadingSession() throws Exception {
        Optional<ReadingSession> optionalReadingSession = readingSessionsDao.getUserReadingSession(JOHN_DOE_USER,
            BOOK_UUID,
            "missing-isbn-1");
        assertThat(optionalReadingSession.isPresent()).isFalse();
    }

    @Test
    public void updateUserReadingSession() throws Exception {
        List<ReadingSession> readingSessions = readingSessionsDao.getUserReadingSessions(JOHN_DOE_USER, BOOK_UUID);
        assertThat(readingSessions).isEmpty();

        ReadingSession readingSession = getReadingSession("uuid-1.json");
        ReadingSession createdReadingSession = readingSessionsDao.createUserReadingSession(JOHN_DOE_USER,
            BOOK_UUID,
            readingSession);
        assertThat(createdReadingSession).isNotNull();
        assertThat(createdReadingSession.getUuid()).isNotNull();

        readingSessions = readingSessionsDao.getUserReadingSessions(JOHN_DOE_USER, BOOK_UUID);
        assertThat(readingSessions)
            .hasSize(1)
            .contains(
                new ReadingSession(readingSessions.get(0).getUuid(),
                    "book-uuid-1",
                    "2017-01-31",
                    Arrays.asList(
                        new DateReadingSession("2017-01-01", 101, "bookmark-101")
                    )
                )
            );

        ReadingSession updatedReadingSession = new ReadingSession(null,
            readingSession.getBookUuid(),
            null,
            Arrays.asList(
                new DateReadingSession("2017-01-01", 101, "updated-bookmark-101")
            )
        );

        Optional<String> optionalUuid = readingSessionsDao.updateUserReadingSession(JOHN_DOE_USER,
            BOOK_UUID,
            createdReadingSession.getUuid(), updatedReadingSession);
        assertThat(optionalUuid.isPresent()).isTrue();

        readingSessions = readingSessionsDao.getUserReadingSessions(JOHN_DOE_USER, BOOK_UUID);
        assertThat(readingSessions)
            .hasSize(1)
            .contains(
                new ReadingSession(readingSessions.get(0).getUuid(),
                    "book-uuid-1",
                    null,
                    Arrays.asList(
                        new DateReadingSession("2017-01-01", 101, "updated-bookmark-101")
                    )
                )
            );
    }

    @Test
    public void updateUserMissingReadingSession() throws Exception {
        ReadingSession readingSession = getReadingSession("uuid-1.json");
        Optional<String> optionalUuid = readingSessionsDao.updateUserReadingSession(JOHN_DOE_USER,
            BOOK_UUID,
            "uuid-1", readingSession);
        assertThat(optionalUuid.isPresent()).isFalse();
    }

    @Test
    public void deleteUserReadingSession() throws Exception {
        ReadingSession readingSession = readingSessionsDao.createUserReadingSession(JOHN_DOE_USER,
            BOOK_UUID,
            getReadingSession("uuid-1.json"));
        assertThat(readingSession).isNotNull();
        assertThat(readingSession.getUuid()).isNotNull();

        Optional<ReadingSession> optionalReadingSession = readingSessionsDao.getUserReadingSession(JOHN_DOE_USER,
            BOOK_UUID,
            readingSession.getUuid());
        assertThat(optionalReadingSession.isPresent()).isTrue();

        readingSessionsDao.deleteUserReadingSession(JOHN_DOE_USER,
            BOOK_UUID,
            readingSession.getUuid());

        optionalReadingSession = readingSessionsDao.getUserReadingSession(JOHN_DOE_USER,
            BOOK_UUID,
            readingSession.getUuid());
        assertThat(optionalReadingSession.isPresent()).isFalse();
    }

    private void createReadingSessionsFolder(String user) throws Exception {
        Path path = Paths.get(getUserReadingSessionsFolder(user));
        if(!path.toFile().exists()) {
            Files.createDirectories(path);
        }

    }

    private String getUserReadingSessionsFolder(String user) {
        return filesystemConfiguration.getLibraryFolder() + "/" + user + "/reading-sessions";
    }

    private void deleteLibraryFolder() {
        FileSystemUtils.deleteRecursively(new File(filesystemConfiguration.getLibraryFolder()));
    }

}
