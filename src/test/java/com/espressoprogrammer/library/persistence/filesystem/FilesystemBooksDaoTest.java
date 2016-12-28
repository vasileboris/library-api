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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FilesystemBooksDaoTest {
    private static final String TMPDIR = "java.io.tmpdir";

    @Mock
    private FilesystemConfiguration filesystemConfiguration;

    @InjectMocks
    private FilesystemBooksDao booksDao;

    private String user;

    @Before
    public void init() throws Exception {
        user = "johndoe";

        when(filesystemConfiguration.getLibraryFolder()).thenReturn(System.getProperty(TMPDIR)
            + "/library-"
            + System.nanoTime());
    }

    @After
    public void clear() throws Exception {
        deleteLibraryFolder();
    }

    @Test
    public void getNoBooks() throws Exception {
        assertThat(booksDao.getUserBooks(user)).isEmpty();
    }

    @Test
    public void getOneBook() throws Exception {
        createBooksFolder(user);
        copyBook("isbn-1.json", user);

        List<Book> userBooks = booksDao.getUserBooks(user);

        assertThat(userBooks)
            .hasSize(1)
            .contains(new Book("isbn10-1",
                "isbn13-1",
                "Title 1",
                Arrays.asList(new Author("First1", "Last1")),
                100));
    }

    @Test
    public void getTwoBooks() throws Exception {
        createBooksFolder(user);
        copyBook("isbn-1.json", user);
        copyBook("isbn-2.json", user);

        List<Book> userBooks = booksDao.getUserBooks(user);

        assertThat(userBooks)
            .hasSize(2)
            .contains(new Book("isbn10-1",
                "isbn13-1",
                "Title 1",
                Arrays.asList(new Author("First1", "Last1")),
                100))
            .contains(new Book("isbn10-2",
                "isbn13-2",
                "Title 2",
                Arrays.asList(new Author("First21", "Last21"), new Author("First22", "Last22")),
                200));
    }

    private void createBooksFolder(String user) throws Exception {
        Path path = Paths.get(getUserBooksFolder(user));
        if(!path.toFile().exists()) {
            Files.createDirectories(path);
        }

    }

    private void copyBook(String book, String user) throws IOException {
        Files.copy(getClass().getResourceAsStream("/json/books/" + book),
            Paths.get(getUserBooksFolder(user) + "/" + book));
    }

    private String getUserBooksFolder(String user) {
        return filesystemConfiguration.getLibraryFolder() + "/" + user + "/books";
    }

    private void deleteLibraryFolder() {
        FileSystemUtils.deleteRecursively(new File(filesystemConfiguration.getLibraryFolder()));
    }

}
