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

import static com.espressoprogrammer.library.LibraryTestUtil.getBook;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
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
        books.add(getBook("978-1-61729-310-8.json"));
        when(booksDao.getUserBooks(JOHN_DOE_USER)).thenReturn(books);

        this.mockMvc.perform(get("/users/{user}/books", JOHN_DOE_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$[0].isbn10", is("1-61729-310-5")))
            .andDo(document("{class-name}/{method-name}",
                pathParameters(parameterWithName("user").description("User id")),
                responseFields(
                    fieldWithPath("[].isbn10").description("10 digits ISBN"),
                    fieldWithPath("[].isbn13").description("13 digits ISBN"),
                    fieldWithPath("[].title").description("Book title"),
                    fieldWithPath("[].authors").description("Book authors"),
                    fieldWithPath("[].authors[].firstName").description("First name"),
                    fieldWithPath("[].authors[].firstName").description("Last name"),
                    fieldWithPath("[].pages").description("Number of pages")
                )));
    }

    @Test
    public void getUserBooksUnexpectedError() throws Exception {
        when(booksDao.getUserBooks(JOHN_DOE_USER)).thenThrow(new RuntimeException("Something bad happened"));

        this.mockMvc.perform(get("/users/{user}/books", JOHN_DOE_USER))
            .andExpect(status().is5xxServerError())
            .andDo(document("{class-name}/{method-name}"));
    }

    @Test
    public void createUserBook() throws Exception {
        Book book = getBook("978-1-61729-310-8.json");
        when(booksDao.createBook(JOHN_DOE_USER, book)).thenReturn("978-1-61729-310-8");

        this.mockMvc.perform(post("/users/{user}/books", JOHN_DOE_USER)
                .content(getBookJson("978-1-61729-310-8.json"))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, "/users/" + JOHN_DOE_USER + "/books/978-1-61729-310-8"))
            .andDo(document("{class-name}/{method-name}",
                pathParameters(parameterWithName("user").description("User id")),
                requestFields(
                    fieldWithPath("isbn10").description("10 digits ISBN"),
                    fieldWithPath("isbn13").description("13 digits ISBN"),
                    fieldWithPath("title").description("Book title"),
                    fieldWithPath("authors").description("Book authors"),
                    fieldWithPath("authors[].firstName").description("First name"),
                    fieldWithPath("authors[].firstName").description("Last name"),
                    fieldWithPath("pages").description("Number of pages")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.LOCATION).description("New added book's resource")
                )));
    }
}
