package com.espressoprogrammer.library.persistence.filesystem;

import com.espressoprogrammer.library.dto.Author;
import com.espressoprogrammer.library.dto.Book;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.espressoprogrammer.library.LibraryTestUtil.copyBook;
import static com.espressoprogrammer.library.LibraryTestUtil.getBook;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FilesystemBooksDaoTest {
    private static final String TMPDIR = "java.io.tmpdir";
    private static final String JOHN_DOE_USER = "johndoe";

    @Mock
    private FilesystemConfiguration filesystemConfiguration;

    @InjectMocks
    private FilesystemBooksDao booksDao;

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
    public void getNoUserBooks() throws Exception {
        assertThat(booksDao.getUserBooks(JOHN_DOE_USER)).isEmpty();
    }

    @Test
    public void getOneUserBook() throws Exception {
        createBooksFolder(JOHN_DOE_USER);
        copyBook("uuid-1.json", getUserBooksFolder(JOHN_DOE_USER));

        List<Book> userBooks = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(userBooks)
            .hasSize(1)
            .contains(new Book("uuid-1",
                "isbn10-1",
                "isbn13-1",
                "Title 1",
                Arrays.asList(new Author("First1", "Last1")),
                100));
    }

    @Test
    public void getTwoUserBooks() throws Exception {
        createBooksFolder(JOHN_DOE_USER);
        copyBook("uuid-1.json", getUserBooksFolder(JOHN_DOE_USER));
        copyBook("uuid-2.json", getUserBooksFolder(JOHN_DOE_USER));

        List<Book> userBooks = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(userBooks)
            .hasSize(2)
            .contains(new Book("uuid-1",
                "isbn10-1",
                "isbn13-1",
                "Title 1",
                Arrays.asList(new Author("First1", "Last1")),
                100))
            .contains(new Book("uuid-2",
                "isbn10-2",
                "isbn13-2",
                "Title 2",
                Arrays.asList(new Author("First21", "Last21"), new Author("First22", "Last22")),
                200));
    }

    @Test
    public void createUserBook() throws Exception {
        List<Book> userBooks = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(userBooks).isEmpty();

        String uuid = booksDao.createUserBook(JOHN_DOE_USER, getBook("uuid-1.json"));
        assertThat(uuid).isNotNull();

        userBooks = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(userBooks.get(0).getUuid()).isNotNull();
        assertThat(userBooks)
            .hasSize(1)
            .contains(new Book(userBooks.get(0).getUuid(),
                "isbn10-1",
                "isbn13-1",
                "Title 1",
                Arrays.asList(new Author("First1", "Last1")),
                100));
    }

    @Test
    public void createUserBookWithIsbn13Null() throws Exception {
        List<Book> userBooks = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(userBooks).isEmpty();

        String uuid = booksDao.createUserBook(JOHN_DOE_USER, getBook("uuid-3.json"));
        assertThat(uuid).isNotNull();

        userBooks = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(userBooks)
            .hasSize(1)
            .contains(new Book(userBooks.get(0).getUuid(),
                "isbn10-3",
                null,
                "Title 3",
                Arrays.asList(new Author("First3", "Last3")),
                300));
    }

    @Test
    public void getUserBook() throws Exception {
        String uuid = booksDao.createUserBook(JOHN_DOE_USER, getBook("uuid-1.json"));
        assertThat(uuid).isNotNull();

        Optional<Book> optionalBook = booksDao.getUserBook(JOHN_DOE_USER, uuid);
        assertThat(optionalBook.isPresent()).isTrue();
        assertThat(optionalBook.get()).isEqualTo(new Book(optionalBook.get().getUuid(),
            "isbn10-1",
            "isbn13-1",
            "Title 1",
            Arrays.asList(new Author("First1", "Last1")),
            100));
    }

    @Test
    public void getUserMissingBook() throws Exception {
        Optional<Book> optionalBook = booksDao.getUserBook(JOHN_DOE_USER, "missing-isbn-1");
        assertThat(optionalBook.isPresent()).isFalse();
    }

    @Test
    public void updateUserBook() throws Exception {
        List<Book> userBooks = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(userBooks).isEmpty();

        Book userBook = getBook("uuid-1.json");
        String uuid = booksDao.createUserBook(JOHN_DOE_USER, userBook);
        assertThat(uuid).isNotNull();

        userBooks = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(userBooks)
            .hasSize(1)
            .contains(new Book(userBooks.get(0).getUuid(),
                "isbn10-1",
                "isbn13-1",
                "Title 1",
                Arrays.asList(new Author("First1", "Last1")),
                100));

        Book updatedUserBook = new Book(null,
            userBook.getIsbn10(),
            userBook.getIsbn13(),
            "Updated " + userBook.getTitle(),
            userBook.getAuthors(),
            userBook.getPages());

        Optional<String> optional = booksDao.updateUserBook(JOHN_DOE_USER, uuid, updatedUserBook);
        assertThat(optional.isPresent()).isTrue();

        userBooks = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(userBooks)
            .hasSize(1)
            .contains(new Book(userBooks.get(0).getUuid(),
                "isbn10-1",
                "isbn13-1",
                "Updated Title 1",
                Arrays.asList(new Author("First1", "Last1")),
                100));
    }

    @Test
    public void updateUserMisingBook() throws Exception {
        Book userBook = getBook("uuid-1.json");
        Optional<String> optional = booksDao.updateUserBook(JOHN_DOE_USER, "uuid-1", userBook);
        assertThat(optional.isPresent()).isFalse();
    }

    @Test
    public void deleteUserBook() throws Exception {
        String uuid = booksDao.createUserBook(JOHN_DOE_USER, getBook("uuid-1.json"));
        assertThat(uuid).isNotNull();

        Optional<Book> optionalBook = booksDao.getUserBook(JOHN_DOE_USER, uuid);
        assertThat(optionalBook.isPresent()).isTrue();

        booksDao.deleteUserBook(JOHN_DOE_USER, uuid);

        optionalBook = booksDao.getUserBook(JOHN_DOE_USER, uuid);
        assertThat(optionalBook.isPresent()).isFalse();
    }

    private void createBooksFolder(String user) throws Exception {
        Path path = Paths.get(getUserBooksFolder(user));
        if(!path.toFile().exists()) {
            Files.createDirectories(path);
        }

    }

    private String getUserBooksFolder(String user) {
        return filesystemConfiguration.getLibraryFolder() + "/" + user + "/books";
    }

    private void deleteLibraryFolder() {
        FileSystemUtils.deleteRecursively(new File(filesystemConfiguration.getLibraryFolder()));
    }

}
