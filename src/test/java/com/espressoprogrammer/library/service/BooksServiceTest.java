package com.espressoprogrammer.library.service;

import com.espressoprogrammer.library.dto.Book;
import com.espressoprogrammer.library.persistence.BooksDao;
import com.espressoprogrammer.library.persistence.ReadingSessionsDao;
import com.espressoprogrammer.library.service.BooksException.Reason;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.espressoprogrammer.library.LibraryTestUtil.getTestBook;
import static com.espressoprogrammer.library.LibraryTestUtil.getTestReadingSession;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BooksServiceTest {
    private static final String JOHN_DOE_USER = "johndoe";

    @MockBean
    private BooksDao booksDao;

    @MockBean
    private ReadingSessionsDao readingSessionsDao;

    @Autowired
    private BooksService booksService;

    @Test
    public void getUserBooks() throws Exception {
        ArrayList<Book> books = new ArrayList<>();
        books.add(getTestBook("1e4014b1-a551-4310-9f30-590c3140b695.json"));
        when(booksDao.getUserBooks(JOHN_DOE_USER, "JavaScript")).thenReturn(books);

        List<Book> actualBooks = booksService.getUserBooks(JOHN_DOE_USER, "JavaScript");
        assertThat(actualBooks).isEqualTo(books);
    }

    @Test
    public void createUserBook() throws Exception {
        when(booksDao.getUserBooks(JOHN_DOE_USER)).thenReturn(Collections.emptyList());

        Book bookRequest = getTestBook("1e4014b1-a551-4310-9f30-590c3140b695-request.json");
        Book book = getTestBook("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(booksDao.createUserBook(JOHN_DOE_USER, bookRequest)).thenReturn(book);

        Book actualBook = booksService.createUserBook(JOHN_DOE_USER, bookRequest);
        assertThat(actualBook).isEqualTo(book);
    }

    @Test
    public void createExistingUserBook() throws Exception {
        Book book = getTestBook("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(booksDao.getUserBooks(JOHN_DOE_USER)).thenReturn(Arrays.asList(book));

        Book bookRequest = getTestBook("1e4014b1-a551-4310-9f30-590c3140b695-request.json");
        try {
            booksService.createUserBook(JOHN_DOE_USER, bookRequest);
            fail("It should fail with " + Reason.BOOK_ALREADY_EXISTS);
        } catch(BooksException ex) {
            assertThat(ex.getReason()).isEqualTo(Reason.BOOK_ALREADY_EXISTS);
        } catch (Exception ex) {
            fail("It should fail with " + Reason.BOOK_ALREADY_EXISTS);
        }
    }

    @Test
    public void getUserBook() throws Exception {
        String uuid = "1e4014b1-a551-4310-9f30-590c3140b695";
        Book book = getTestBook(uuid + ".json");
        when(booksDao.getUserBook(JOHN_DOE_USER, uuid)).thenReturn(Optional.of(book));

        Book actualBook = booksService.getUserBook(JOHN_DOE_USER, uuid);
        assertThat(actualBook).isEqualTo(book);
    }

    @Test
    public void getMissingUserBook() throws Exception {
        String uuid = "missing-uuid-1";
        when(booksDao.getUserBook(JOHN_DOE_USER, uuid)).thenReturn(Optional.empty());

        try {
            booksService.getUserBook(JOHN_DOE_USER, uuid);
            fail("It should fail with " + Reason.BOOK_NOT_FOUND);
        } catch(BooksException ex) {
            assertThat(ex.getReason()).isEqualTo(Reason.BOOK_NOT_FOUND);
        } catch (Exception ex) {
            fail("It should fail with " + Reason.BOOK_NOT_FOUND);
        }
    }

    @Test
    public void updateUserBook() throws Exception {
        Book updateBook = getTestBook("1e4014b1-a551-4310-9f30-590c3140b695-update.json");
        Book updateBookRequest = getTestBook("1e4014b1-a551-4310-9f30-590c3140b695-update-request.json");
        when(booksDao.updateUserBook(JOHN_DOE_USER, updateBook.getUuid(), updateBookRequest))
            .thenReturn(Optional.of(updateBook.getUuid()));

        String actualUuid = booksService.updateUserBook(JOHN_DOE_USER, updateBook.getUuid(), updateBookRequest);
        assertThat(actualUuid).isEqualTo(updateBook.getUuid());
    }

    @Test
    public void updateExistingUserBook() throws Exception {
        Book theOtherBook = getTestBook("f2e10e37-b0fc-4eff-93aa-3dff682cc388.json");
        Book updateBook = getTestBook("f2e10e37-b0fc-4eff-93aa-3dff682cc388.json");
        Book updateBookRequest = getTestBook("1e4014b1-a551-4310-9f30-590c3140b695-update-existing-book.json");
        when(booksDao.getUserBooks(JOHN_DOE_USER)).thenReturn(Arrays.asList(updateBook, theOtherBook));

        try {
            booksService.updateUserBook(JOHN_DOE_USER, updateBook.getUuid(), updateBookRequest);
            fail("It should fail with " + Reason.BOOK_ALREADY_EXISTS);
        } catch(BooksException ex) {
            assertThat(ex.getReason()).isEqualTo(Reason.BOOK_ALREADY_EXISTS);
        } catch (Exception ex) {
            fail("It should fail with " + Reason.BOOK_ALREADY_EXISTS);
        }
    }

    @Test
    public void updateMissingUserBook() throws Exception {
        Book updateBook = getTestBook("1e4014b1-a551-4310-9f30-590c3140b695-update.json");
        Book updateBookRequest = getTestBook("1e4014b1-a551-4310-9f30-590c3140b695-update-request.json");
        when(booksDao.updateUserBook(JOHN_DOE_USER, updateBook.getUuid(), updateBookRequest)).thenReturn(Optional.empty());

        try {
            booksService.updateUserBook(JOHN_DOE_USER, updateBook.getUuid(), updateBookRequest);
            fail("It should fail with " + Reason.BOOK_NOT_FOUND);
        } catch(BooksException ex) {
            assertThat(ex.getReason()).isEqualTo(Reason.BOOK_NOT_FOUND);
        } catch (Exception ex) {
            fail("It should fail with " + Reason.BOOK_NOT_FOUND);
        }
    }

    @Test
    public void deleteUserBook() throws Exception {
        Book book = getTestBook("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(booksDao.deleteUserBook(JOHN_DOE_USER, book.getUuid())).thenReturn(Optional.of(book.getUuid()));
        when(readingSessionsDao.getUserReadingSessions(JOHN_DOE_USER, book.getUuid()))
                .thenReturn(Arrays.asList(getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695-delete-date-reading-session.json")));

        String actualBookUuid = booksService.deleteUserBook(JOHN_DOE_USER, book.getUuid());
        assertThat(actualBookUuid).isEqualTo(book.getUuid());
    }

    @Test
    public void deleteMissingUserBook() throws Exception {
        Book book = getTestBook("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(booksDao.deleteUserBook(JOHN_DOE_USER, book.getUuid())).thenReturn(Optional.empty());

        try {
            booksService.deleteUserBook(JOHN_DOE_USER, book.getUuid());
            fail("It should fail with " + Reason.BOOK_NOT_FOUND);
        } catch(BooksException ex) {
            assertThat(ex.getReason()).isEqualTo(Reason.BOOK_NOT_FOUND);
        } catch (Exception ex) {
            fail("It should fail with " + Reason.BOOK_NOT_FOUND);
        }
    }

    @Test
    public void deleteUserBookWithReadingSessions() throws Exception {
        Book book = getTestBook("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(booksDao.deleteUserBook(JOHN_DOE_USER, book.getUuid())).thenReturn(Optional.of(book.getUuid()));
        when(readingSessionsDao.getUserReadingSessions(JOHN_DOE_USER, book.getUuid()))
                .thenReturn(Arrays.asList(getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json")));

        try {
            booksService.deleteUserBook(JOHN_DOE_USER, book.getUuid());
            fail("It should fail with " + Reason.BOOK_HAS_READING_SESSION);
        } catch(BooksException ex) {
            assertThat(ex.getReason()).isEqualTo(Reason.BOOK_HAS_READING_SESSION);
        } catch (Exception ex) {
            fail("It should fail with " + Reason.BOOK_HAS_READING_SESSION);
        }
    }

}
