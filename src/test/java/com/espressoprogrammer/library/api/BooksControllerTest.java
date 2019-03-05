package com.espressoprogrammer.library.api;

import com.espressoprogrammer.library.dto.Book;
import com.espressoprogrammer.library.service.BooksService;
import com.espressoprogrammer.library.service.BooksException;
import com.espressoprogrammer.library.service.BooksException.Reason;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static com.espressoprogrammer.library.LibraryTestUtil.getBook;
import static com.espressoprogrammer.library.LibraryTestUtil.getBookJson;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BooksControllerTest {
    private static final String JOHN_DOE_USER = "johndoe";

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private BooksService booksService;

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
            .apply(documentationConfiguration(this.restDocumentation))
            .build();
    }

    @Test
    public void getUserBooks() throws Exception {
        ArrayList<Book> books = new ArrayList<>();
        books.add(getBook("1e4014b1-a551-4310-9f30-590c3140b695.json"));
        when(booksService.getUserBooks(JOHN_DOE_USER, "JavaScript")).thenReturn(books);

        this.mockMvc.perform(get("/users/{user}/books?searchText={searchText}", JOHN_DOE_USER, "JavaScript"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$[0].uuid", is("1e4014b1-a551-4310-9f30-590c3140b695")))
            .andExpect(jsonPath("$[0].isbn10", is("1-61729-310-5")))
            .andExpect(jsonPath("$[0].isbn13", is("978-1-61729-310-8")))
            .andExpect(jsonPath("$[0].title", is("Get Programming with JavaScript")))
            .andExpect(jsonPath("$[0].authors[0]", is("John R. Larsen")))
            .andExpect(jsonPath("$[0].image", is("Larsen_hires.png")))
            .andExpect(jsonPath("$[0].pages", is(406)))
            .andDo(document("{class-name}/{method-name}",
                pathParameters(
                    parameterWithName("user").description("User id")),
                requestParameters(
                    parameterWithName("searchText")
                        .description("It is used to search all book fields for this value (Optional)").optional()),
                responseFields(
                    fieldWithPath("[].uuid").description("UUID used to identify a book"),
                    fieldWithPath("[].isbn10").description("10 digits ISBN (optional)").optional(),
                    fieldWithPath("[].isbn13").description("13 digits ISBN (optional)").optional(),
                    fieldWithPath("[].title").description("Book title"),
                    fieldWithPath("[].authors").description("Book authors (optional)").optional(),
                    fieldWithPath("[].image").description("Book image (optional)").optional(),
                    fieldWithPath("[].pages").description("Number of pages")
                )));
    }

    @Test
    public void createUserBook() throws Exception {
        Book bookRequest = getBook("1e4014b1-a551-4310-9f30-590c3140b695-request.json");
        when(booksService.createUserBook(JOHN_DOE_USER, bookRequest)).thenReturn(getBook("1e4014b1-a551-4310-9f30-590c3140b695.json"));

        this.mockMvc.perform(post("/users/{user}/books", JOHN_DOE_USER)
                .content(getBookJson("1e4014b1-a551-4310-9f30-590c3140b695-request.json"))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, "/users/" + JOHN_DOE_USER + "/books/1e4014b1-a551-4310-9f30-590c3140b695"))
            .andDo(document("{class-name}/{method-name}",
                pathParameters(parameterWithName("user").description("User id")),
                requestFields(
                    fieldWithPath("isbn10").description("10 digits ISBN (optional)").optional(),
                    fieldWithPath("isbn13").description("13 digits ISBN (optional)" ).optional(),
                    fieldWithPath("title").description("Book title"),
                    fieldWithPath("authors").description("Book authors (optional)").optional(),
                    fieldWithPath("image").description("Book image (optional)").optional(),
                    fieldWithPath("pages").description("Number of pages")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.LOCATION).description("New added bookRequest resource")
                ),
                responseFields(
                    fieldWithPath("uuid").description("UUID used to identify a bookRequest"),
                    fieldWithPath("isbn10").description("10 digits ISBN (optional)").optional(),
                    fieldWithPath("isbn13").description("13 digits ISBN (optional)").optional(),
                    fieldWithPath("title").description("Book title"),
                    fieldWithPath("authors").description("Book authors (optional)").optional(),
                    fieldWithPath("image").description("Book image (optional)").optional(),
                    fieldWithPath("pages").description("Number of pages")
                )));
    }

    @Test
    public void createExistingUserBook() throws Exception {
        Book bookRequest = getBook("1e4014b1-a551-4310-9f30-590c3140b695-request.json");
        when(booksService.createUserBook(JOHN_DOE_USER, bookRequest)).thenThrow(new BooksException(Reason.BOOK_ALREADY_EXISTS));

        this.mockMvc.perform(post("/users/{user}/books", JOHN_DOE_USER)
            .content(getBookJson("1e4014b1-a551-4310-9f30-590c3140b695-request.json"))
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isForbidden());
    }

    @Test
    public void getUserBook() throws Exception {
        String uuid = "1e4014b1-a551-4310-9f30-590c3140b695";
        Book book = getBook(uuid + ".json");
        when(booksService.getUserBook(JOHN_DOE_USER, uuid)).thenReturn(book);

        this.mockMvc.perform(get("/users/{user}/books/{uuid}", JOHN_DOE_USER, uuid))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("uuid", is("1e4014b1-a551-4310-9f30-590c3140b695")))
            .andExpect(jsonPath("isbn10", is("1-61729-310-5")))
            .andExpect(jsonPath("isbn13", is("978-1-61729-310-8")))
            .andExpect(jsonPath("title", is("Get Programming with JavaScript")))
            .andExpect(jsonPath("authors[0]", is("John R. Larsen")))
            .andExpect(jsonPath("image", is("Larsen_hires.png")))
            .andExpect(jsonPath("pages", is(406)))
            .andDo(document("{class-name}/{method-name}",
                pathParameters(
                    parameterWithName("user").description("User id"),
                    parameterWithName("uuid").description("Book uuid")),
                responseFields(
                    fieldWithPath("uuid").description("UUID used to identify a book"),
                    fieldWithPath("isbn10").description("10 digits ISBN (optional)").optional(),
                    fieldWithPath("isbn13").description("13 digits ISBN (optional)").optional(),
                    fieldWithPath("title").description("Book title"),
                    fieldWithPath("authors").description("Book authors (optional)").optional(),
                    fieldWithPath("image").description("Book image (optional)").optional(),
                    fieldWithPath("pages").description("Number of pages")
                )));
    }

    @Test
    public void getMissingUserBook() throws Exception {
        String uuid = "missing-uuid-1";
        when(booksService.getUserBook(JOHN_DOE_USER, uuid)).thenThrow(new BooksException(Reason.BOOK_NOT_FOUND));

        this.mockMvc.perform(get("/users/{user}/books/{uuid}", JOHN_DOE_USER, uuid))
            .andExpect(status().isNotFound())
            .andDo(document("{class-name}/{method-name}"));
    }

    @Test
    public void updateUserBook() throws Exception {
        Book updateBook = getBook("1e4014b1-a551-4310-9f30-590c3140b695.json");
        Book updateBookRequest = getBook("1e4014b1-a551-4310-9f30-590c3140b695-update-request.json");
        when(booksService.updateUserBook(JOHN_DOE_USER, updateBook.getUuid(), updateBookRequest)).thenReturn(updateBook.getUuid());

        this.mockMvc.perform(put("/users/{user}/books/{uuid}", JOHN_DOE_USER, updateBook.getUuid())
            .content(getBookJson("1e4014b1-a551-4310-9f30-590c3140b695-update-request.json"))
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isNoContent())
            .andDo(document("{class-name}/{method-name}",
                pathParameters(
                    parameterWithName("user").description("User id"),
                    parameterWithName("uuid").description("Book uuid")),
                requestFields(
                    fieldWithPath("isbn10").description("10 digits ISBN (optional)").optional(),
                    fieldWithPath("isbn13").description("13 digits ISBN (optional)").optional(),
                    fieldWithPath("title").description("Book title"),
                    fieldWithPath("authors").description("Book authors (optional)").optional(),
                    fieldWithPath("image").description("Book image (optional)").optional(),
                    fieldWithPath("pages").description("Number of pages")
                )));
    }

    @Test
    public void updateExistingUserBook() throws Exception {
        Book updateBook = getBook("f2e10e37-b0fc-4eff-93aa-3dff682cc388.json");
        Book updateBookRequest = getBook("1e4014b1-a551-4310-9f30-590c3140b695-request.json");
        when(booksService.updateUserBook(JOHN_DOE_USER, updateBook.getUuid(), updateBookRequest)).thenThrow(new BooksException(Reason.BOOK_ALREADY_EXISTS));

        this.mockMvc.perform(put("/users/{user}/books/{uuid}", JOHN_DOE_USER, updateBook.getUuid())
            .content(getBookJson("1e4014b1-a551-4310-9f30-590c3140b695-request.json"))
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isForbidden());
    }

    @Test
    public void updateMissingUserBook() throws Exception {
        Book updateBook = getBook("1e4014b1-a551-4310-9f30-590c3140b695-update.json");
        Book updateBookRequest = getBook("1e4014b1-a551-4310-9f30-590c3140b695-update-request.json");
        when(booksService.updateUserBook(JOHN_DOE_USER, updateBook.getUuid(), updateBookRequest)).thenThrow(new BooksException(Reason.BOOK_NOT_FOUND));

        this.mockMvc.perform(put("/users/{user}/books/{uuid}", JOHN_DOE_USER, updateBook.getUuid())
            .content(getBookJson("1e4014b1-a551-4310-9f30-590c3140b695-update-request.json"))
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isNotFound())
            .andDo(document("{class-name}/{method-name}"));
    }

    @Test
    public void deleteUserBook() throws Exception {
        Book book = getBook("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(booksService.deleteUserBook(JOHN_DOE_USER, book.getUuid())).thenReturn(book.getUuid());

        this.mockMvc.perform(delete("/users/{user}/books/{uuid}", JOHN_DOE_USER, book.getUuid()))
            .andExpect(status().isNoContent())
            .andDo(document("{class-name}/{method-name}",
                pathParameters(
                    parameterWithName("user").description("User id"),
                    parameterWithName("uuid").description("Book uuid"))));
    }

    @Test
    public void deleteMissingUserBook() throws Exception {
        Book book = getBook("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(booksService.deleteUserBook(JOHN_DOE_USER, book.getUuid())).thenThrow(new BooksException(Reason.BOOK_NOT_FOUND));

        this.mockMvc.perform(delete("/users/{user}/books/{uuid}", JOHN_DOE_USER, book.getUuid()))
            .andExpect(status().isNotFound())
            .andDo(document("{class-name}/{method-name}"));
    }

    @Test
    public void deleteUserBookWithReadingSessions() throws Exception {
        Book book = getBook("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(booksService.deleteUserBook(JOHN_DOE_USER, book.getUuid())).thenThrow(new BooksException(Reason.BOOK_HAS_READING_SESSION));

        this.mockMvc.perform(delete("/users/{user}/books/{uuid}", JOHN_DOE_USER, book.getUuid()))
                .andExpect(status().isForbidden())
                .andDo(document("{class-name}/{method-name}"));
    }

}
