package com.espressoprogrammer.library.service;

import com.espressoprogrammer.library.dto.ReadingSession;
import com.espressoprogrammer.library.persistence.ReadingSessionsDao;
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
import java.util.Optional;

import static com.espressoprogrammer.library.LibraryTestUtil.getReadingSession;
import static com.espressoprogrammer.library.LibraryTestUtil.getReadingSessionJson;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
            .andExpect(jsonPath("$[0].dateReadingSessions[0].date", is("2017-01-01")))
            .andExpect(jsonPath("$[0].dateReadingSessions[0].lastReadPage", is(32)))
            .andExpect(jsonPath("$[0].dateReadingSessions[0].bookmark", is("Section 3.3")))
            .andDo(document("{class-name}/{method-name}",
                pathParameters(parameterWithName("user").description("User id")),
                responseFields(
                    fieldWithPath("[].uuid").description("UUID used to identify a reading session"),
                    fieldWithPath("[].bookUuid").description("UUID used to identify a book"),
                    fieldWithPath("[].dateReadingSessions").description("Reading sessions (optional)").optional(),
                    fieldWithPath("[].dateReadingSessions[].date").description("Date of a reading session in the format yyyy-MM-dd"),
                    fieldWithPath("[].dateReadingSessions[].lastReadPage").description("Last page that was read"),
                    fieldWithPath("[].dateReadingSessions[].bookmark").description("Where to start next (optional)").optional()
                )));
    }

    @Test
    public void createUserReadingSession() throws Exception {
        ReadingSession readingSession = getReadingSession("1e4014b1-a551-4310-9f30-590c3140b695-request.json");
        when(readingSessionsDao.createUserReadingSession(JOHN_DOE_USER, readingSession)).thenReturn("1e4014b1-a551-4310-9f30-590c3140b695");

        this.mockMvc.perform(post("/users/{user}/reading-sessions", JOHN_DOE_USER)
            .content(getReadingSessionJson("1e4014b1-a551-4310-9f30-590c3140b695-request.json"))
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, "/users/" + JOHN_DOE_USER + "/reading-sessions/1e4014b1-a551-4310-9f30-590c3140b695"))
            .andDo(document("{class-name}/{method-name}",
                pathParameters(parameterWithName("user").description("User id")),
                requestFields(
                    fieldWithPath("bookUuid").description("UUID used to identify a book"),
                    fieldWithPath("dateReadingSessions").description("Reading sessions (optional)").optional(),
                    fieldWithPath("dateReadingSessions[].date").description("Date of a reading session in the format yyyy-MM-dd"),
                    fieldWithPath("dateReadingSessions[].lastReadPage").description("Last page that was read"),
                    fieldWithPath("dateReadingSessions[].bookmark").description("Where to start next (optional)").optional()
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.LOCATION).description("New added reading session resource")
                )));
    }

    @Test
    public void getUserReadingSession() throws Exception {
        String uuid = "1e4014b1-a551-4310-9f30-590c3140b695";
        ReadingSession readingSession = getReadingSession(uuid + ".json");
        when(readingSessionsDao.getUserReadingSession(JOHN_DOE_USER, uuid)).thenReturn(Optional.of(readingSession));

        this.mockMvc.perform(get("/users/{user}/reading-sessions/{uuid}", JOHN_DOE_USER, uuid))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("uuid", is("1e4014b1-a551-4310-9f30-590c3140b695")))
            .andExpect(jsonPath("bookUuid", is("1e4014b1-a551-4310-9f30-590c3140b695")))
            .andExpect(jsonPath("dateReadingSessions[0].date", is("2017-01-01")))
            .andExpect(jsonPath("dateReadingSessions[0].lastReadPage", is(32)))
            .andExpect(jsonPath("dateReadingSessions[0].bookmark", is("Section 3.3")))
            .andDo(document("{class-name}/{method-name}",
                pathParameters(
                    parameterWithName("user").description("User id"),
                    parameterWithName("uuid").description("Reading session uuid")),
                responseFields(
                    fieldWithPath("uuid").description("UUID used to identify a reading session"),
                    fieldWithPath("bookUuid").description("UUID used to identify a book"),
                    fieldWithPath("dateReadingSessions").description("Reading sessions (optional)").optional(),
                    fieldWithPath("dateReadingSessions[].date").description("Date of a reading session in the format yyyy-MM-dd"),
                    fieldWithPath("dateReadingSessions[].lastReadPage").description("Last page that was read"),
                    fieldWithPath("dateReadingSessions[].bookmark").description("Where to start next (optional)").optional()
                )));
    }

    @Test
    public void getUserMissingReadingSession() throws Exception {
        String uuid = "missing-uuid-1";
        when(readingSessionsDao.getUserReadingSession(JOHN_DOE_USER, uuid)).thenReturn(Optional.empty());

        this.mockMvc.perform(get("/users/{user}/reading-sessions/{uuid}", JOHN_DOE_USER, uuid))
            .andExpect(status().isNotFound())
            .andDo(document("{class-name}/{method-name}"));
    }

    @Test
    public void deleteUserReadingSession() throws Exception {
        ReadingSession readingSession = getReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(readingSessionsDao.getUserReadingSession(JOHN_DOE_USER, readingSession.getUuid())).thenReturn(Optional.of(readingSession));

        this.mockMvc.perform(delete("/users/{user}/reading-sessions/{uuid}", JOHN_DOE_USER, readingSession.getUuid()))
            .andExpect(status().isOk())
            .andDo(document("{class-name}/{method-name}",
                pathParameters(
                    parameterWithName("user").description("User id"),
                    parameterWithName("uuid").description("Reading session uuid"))));

        verify(readingSessionsDao).deleteUserReadingSession(JOHN_DOE_USER, readingSession.getUuid());
    }

    @Test
    public void deleteUserMissingReadingSession() throws Exception {
        ReadingSession readingSession = getReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(readingSessionsDao.getUserReadingSession(JOHN_DOE_USER, readingSession.getUuid())).thenReturn(Optional.empty());

        this.mockMvc.perform(delete("/users/{user}/reading-sessions/{uuid}", JOHN_DOE_USER, readingSession.getUuid()))
            .andExpect(status().isNotFound())
            .andDo(document("{class-name}/{method-name}"));

        verify(readingSessionsDao, times(0)).deleteUserReadingSession(JOHN_DOE_USER, readingSession.getUuid());
    }

    @Test
    public void getDateReadingSessions() throws Exception {
        String uuid = "1e4014b1-a551-4310-9f30-590c3140b695";
        ReadingSession readingSession = getReadingSession(uuid + ".json");
        when(readingSessionsDao.getUserReadingSession(JOHN_DOE_USER, uuid)).thenReturn(Optional.of(readingSession));

        this.mockMvc.perform(get("/users/{user}/reading-sessions/{uuid}/date-reading-sessions", JOHN_DOE_USER, uuid))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$[0].date", is("2017-01-01")))
            .andExpect(jsonPath("$[0].lastReadPage", is(32)))
            .andExpect(jsonPath("$[0].bookmark", is("Section 3.3")))
            .andDo(document("{class-name}/{method-name}",
                pathParameters(
                    parameterWithName("user").description("User id"),
                    parameterWithName("uuid").description("Reading session uuid")),
                responseFields(
                    fieldWithPath("[].date").description("Date of a reading session in the format yyyy-MM-dd"),
                    fieldWithPath("[].lastReadPage").description("Last page that was read"),
                    fieldWithPath("[].bookmark").description("Where to start next (optional)").optional()
                )));
    }

    @Test
    public void createDateReadingSession() throws Exception {
        ReadingSession readingSession = getReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(readingSessionsDao.getUserReadingSession(JOHN_DOE_USER, "1e4014b1-a551-4310-9f30-590c3140b695")).thenReturn(Optional.of(readingSession));

        this.mockMvc.perform(post("/users/{user}/reading-sessions/{uuid}/date-reading-sessions", JOHN_DOE_USER, "1e4014b1-a551-4310-9f30-590c3140b695")
            .content(getReadingSessionJson("1e4014b1-a551-4310-9f30-590c3140b695-new-date-reading-session.json"))
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, "/users/" + JOHN_DOE_USER
                + "/reading-sessions/1e4014b1-a551-4310-9f30-590c3140b695"
                + "/date-reading-sessions/2017-01-02"))
            .andDo(document("{class-name}/{method-name}",
                pathParameters(
                    parameterWithName("user").description("User id"),
                    parameterWithName("uuid").description("Reading session uuid")
                ),
                requestFields(
                    fieldWithPath("date").description("Date of a reading session in the format yyyy-MM-dd"),
                    fieldWithPath("lastReadPage").description("Last page that was read"),
                    fieldWithPath("bookmark").description("Where to start next (optional)").optional()
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.LOCATION).description("New added reading session resource")
                )));

        verify(readingSessionsDao).updateUserReadingSession(JOHN_DOE_USER,
            "1e4014b1-a551-4310-9f30-590c3140b695",
            getReadingSession("1e4014b1-a551-4310-9f30-590c3140b695-update.json"));
    }

    @Test
    public void createDateReadingSessionExistingDate() throws Exception {
        ReadingSession readingSession = getReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(readingSessionsDao.getUserReadingSession(JOHN_DOE_USER, "1e4014b1-a551-4310-9f30-590c3140b695")).thenReturn(Optional.of(readingSession));

        this.mockMvc.perform(post("/users/{user}/reading-sessions/{uuid}/date-reading-sessions", JOHN_DOE_USER, "1e4014b1-a551-4310-9f30-590c3140b695")
            .content(getReadingSessionJson("1e4014b1-a551-4310-9f30-590c3140b695-existing-date-reading-session.json"))
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isForbidden())
            .andDo(document("{class-name}/{method-name}"));
    }
}
