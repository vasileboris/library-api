package com.espressoprogrammer.library.util;

import com.espressoprogrammer.library.dto.Book;
import com.espressoprogrammer.library.dto.DateReadingSession;
import com.espressoprogrammer.library.dto.ReadingSession;
import com.espressoprogrammer.library.dto.ReadingSessionProgress;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    public static String getTestBookJson(String fileName) throws IOException {
        return readTestJson(LibraryTestUtil.class.getResourceAsStream("/json/books/" + fileName));
    }

    public static String getTestReadingSessionJson(String fileName) throws IOException {
        return readTestJson(LibraryTestUtil.class.getResourceAsStream("/json/reading-sessions/" + fileName));
    }

    public static String getTestDateReadingSessionJson(String fileName) throws IOException {
        return getTestReadingSessionJson(fileName);
    }

    private static String readTestJson(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        }
        return result.toString();
    }
}
