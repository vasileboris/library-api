package com.espressoprogrammer.library.service;

import com.espressoprogrammer.library.dto.Book;
import com.espressoprogrammer.library.persistence.BooksDao;
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
import java.util.Arrays;
import java.util.Optional;

import static com.espressoprogrammer.library.LibraryTestUtil.getBook;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static com.espressoprogrammer.library.LibraryTestUtil.getBookJson;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BooksControllerTest {
    private static final String JOHN_DOE_USER = "johndoe";

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private BooksDao booksDao;

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
        when(booksDao.getUserBooks(JOHN_DOE_USER)).thenReturn(books);

        this.mockMvc.perform(get("/users/{user}/books", JOHN_DOE_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$[0].uuid", is("1e4014b1-a551-4310-9f30-590c3140b695")))
            .andExpect(jsonPath("$[0].isbn10", is("1-61729-310-5")))
            .andExpect(jsonPath("$[0].isbn13", is("978-1-61729-310-8")))
            .andExpect(jsonPath("$[0].title", is("Get Programming with JavaScript")))
            .andExpect(jsonPath("$[0].authors[0].firstName", is("John R.")))
            .andExpect(jsonPath("$[0].authors[0].lastName", is("Larsen")))
            .andExpect(jsonPath("$[0].pages", is(406)))
            .andDo(document("{class-name}/{method-name}",
                pathParameters(parameterWithName("user").description("User id")),
                responseFields(
                    fieldWithPath("[].uuid").description("UUID used to identify a book"),
                    fieldWithPath("[].isbn10").description("10 digits ISBN (optional)").optional(),
                    fieldWithPath("[].isbn13").description("13 digits ISBN (optional)").optional(),
                    fieldWithPath("[].title").description("Book title"),
                    fieldWithPath("[].authors").description("Book authors (optional)").optional(),
                    fieldWithPath("[].authors[].firstName").description("First name"),
                    fieldWithPath("[].authors[].firstName").description("Last name"),
                    fieldWithPath("[].pages").description("Number of pages")
                )));
    }

    @Test
    public void createUserBook() throws Exception {
        Book book = getBook("1e4014b1-a551-4310-9f30-590c3140b695-request.json");
        when(booksDao.getUserBook(JOHN_DOE_USER, book.getUuid())).thenReturn(Optional.empty());
        when(booksDao.createUserBook(JOHN_DOE_USER, book)).thenReturn("1e4014b1-a551-4310-9f30-590c3140b695");

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
                    fieldWithPath("authors[].firstName").description("First name"),
                    fieldWithPath("authors[].firstName").description("Last name"),
                    fieldWithPath("pages").description("Number of pages")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.LOCATION).description("New added book resource")
                )));
    }

    @Test
    public void createUserExistingBook() throws Exception {
        Book book = getBook("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(booksDao.getUserBooks(JOHN_DOE_USER)).thenReturn(Arrays.asList(book));

        this.mockMvc.perform(post("/users/{user}/books", JOHN_DOE_USER)
            .content(getBookJson("1e4014b1-a551-4310-9f30-590c3140b695-request.json"))
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isForbidden())
            .andDo(document("{class-name}/{method-name}"));
    }

    @Test
    public void getUserBook() throws Exception {
        String uuid = "1e4014b1-a551-4310-9f30-590c3140b695";
        Book book = getBook(uuid + ".json");
        when(booksDao.getUserBook(JOHN_DOE_USER, uuid)).thenReturn(Optional.of(book));

        this.mockMvc.perform(get("/users/{user}/books/{uuid}", JOHN_DOE_USER, uuid))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("uuid", is("1e4014b1-a551-4310-9f30-590c3140b695")))
            .andExpect(jsonPath("isbn10", is("1-61729-310-5")))
            .andExpect(jsonPath("isbn13", is("978-1-61729-310-8")))
            .andExpect(jsonPath("title", is("Get Programming with JavaScript")))
            .andExpect(jsonPath("authors[0].firstName", is("John R.")))
            .andExpect(jsonPath("authors[0].lastName", is("Larsen")))
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
                    fieldWithPath("authors[].firstName").description("First name"),
                    fieldWithPath("authors[].firstName").description("Last name"),
                    fieldWithPath("pages").description("Number of pages")
                )));
    }

    @Test
    public void getUserMissingBook() throws Exception {
        String uuid = "missing-uuid-1";
        when(booksDao.getUserBook(JOHN_DOE_USER, uuid)).thenReturn(Optional.empty());

        this.mockMvc.perform(get("/users/{user}/books/{uuid}", JOHN_DOE_USER, uuid))
            .andExpect(status().isNotFound())
            .andDo(document("{class-name}/{method-name}"));
    }

    @Test
    public void updateUserBook() throws Exception {
        Book updateBook = getBook("1e4014b1-a551-4310-9f30-590c3140b695-update.json");
        Book updateBookRequest = getBook("1e4014b1-a551-4310-9f30-590c3140b695-update-request.json");
        when(booksDao.updateUserBook(JOHN_DOE_USER, updateBook.getUuid(), updateBookRequest))
            .thenReturn(Optional.of(updateBook.getUuid()));

        this.mockMvc.perform(put("/users/{user}/books/{uuid}", JOHN_DOE_USER, updateBook.getUuid())
            .content(getBookJson("1e4014b1-a551-4310-9f30-590c3140b695-update-request.json"))
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andDo(document("{class-name}/{method-name}",
                pathParameters(
                    parameterWithName("user").description("User id"),
                    parameterWithName("uuid").description("Book uuid")),
                requestFields(
                    fieldWithPath("isbn10").description("10 digits ISBN (optional)").optional(),
                    fieldWithPath("isbn13").description("13 digits ISBN (optional)").optional(),
                    fieldWithPath("title").description("Book title"),
                    fieldWithPath("authors").description("Book authors (optional)").optional(),
                    fieldWithPath("authors[].firstName").description("First name"),
                    fieldWithPath("authors[].firstName").description("Last name"),
                    fieldWithPath("pages").description("Number of pages")
                )));
    }

    @Test
    public void updateUserMissingBook() throws Exception {
        Book updateBook = getBook("1e4014b1-a551-4310-9f30-590c3140b695-update.json");
        Book updateBookRequest = getBook("1e4014b1-a551-4310-9f30-590c3140b695-update-request.json");
        when(booksDao.updateUserBook(JOHN_DOE_USER, updateBook.getUuid(), updateBookRequest)).thenReturn(Optional.empty());

        this.mockMvc.perform(put("/users/{user}/books/{uuid}", JOHN_DOE_USER, updateBook.getUuid())
            .content(getBookJson("1e4014b1-a551-4310-9f30-590c3140b695-update-request.json"))
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isNotFound())
            .andDo(document("{class-name}/{method-name}"));
    }

    @Test
    public void deleteUserBook() throws Exception {
        Book book = getBook("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(booksDao.getUserBook(JOHN_DOE_USER, book.getUuid())).thenReturn(Optional.of(book));

        this.mockMvc.perform(delete("/users/{user}/books/{uuid}", JOHN_DOE_USER, book.getUuid()))
            .andExpect(status().isOk())
            .andDo(document("{class-name}/{method-name}",
                pathParameters(
                    parameterWithName("user").description("User id"),
                    parameterWithName("uuid").description("Book uuid"))));

        verify(booksDao).deleteUserBook(JOHN_DOE_USER, book.getUuid());
    }

    @Test
    public void deleteUserMissingBook() throws Exception {
        Book book = getBook("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(booksDao.getUserBook(JOHN_DOE_USER, book.getUuid())).thenReturn(Optional.empty());

        this.mockMvc.perform(delete("/users/{user}/books/{uuid}", JOHN_DOE_USER, book.getUuid()))
            .andExpect(status().isNotFound())
            .andDo(document("{class-name}/{method-name}"));

        verify(booksDao, times(0)).deleteUserBook(JOHN_DOE_USER, book.getUuid());
    }
}
