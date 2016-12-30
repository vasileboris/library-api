package com.espressoprogrammer.library.service;

import com.espressoprogrammer.library.dto.Book;
import com.espressoprogrammer.library.dto.ReadingSession;
import com.espressoprogrammer.library.persistence.BooksDao;
import com.espressoprogrammer.library.persistence.ReadingSessionsDao;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static com.espressoprogrammer.library.LibraryTestUtil.getBook;
import static com.espressoprogrammer.library.LibraryTestUtil.getReadingSession;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadingSessionsControllerTest {
    private static final String JOHN_DOE_USER = "johndoe";

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private ReadingSessionsDao readingSessionsDao;

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
            .apply(documentationConfiguration(this.restDocumentation))
            .build();
    }

    @Test
    public void getUserReadingSessions() throws Exception {
        ArrayList<ReadingSession> readingSessions = new ArrayList<>();
        readingSessions.add(getReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json"));
        when(readingSessionsDao.getUserReadingSessions(JOHN_DOE_USER)).thenReturn(readingSessions);

        this.mockMvc.perform(get("/users/{user}/reading-sessions", JOHN_DOE_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$[0].uuid", is("1e4014b1-a551-4310-9f30-590c3140b695")))
            .andExpect(jsonPath("$[0].bookUuid", is("1e4014b1-a551-4310-9f30-590c3140b695")))
            .andExpect(jsonPath("$[0].readingSessions[0].date", is("2017-01-01")))
            .andExpect(jsonPath("$[0].readingSessions[0].lastReadPage", is(32)))
            .andExpect(jsonPath("$[0].readingSessions[0].bookmark", is("Section 3.3")))
            .andDo(document("{class-name}/{method-name}",
                pathParameters(parameterWithName("user").description("User id")),
                responseFields(
                    fieldWithPath("[].uuid").description("UUID used to identify a reading session"),
                    fieldWithPath("[].bookUuid").description("UUID used to identify a book"),
                    fieldWithPath("[].readingSessions").description("Reading sessions"),
                    fieldWithPath("[].readingSessions[].date").description("Date of a reading session in the format yyyy-MM-dd"),
                    fieldWithPath("[].readingSessions[].lastReadPage").description("Last page that was read"),
                    fieldWithPath("[].readingSessions[].bookmark").description("Where to start next")
                )));
    }

}
