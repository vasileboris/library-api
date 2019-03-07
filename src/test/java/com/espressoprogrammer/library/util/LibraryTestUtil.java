package com.espressoprogrammer.library.util;

import com.espressoprogrammer.library.dto.Book;
import com.espressoprogrammer.library.dto.DateReadingSession;
import com.espressoprogrammer.library.dto.ReadingSession;
import com.espressoprogrammer.library.dto.ReadingSessionProgress;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.util.stream.Collectors.joining;

public class LibraryTestUtil {

    public static void copyBook(String fileName, String folder) throws IOException {
        Files.copy(LibraryTestUtil.class.getResourceAsStream("/json/books/" + fileName), Paths.get(folder + "/" + fileName));
    }

    public static void copyReadingSession(String fileName, String folder) throws IOException {
        Files.copy(LibraryTestUtil.class.getResourceAsStream("/json/reading-sessions/" + fileName), Paths.get(folder + "/" + fileName));
    }

    public static Book getTestBook(String fileName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(LibraryTestUtil.class.getResourceAsStream("/json/books/" + fileName), Book.class);
    }

    public static ReadingSession getTestReadingSession(String fileName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(LibraryTestUtil.class.getResourceAsStream("/json/reading-sessions/" + fileName), ReadingSession.class);
    }

    public static DateReadingSession getTestDateReadingSession(String fileName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(LibraryTestUtil.class.getResourceAsStream("/json/reading-sessions/" + fileName), DateReadingSession.class);
    }

    public static ReadingSessionProgress getTestReadingSessionProgress(String fileName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(LibraryTestUtil.class.getResourceAsStream("/json/reading-sessions/" + fileName), ReadingSessionProgress.class);
    }

    public static String getTestBookJson(String fileName) throws IOException, URISyntaxException {
        return Files.readAllLines(Paths.get(LibraryTestUtil.class.getResource("/json/books/" + fileName).toURI())).stream()
            .collect(joining("\n"));
    }

    public static String getTestReadingSessionJson(String fileName) throws IOException, URISyntaxException {
        return Files.readAllLines(Paths.get(LibraryTestUtil.class.getResource("/json/reading-sessions/" + fileName).toURI())).stream()
            .collect(joining("\n"));
    }

    public static String getTestDateReadingSessionJson(String fileName) throws IOException, URISyntaxException {
        return getTestReadingSessionJson(fileName);
    }

    public static String getTestReadingSessionProgressJson(String fileName) throws IOException, URISyntaxException {
        return getTestReadingSessionJson(fileName);
    }

}
