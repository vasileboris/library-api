package com.espressoprogrammer.library.persistence;

import com.espressoprogrammer.library.dto.ReadingSession;

import java.util.List;
import java.util.Optional;

public interface ReadingSessionsDao {

    List<ReadingSession> getUserReadingSessions(String user);

    String createUserReadingSession(String user, ReadingSession readingSession);

    Optional<ReadingSession> getUserReadingSession(String user, String uuid);

    Optional<ReadingSession> updateUserReadingSession(String user, ReadingSession readingSession);

    void deleteUserReadingSession(String user, String uuid);
}
