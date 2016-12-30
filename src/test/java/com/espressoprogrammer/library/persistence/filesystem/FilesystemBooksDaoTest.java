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

        List<Book> books = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(books)
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

        List<Book> books = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(books)
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
        List<Book> books = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(books).isEmpty();

        String uuid = booksDao.createUserBook(JOHN_DOE_USER, getBook("uuid-1.json"));
        assertThat(uuid).isNotNull();

        books = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(books.get(0).getUuid()).isNotNull();
        assertThat(books)
            .hasSize(1)
            .contains(new Book(books.get(0).getUuid(),
                "isbn10-1",
                "isbn13-1",
                "Title 1",
                Arrays.asList(new Author("First1", "Last1")),
                100));
    }

    @Test
    public void createUserBookWithIsbn13Null() throws Exception {
        List<Book> books = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(books).isEmpty();

        String uuid = booksDao.createUserBook(JOHN_DOE_USER, getBook("uuid-3.json"));
        assertThat(uuid).isNotNull();

        books = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(books)
            .hasSize(1)
            .contains(new Book(books.get(0).getUuid(),
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
        List<Book> books = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(books).isEmpty();

        Book book = getBook("uuid-1.json");
        String uuid = booksDao.createUserBook(JOHN_DOE_USER, book);
        assertThat(uuid).isNotNull();

        books = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(books)
            .hasSize(1)
            .contains(new Book(books.get(0).getUuid(),
                "isbn10-1",
                "isbn13-1",
                "Title 1",
                Arrays.asList(new Author("First1", "Last1")),
                100));

        Book updatedBook = new Book(null,
            book.getIsbn10(),
            book.getIsbn13(),
            "Updated " + book.getTitle(),
            book.getAuthors(),
            book.getPages());

        Optional<String> optionalUuid = booksDao.updateUserBook(JOHN_DOE_USER, uuid, updatedBook);
        assertThat(optionalUuid.isPresent()).isTrue();

        books = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(books)
            .hasSize(1)
            .contains(new Book(books.get(0).getUuid(),
                "isbn10-1",
                "isbn13-1",
                "Updated Title 1",
                Arrays.asList(new Author("First1", "Last1")),
                100));
    }

    @Test
    public void updateUserMissingBook() throws Exception {
        Book book = getBook("uuid-1.json");
        Optional<String> optionalUuid = booksDao.updateUserBook(JOHN_DOE_USER, "uuid-1", book);
        assertThat(optionalUuid.isPresent()).isFalse();
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
