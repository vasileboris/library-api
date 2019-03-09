package com.espressoprogrammer.library.api;

import com.espressoprogrammer.library.dto.DateReadingSession;
import com.espressoprogrammer.library.dto.ReadingSession;
import com.espressoprogrammer.library.dto.ReadingSessionProgress;
import com.espressoprogrammer.library.service.ReadingSessionsException;
import com.espressoprogrammer.library.service.ReadingSessionsService;
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

import java.time.LocalDate;
import java.util.ArrayList;

import static com.espressoprogrammer.library.util.LibraryTestUtil.getTestDateReadingSession;
import static com.espressoprogrammer.library.util.LibraryTestUtil.getTestDateReadingSessionJson;
import static com.espressoprogrammer.library.util.LibraryTestUtil.getTestReadingSession;
import static com.espressoprogrammer.library.util.LibraryTestUtil.getTestReadingSessionJson;
import static com.espressoprogrammer.library.util.LibraryTestUtil.getTestReadingSessionProgress;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
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
    private static final String BOOK_UUID = "1e4014b1-a551-4310-9f30-590c3140b695";

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private ReadingSessionsService readingSessionsService;

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
        readingSessions.add(getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json"));
        when(readingSessionsService.getUserReadingSessions(JOHN_DOE_USER, BOOK_UUID)).thenReturn(readingSessions);

        this.mockMvc.perform(get("/users/{user}/books/{bookUuid}/reading-sessions", JOHN_DOE_USER, BOOK_UUID))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$[0].uuid", is("1e4014b1-a551-4310-9f30-590c3140b695")))
            .andExpect(jsonPath("$[0].bookUuid", is("1e4014b1-a551-4310-9f30-590c3140b695")))
            .andExpect(jsonPath("$[0].deadline", is("2017-01-31")))
            .andExpect(jsonPath("$[0].dateReadingSessions[0].date", is("2017-01-01")))
            .andExpect(jsonPath("$[0].dateReadingSessions[0].lastReadPage", is(32)))
            .andExpect(jsonPath("$[0].dateReadingSessions[0].bookmark", is("Section 3.3")))
            .andDo(document("{class-name}/{method-name}",
                pathParameters(
                    parameterWithName("user").description("User id"),
                    parameterWithName("bookUuid").description("Book uuid")),
                responseFields(
                    fieldWithPath("[].uuid").description("UUID used to identify a reading session"),
                    fieldWithPath("[].bookUuid").description("UUID used to identify a book"),
                    fieldWithPath("[].deadline").description("When book is expected to be read (optional)"),
                    fieldWithPath("[].dateReadingSessions").description("Reading sessions (optional)").optional(),
                    fieldWithPath("[].dateReadingSessions[].date").description("Date of a reading session in the format yyyy-MM-dd"),
                    fieldWithPath("[].dateReadingSessions[].lastReadPage").description("Last page that was read"),
                    fieldWithPath("[].dateReadingSessions[].bookmark").description("Where to start next")
                )));
    }

    @Test
    public void getUserCurrentReadingSession() throws Exception {
        ReadingSession readingSession = getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(readingSessionsService.getUserCurrentReadingSession(JOHN_DOE_USER, BOOK_UUID)).thenReturn(readingSession);

        this.mockMvc.perform(get("/users/{user}/books/{bookUuid}/current-reading-session", JOHN_DOE_USER, BOOK_UUID))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("uuid", is("1e4014b1-a551-4310-9f30-590c3140b695")))
            .andExpect(jsonPath("bookUuid", is("1e4014b1-a551-4310-9f30-590c3140b695")))
            .andExpect(jsonPath("deadline", is("2017-01-31")))
            .andExpect(jsonPath("dateReadingSessions[0].date", is("2017-01-01")))
            .andExpect(jsonPath("dateReadingSessions[0].lastReadPage", is(32)))
            .andExpect(jsonPath("dateReadingSessions[0].bookmark", is("Section 3.3")))
            .andDo(document("{class-name}/{method-name}",
                pathParameters(
                    parameterWithName("user").description("User id"),
                    parameterWithName("bookUuid").description("Book uuid")),
                responseFields(
                    fieldWithPath("uuid").description("UUID used to identify a reading session"),
                    fieldWithPath("bookUuid").description("UUID used to identify a book"),
                    fieldWithPath("deadline").description("When book is expected to be read (optional)"),
                    fieldWithPath("dateReadingSessions").description("Reading sessions (optional)").optional(),
                    fieldWithPath("dateReadingSessions[].date").description("Date of a reading session in the format yyyy-MM-dd"),
                    fieldWithPath("dateReadingSessions[].lastReadPage").description("Last page that was read"),
                    fieldWithPath("dateReadingSessions[].bookmark").description("Where to start next")
                )));
    }

    @Test
    public void getMissingUserCurrentReadingSession() throws Exception {
        when(readingSessionsService.getUserCurrentReadingSession(JOHN_DOE_USER, BOOK_UUID))
                .thenThrow(new ReadingSessionsException(ReadingSessionsException.Reason.READING_SESSION_NOT_FOUND));

        this.mockMvc.perform(get("/users/{user}/books/{bookUuid}/current-reading-session", JOHN_DOE_USER, BOOK_UUID))
            .andExpect(status().isNotFound())
            .andDo(document("{class-name}/{method-name}"));
    }

    @Test
    public void createUserReadingSession() throws Exception {
        ReadingSession readingSession = getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695-request.json");
        when(readingSessionsService.createUserReadingSession(JOHN_DOE_USER, BOOK_UUID, readingSession))
                .thenReturn(getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json"));

        this.mockMvc.perform(post("/users/{user}/books/{bookUuid}/reading-sessions", JOHN_DOE_USER, BOOK_UUID)
            .content(getTestReadingSessionJson("1e4014b1-a551-4310-9f30-590c3140b695-request.json"))
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION,
                "/users/" + JOHN_DOE_USER + "/books/" + BOOK_UUID + "/reading-sessions/1e4014b1-a551-4310-9f30-590c3140b695"))
            .andDo(document("{class-name}/{method-name}",
                pathParameters(
                    parameterWithName("user").description("User id"),
                    parameterWithName("bookUuid").description("Book uuid")),
                requestFields(
                    fieldWithPath("bookUuid").description("UUID used to identify a book"),
                    fieldWithPath("deadline").description("When book is expected to be read (optional)"),
                    fieldWithPath("dateReadingSessions").description("Reading sessions (optional)").optional(),
                    fieldWithPath("dateReadingSessions[].date").description("Date of a reading session in the format yyyy-MM-dd"),
                    fieldWithPath("dateReadingSessions[].lastReadPage").description("Last page that was read"),
                    fieldWithPath("dateReadingSessions[].bookmark").description("Where to start next")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.LOCATION).description("New added reading session resource")
                ),
                responseFields(
                    fieldWithPath("uuid").description("UUID used to identify a reading session"),
                    fieldWithPath("bookUuid").description("UUID used to identify a book"),
                    fieldWithPath("deadline").description("When book is expected to be read (optional)"),
                    fieldWithPath("dateReadingSessions").description("Reading sessions (optional)").optional(),
                    fieldWithPath("dateReadingSessions[].date").description("Date of a reading session in the format yyyy-MM-dd"),
                    fieldWithPath("dateReadingSessions[].lastReadPage").description("Last page that was read"),
                    fieldWithPath("dateReadingSessions[].bookmark").description("Where to start next")
                )));
    }

    @Test
    public void createAdditionalUserReadingSession() throws Exception {
        ReadingSession readingSession = getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695-request.json");
        when(readingSessionsService.createUserReadingSession(JOHN_DOE_USER, BOOK_UUID, readingSession))
                .thenThrow(new ReadingSessionsException(ReadingSessionsException.Reason.READING_SESSION_ALREADY_EXISTS));

        this.mockMvc.perform(post("/users/{user}/books/{bookUuid}/reading-sessions", JOHN_DOE_USER, BOOK_UUID)
            .content(getTestReadingSessionJson("1e4014b1-a551-4310-9f30-590c3140b695-request.json"))
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isForbidden())
            .andDo(document("{class-name}/{method-name}"));
    }

    @Test
    public void getUserReadingSession() throws Exception {
        String uuid = "1e4014b1-a551-4310-9f30-590c3140b695";
        ReadingSession readingSession = getTestReadingSession(uuid + ".json");
        when(readingSessionsService.getUserReadingSession(JOHN_DOE_USER, BOOK_UUID, uuid)).thenReturn(readingSession);

        this.mockMvc.perform(get("/users/{user}/books/{bookUuid}/reading-sessions/{uuid}", JOHN_DOE_USER, BOOK_UUID, uuid))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("uuid", is("1e4014b1-a551-4310-9f30-590c3140b695")))
            .andExpect(jsonPath("bookUuid", is("1e4014b1-a551-4310-9f30-590c3140b695")))
            .andExpect(jsonPath("deadline", is("2017-01-31")))
            .andExpect(jsonPath("dateReadingSessions[0].date", is("2017-01-01")))
            .andExpect(jsonPath("dateReadingSessions[0].lastReadPage", is(32)))
            .andExpect(jsonPath("dateReadingSessions[0].bookmark", is("Section 3.3")))
            .andDo(document("{class-name}/{method-name}",
                pathParameters(
                    parameterWithName("user").description("User id"),
                    parameterWithName("bookUuid").description("Book uuid"),
                    parameterWithName("uuid").description("Reading session uuid")),
                responseFields(
                    fieldWithPath("uuid").description("UUID used to identify a reading session"),
                    fieldWithPath("bookUuid").description("UUID used to identify a book"),
                    fieldWithPath("deadline").description("When book is expected to be read (optional)"),
                    fieldWithPath("dateReadingSessions").description("Reading sessions (optional)").optional(),
                    fieldWithPath("dateReadingSessions[].date").description("Date of a reading session in the format yyyy-MM-dd"),
                    fieldWithPath("dateReadingSessions[].lastReadPage").description("Last page that was read"),
                    fieldWithPath("dateReadingSessions[].bookmark").description("Where to start next")
                )));
    }

    @Test
    public void getMissingUserReadingSession() throws Exception {
        String uuid = "missing-uuid-1";
        when(readingSessionsService.getUserReadingSession(JOHN_DOE_USER, BOOK_UUID, uuid))
                .thenThrow(new ReadingSessionsException(ReadingSessionsException.Reason.READING_SESSION_NOT_FOUND));

        this.mockMvc.perform(get("/users/{user}/books/{bookUuid}/reading-sessions/{uuid}", JOHN_DOE_USER, BOOK_UUID, uuid))
            .andExpect(status().isNotFound())
            .andDo(document("{class-name}/{method-name}"));
    }

    @Test
    public void deleteUserReadingSession() throws Exception {
        ReadingSession readingSession = getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(readingSessionsService.deleteUserReadingSession(JOHN_DOE_USER, BOOK_UUID, readingSession.getUuid())).thenReturn(readingSession.getUuid());

        this.mockMvc.perform(delete("/users/{user}/books/{bookUuid}/reading-sessions/{uuid}",
            JOHN_DOE_USER,
            BOOK_UUID,
            readingSession.getUuid()))
            .andExpect(status().isOk())
            .andDo(document("{class-name}/{method-name}",
                pathParameters(
                    parameterWithName("user").description("User id"),
                    parameterWithName("bookUuid").description("Book uuid"),
                    parameterWithName("uuid").description("Reading session uuid"))));
    }

    @Test
    public void deleteMissingUserReadingSession() throws Exception {
        ReadingSession readingSession = getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(readingSessionsService.deleteUserReadingSession(JOHN_DOE_USER, BOOK_UUID, readingSession.getUuid()))
                .thenThrow(new ReadingSessionsException(ReadingSessionsException.Reason.READING_SESSION_NOT_FOUND));

        this.mockMvc.perform(delete("/users/{user}/books/{bookUuid}/reading-sessions/{uuid}",
            JOHN_DOE_USER, BOOK_UUID, readingSession.getUuid()))
            .andExpect(status().isNotFound())
            .andDo(document("{class-name}/{method-name}"));
    }

    @Test
    public void getDateReadingSessions() throws Exception {
        String uuid = "1e4014b1-a551-4310-9f30-590c3140b695";
        ReadingSession readingSession = getTestReadingSession(uuid + ".json");
        when(readingSessionsService.getUserReadingSession(JOHN_DOE_USER, BOOK_UUID, uuid)).thenReturn(readingSession);

        this.mockMvc.perform(get("/users/{user}/books/{bookUuid}/reading-sessions/{uuid}/date-reading-sessions",
            JOHN_DOE_USER, BOOK_UUID, uuid))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$[0].date", is("2017-01-01")))
            .andExpect(jsonPath("$[0].lastReadPage", is(32)))
            .andExpect(jsonPath("$[0].bookmark", is("Section 3.3")))
            .andDo(document("{class-name}/{method-name}",
                pathParameters(
                    parameterWithName("user").description("User id"),
                    parameterWithName("bookUuid").description("Book uuid"),
                    parameterWithName("uuid").description("Reading session uuid")),
                responseFields(
                    fieldWithPath("[].date").description("Date of a reading session in the format yyyy-MM-dd"),
                    fieldWithPath("[].lastReadPage").description("Last page that was read"),
                    fieldWithPath("[].bookmark").description("Where to start next")
                )));
    }

    @Test
    public void createDateReadingSession() throws Exception {
        ReadingSession readingSession = getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json");
        DateReadingSession dateReadingSession = getTestDateReadingSession("1e4014b1-a551-4310-9f30-590c3140b695-new-date-reading-session.json");
        when(readingSessionsService.createDateReadingSession(JOHN_DOE_USER, BOOK_UUID, readingSession.getUuid(), dateReadingSession))
                .thenReturn(dateReadingSession);

        this.mockMvc.perform(post("/users/{user}/books/{bookUuid}/reading-sessions/{uuid}/date-reading-sessions",
            JOHN_DOE_USER, BOOK_UUID, "1e4014b1-a551-4310-9f30-590c3140b695")
            .content(getTestDateReadingSessionJson("1e4014b1-a551-4310-9f30-590c3140b695-new-date-reading-session.json"))
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, "/users/" + JOHN_DOE_USER
                + "/reading-sessions/1e4014b1-a551-4310-9f30-590c3140b695"
                + "/date-reading-sessions/2017-01-02"))
            .andDo(document("{class-name}/{method-name}",
                pathParameters(
                    parameterWithName("user").description("User id"),
                    parameterWithName("bookUuid").description("Book uuid"),
                    parameterWithName("uuid").description("Reading session uuid")
                ),
                requestFields(
                    fieldWithPath("date").description("Date of a reading session in the format yyyy-MM-dd"),
                    fieldWithPath("lastReadPage").description("Last page that was read"),
                    fieldWithPath("bookmark").description("Where to start next")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.LOCATION).description("New added reading session resource")
                ),
                responseFields(
                    fieldWithPath("date").description("Date of a reading session in the format yyyy-MM-dd"),
                    fieldWithPath("lastReadPage").description("Last page that was read"),
                    fieldWithPath("bookmark").description("Where to start next")
                )));
    }

    @Test
    public void createDateReadingSessionExistingDate() throws Exception {
        DateReadingSession dateReadingSession = getTestDateReadingSession("1e4014b1-a551-4310-9f30-590c3140b695-existing-date-reading-session.json");
        when(readingSessionsService.createDateReadingSession(JOHN_DOE_USER, BOOK_UUID, "1e4014b1-a551-4310-9f30-590c3140b695", dateReadingSession))
                .thenThrow(new ReadingSessionsException(ReadingSessionsException.Reason.DATE_READING_SESSION_ALREADY_EXISTS));

        this.mockMvc.perform(post("/users/{user}/books/{bookUuid}/reading-sessions/{uuid}/date-reading-sessions",
            JOHN_DOE_USER,
            BOOK_UUID,
            "1e4014b1-a551-4310-9f30-590c3140b695")
            .content(getTestReadingSessionJson("1e4014b1-a551-4310-9f30-590c3140b695-existing-date-reading-session.json"))
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isForbidden())
            .andDo(document("{class-name}/{method-name}"));
    }

    @Test
    public void getDateReadingSession() throws Exception {
        String uuid = "1e4014b1-a551-4310-9f30-590c3140b695";
        ReadingSession readingSession = getTestReadingSession(uuid + ".json");
        DateReadingSession dateReadingSession = readingSession.getDateReadingSessions().get(0);
        String date = "2017-01-01";
        when(readingSessionsService.getDateReadingSession(JOHN_DOE_USER, BOOK_UUID, uuid, date)).thenReturn(dateReadingSession);

        this.mockMvc.perform(get("/users/{user}/books/{bookUuid}/reading-sessions/{uuid}/date-reading-sessions/{date}",
            JOHN_DOE_USER, BOOK_UUID, uuid, date))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("date", is("2017-01-01")))
            .andExpect(jsonPath("lastReadPage", is(32)))
            .andExpect(jsonPath("bookmark", is("Section 3.3")))
            .andDo(document("{class-name}/{method-name}",
                pathParameters(
                    parameterWithName("user").description("User id"),
                    parameterWithName("bookUuid").description("Book uuid"),
                    parameterWithName("uuid").description("Reading session uuid"),
                    parameterWithName("date").description("Reading session date in the format yyyy-MM-dd")),
                responseFields(
                    fieldWithPath("date").description("Date of a reading session in the format yyyy-MM-dd"),
                    fieldWithPath("lastReadPage").description("Last page that was read"),
                    fieldWithPath("bookmark").description("Where to start next")
                )));
    }

    @Test
    public void getMissingDateReadingSession() throws Exception {
        String uuid = "1e4014b1-a551-4310-9f30-590c3140b695";
        String date = "2017-01-02";
        when(readingSessionsService.getDateReadingSession(JOHN_DOE_USER, BOOK_UUID, uuid, date))
                .thenThrow(new ReadingSessionsException(ReadingSessionsException.Reason.DATE_READING_SESSION_NOT_FOUND));

        this.mockMvc.perform(get("/users/{user}/books/{bookUuid}/reading-sessions/{uuid}/date-reading-sessions/{date}",
            JOHN_DOE_USER, BOOK_UUID, uuid, date))
            .andExpect(status().isNotFound())
            .andDo(document("{class-name}/{method-name}"));
    }

    @Test
    public void updateDateReadingSession() throws Exception {
        ReadingSession readingSession = getTestReadingSession("1e4014b1-a551-4310-9f30-590c3140b695.json");
        when(readingSessionsService.getUserReadingSession(JOHN_DOE_USER, BOOK_UUID, "1e4014b1-a551-4310-9f30-590c3140b695")).thenReturn(readingSession);

        String date = "2017-01-01";
        this.mockMvc.perform(put("/users/{user}/books/{bookUuid}/reading-sessions/{uuid}/date-reading-sessions/{date}",
            JOHN_DOE_USER, BOOK_UUID, "1e4014b1-a551-4310-9f30-590c3140b695", date)
            .content(getTestReadingSessionJson("1e4014b1-a551-4310-9f30-590c3140b695-update-date-reading-session-request.json"))
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isNoContent())
            .andDo(document("{class-name}/{method-name}",
                pathParameters(
                    parameterWithName("user").description("User id"),
                    parameterWithName("bookUuid").description("Book uuid"),
                    parameterWithName("uuid").description("Reading session uuid"),
                    parameterWithName("date").description("Reading session date in the format yyyy-MM-dd")
                ),
                requestFields(
                    fieldWithPath("date").description("Date of a reading session in the format yyyy-MM-dd"),
                    fieldWithPath("lastReadPage").description("Last page that was read"),
                    fieldWithPath("bookmark").description("Where to start next")
                )));
    }

    @Test
    public void updateMissingDateReadingSession() throws Exception {
        DateReadingSession dateReadingSession = getTestDateReadingSession("1e4014b1-a551-4310-9f30-590c3140b695-update-date-reading-session-request.json");
        String date = "2017-01-02";
        when(readingSessionsService.updateDateReadingSession(JOHN_DOE_USER, BOOK_UUID, "1e4014b1-a551-4310-9f30-590c3140b695", date, dateReadingSession))
                .thenThrow(new ReadingSessionsException(ReadingSessionsException.Reason.DATE_READING_SESSION_NOT_FOUND));

        this.mockMvc.perform(put("/users/{user}/books/{bookUuid}/reading-sessions/{uuid}/date-reading-sessions/{date}",
            JOHN_DOE_USER, BOOK_UUID, "1e4014b1-a551-4310-9f30-590c3140b695", date)
            .content(getTestDateReadingSessionJson("1e4014b1-a551-4310-9f30-590c3140b695-update-date-reading-session-request.json"))
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isNotFound())
            .andDo(document("{class-name}/{method-name}"));
    }

    @Test
    public void deleteDateReadingSession() throws Exception {
        String date = "2017-01-01";
        when(readingSessionsService.deleteDateReadingSession(JOHN_DOE_USER, BOOK_UUID, "1e4014b1-a551-4310-9f30-590c3140b695", date)).thenReturn(date);

        this.mockMvc.perform(delete("/users/{user}/books/{bookUuid}/reading-sessions/{uuid}/date-reading-sessions/{date}",
            JOHN_DOE_USER,
            BOOK_UUID,
            "1e4014b1-a551-4310-9f30-590c3140b695",
            date))
            .andExpect(status().isNoContent())
            .andDo(document("{class-name}/{method-name}",
                pathParameters(
                    parameterWithName("user").description("User id"),
                    parameterWithName("bookUuid").description("Book uuid"),
                    parameterWithName("uuid").description("Reading session uuid"),
                    parameterWithName("date").description("Reading session date in the format yyyy-MM-dd")
                )));

    }

    @Test
    public void deleteMissingDateReadingSession() throws Exception {
        String date = "2017-01-02";
        when(readingSessionsService.deleteDateReadingSession(JOHN_DOE_USER, BOOK_UUID, "1e4014b1-a551-4310-9f30-590c3140b695", date))
                .thenThrow(new ReadingSessionsException(ReadingSessionsException.Reason.DATE_READING_SESSION_NOT_FOUND));

        this.mockMvc.perform(delete("/users/{user}/books/{bookUuid}/reading-sessions/{uuid}/date-reading-sessions/{date}",
            JOHN_DOE_USER, BOOK_UUID, "1e4014b1-a551-4310-9f30-590c3140b695", date))
            .andExpect(status().isNotFound())
            .andDo(document("{class-name}/{method-name}"));
    }

    @Test
    public void getUserReadingSessionProgress() throws Exception {
        String uuid = "1e4014b1-a551-4310-9f30-590c3140b695";
        ReadingSessionProgress expectedReadingSessionProgressTemplate = getTestReadingSessionProgress(uuid + "-one-reading-progress.json");
        String estimatedFinishDate = LocalDate.now().plusDays(expectedReadingSessionProgressTemplate.getEstimatedDaysLeft().intValue()).toString();
        ReadingSessionProgress expectedReadingSessionProgress = expectedReadingSessionProgressTemplate.copy(
                expectedReadingSessionProgressTemplate.getBookUuid(),
                expectedReadingSessionProgressTemplate.getLastReadPage(),
                expectedReadingSessionProgressTemplate.getPagesTotal(),
                expectedReadingSessionProgressTemplate.getReadPercentage(),
                expectedReadingSessionProgressTemplate.getAveragePagesPerDay(),
                expectedReadingSessionProgressTemplate.getEstimatedReadDaysLeft(),
                expectedReadingSessionProgressTemplate.getEstimatedDaysLeft(),
                estimatedFinishDate,
                expectedReadingSessionProgressTemplate.getDeadline());
        when(readingSessionsService.getUserReadingSessionProgress(JOHN_DOE_USER, BOOK_UUID, uuid)).thenReturn(expectedReadingSessionProgress);

        this.mockMvc.perform(get("/users/{user}/books/{bookUuid}/reading-sessions/{uuid}/progress",
                JOHN_DOE_USER, BOOK_UUID, uuid))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("bookUuid", is("1e4014b1-a551-4310-9f30-590c3140b695")))
                .andExpect(jsonPath("lastReadPage", is(100)))
                    .andExpect(jsonPath("pagesTotal", is(400)))
                .andExpect(jsonPath("readPercentage", is(25)))
                .andExpect(jsonPath("averagePagesPerDay", is(100)))
                .andExpect(jsonPath("estimatedReadDaysLeft", is(3)))
                .andExpect(jsonPath("estimatedDaysLeft", is(3)))
                .andExpect(jsonPath("estimatedFinishDate", is(estimatedFinishDate)))
                .andExpect(jsonPath("deadline", is("2019-03-31")))
                .andDo(document("{class-name}/{method-name}",
                        pathParameters(
                                parameterWithName("user").description("User id"),
                                parameterWithName("bookUuid").description("Book uuid"),
                                parameterWithName("uuid").description("Reading session uuid")),
                        responseFields(
                                fieldWithPath("bookUuid").description("Book uuid"),
                                fieldWithPath("lastReadPage").description("Last page that was read"),
                                fieldWithPath("pagesTotal").description("Total pages of the book"),
                                fieldWithPath("readPercentage").description("Reading progress in percentage"),
                                fieldWithPath("averagePagesPerDay").description("Pages read average"),
                                fieldWithPath("estimatedReadDaysLeft").description("How many remaining reading days are estimated"),
                                fieldWithPath("estimatedDaysLeft").description("How many remaining calendar days are estimated"),
                                fieldWithPath("estimatedFinishDate").description("Estimated finish date."),
                                fieldWithPath("deadline").description("The deadline of the reading session")
                        )));
    }

    @Test
    public void getUserReadingSessionProgressForMissingBook() throws Exception {
        String uuid = "1e4014b1-a551-4310-9f30-590c3140b695";
        when(readingSessionsService.getUserReadingSessionProgress(JOHN_DOE_USER, BOOK_UUID, uuid))
                .thenThrow(new ReadingSessionsException(ReadingSessionsException.Reason.DATE_READING_SESSION_NOT_FOUND));

        this.mockMvc.perform(get("/users/{user}/books/{bookUuid}/reading-sessions/{uuid}/progress",
                JOHN_DOE_USER, BOOK_UUID, uuid))
                .andExpect(status().isNotFound())
                .andDo(document("{class-name}/{method-name}"));
    }
}
