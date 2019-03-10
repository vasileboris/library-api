package com.espressoprogrammer.library.persistence.filesystem;

import com.espressoprogrammer.library.dto.Book;
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

import static com.espressoprogrammer.library.util.LibraryTestUtil.copyBook;
import static com.espressoprogrammer.library.util.LibraryTestUtil.getTestBook;
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
                Arrays.asList("First1 Last1"),
                "image-1",
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
                Arrays.asList("First1 Last1"),
                "image-1",
                100))
            .contains(new Book("uuid-2",
                "isbn10-2",
                "isbn13-2",
                "Title 2",
                Arrays.asList("First21 Last21", "First22 Last22"),
                "image-2",
                200));
    }

    @Test
    public void searchOneUserBookByTitle() throws Exception {
        createBooksFolder(JOHN_DOE_USER);
        copyBook("uuid-1.json", getUserBooksFolder(JOHN_DOE_USER));
        copyBook("uuid-2.json", getUserBooksFolder(JOHN_DOE_USER));

        List<Book> books = booksDao.getUserBooks(JOHN_DOE_USER, "Title 1");
        assertThat(books)
            .hasSize(1)
            .contains(new Book("uuid-1",
                "isbn10-1",
                "isbn13-1",
                "Title 1",
                Arrays.asList("First1 Last1"),
                "image-1",
                100));
    }

    @Test
    public void searchOneUserBookByIsbn10() throws Exception {
        createBooksFolder(JOHN_DOE_USER);
        copyBook("uuid-1.json", getUserBooksFolder(JOHN_DOE_USER));
        copyBook("uuid-2.json", getUserBooksFolder(JOHN_DOE_USER));

        List<Book> books = booksDao.getUserBooks(JOHN_DOE_USER, "isbn10-1");
        assertThat(books)
            .hasSize(1)
            .contains(new Book("uuid-1",
                "isbn10-1",
                "isbn13-1",
                "Title 1",
                Arrays.asList("First1 Last1"),
                "image-1",
                100));
    }

    @Test
    public void searchOneUserBookByIsbn13() throws Exception {
        createBooksFolder(JOHN_DOE_USER);
        copyBook("uuid-1.json", getUserBooksFolder(JOHN_DOE_USER));
        copyBook("uuid-2.json", getUserBooksFolder(JOHN_DOE_USER));

        List<Book> books = booksDao.getUserBooks(JOHN_DOE_USER, "isbn13-1");
        assertThat(books)
            .hasSize(1)
            .contains(new Book("uuid-1",
                "isbn10-1",
                "isbn13-1",
                "Title 1",
                Arrays.asList("First1 Last1"),
                "image-1",
                100));
    }

    @Test
    public void searchOneUserBookByAuthor() throws Exception {
        createBooksFolder(JOHN_DOE_USER);
        copyBook("uuid-1.json", getUserBooksFolder(JOHN_DOE_USER));
        copyBook("uuid-2.json", getUserBooksFolder(JOHN_DOE_USER));

        List<Book> books = booksDao.getUserBooks(JOHN_DOE_USER, "First1");
        assertThat(books)
            .hasSize(1)
            .contains(new Book("uuid-1",
                "isbn10-1",
                "isbn13-1",
                "Title 1",
                Arrays.asList("First1 Last1"),
                "image-1",
                100));
    }

    @Test
    public void searchUserBookByMissingData() throws Exception {
        createBooksFolder(JOHN_DOE_USER);
        copyBook("uuid-1.json", getUserBooksFolder(JOHN_DOE_USER));
        copyBook("uuid-2.json", getUserBooksFolder(JOHN_DOE_USER));

        List<Book> books = booksDao.getUserBooks(JOHN_DOE_USER, "Missing");
        assertThat(books).isEmpty();
    }

    @Test
    public void createUserBook() throws Exception {
        List<Book> books = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(books).isEmpty();

        Book book = booksDao.createUserBook(JOHN_DOE_USER, getTestBook("uuid-1.json"));
        assertThat(book).isNotNull();
        assertThat(book.getUuid()).isNotNull();

        books = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(books.get(0).getUuid()).isNotNull();
        assertThat(books)
            .hasSize(1)
            .contains(new Book(books.get(0).getUuid(),
                "isbn10-1",
                "isbn13-1",
                "Title 1",
                Arrays.asList("First1 Last1"),
                "image-1",
                100));
    }

    @Test
    public void createUserBookWithIsbn13Null() throws Exception {
        List<Book> books = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(books).isEmpty();

        Book book = booksDao.createUserBook(JOHN_DOE_USER, getTestBook("uuid-3.json"));
        assertThat(book).isNotNull();
        assertThat(book.getUuid()).isNotNull();

        books = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(books)
            .hasSize(1)
            .contains(new Book(books.get(0).getUuid(),
                "isbn10-3",
                null,
                "Title 3",
                Arrays.asList("First3 Last3"),
                "image-3",
                300));
    }

    @Test
    public void getUserBook() throws Exception {
        Book book = booksDao.createUserBook(JOHN_DOE_USER, getTestBook("uuid-1.json"));
        assertThat(book).isNotNull();
        assertThat(book.getUuid()).isNotNull();

        Optional<Book> optionalBook = booksDao.getUserBook(JOHN_DOE_USER, book.getUuid());
        assertThat(optionalBook.isPresent()).isTrue();
        assertThat(optionalBook.get()).isEqualTo(new Book(optionalBook.get().getUuid(),
            "isbn10-1",
            "isbn13-1",
            "Title 1",
            Arrays.asList("First1 Last1"),
            "image-1",
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

        Book book = getTestBook("uuid-1.json");
        Book createdBook = booksDao.createUserBook(JOHN_DOE_USER, book);
        assertThat(createdBook).isNotNull();
        assertThat(createdBook.getUuid()).isNotNull();

        books = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(books)
            .hasSize(1)
            .contains(new Book(books.get(0).getUuid(),
                "isbn10-1",
                "isbn13-1",
                "Title 1",
                Arrays.asList("First1 Last1"),
                "image-1",
                100));

        Book updatedBook = new Book(null,
            book.getIsbn10(),
            book.getIsbn13(),
            "Updated " + book.getTitle(),
            book.getAuthors(),
            "image-1",
            book.getPages());

        Optional<String> optionalUuid = booksDao.updateUserBook(JOHN_DOE_USER, createdBook.getUuid(), updatedBook);
        assertThat(optionalUuid.isPresent()).isTrue();

        books = booksDao.getUserBooks(JOHN_DOE_USER);
        assertThat(books)
            .hasSize(1)
            .contains(new Book(books.get(0).getUuid(),
                "isbn10-1",
                "isbn13-1",
                "Updated Title 1",
                Arrays.asList("First1 Last1"),
                "image-1",
                100));
    }

    @Test
    public void updateUserMissingBook() throws Exception {
        Book book = getTestBook("uuid-1.json");
        Optional<String> optionalUuid = booksDao.updateUserBook(JOHN_DOE_USER, "uuid-1", book);
        assertThat(optionalUuid.isPresent()).isFalse();
    }

    @Test
    public void deleteUserBook() throws Exception {
        Book book = booksDao.createUserBook(JOHN_DOE_USER, getTestBook("uuid-1.json"));
        assertThat(book).isNotNull();
        assertThat(book.getUuid()).isNotNull();

        Optional<Book> optionalBook = booksDao.getUserBook(JOHN_DOE_USER, book.getUuid());
        assertThat(optionalBook.isPresent()).isTrue();

        booksDao.deleteUserBook(JOHN_DOE_USER, book.getUuid());

        optionalBook = booksDao.getUserBook(JOHN_DOE_USER, book.getUuid());
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
