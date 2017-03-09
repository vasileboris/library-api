package com.espressoprogrammer.library.persistence;

import com.espressoprogrammer.library.dto.ReadingSession;

import java.util.List;
import java.util.Optional;

public interface ReadingSessionsDao {

    List<ReadingSession> getUserReadingSessions(String user);

    ReadingSession createUserReadingSession(String user, ReadingSession readingSession);

    Optional<ReadingSession> getUserReadingSession(String user, String uuid);

    Optional<String> updateUserReadingSession(String user, String uuid, ReadingSession readingSession);

    Optional<String> deleteUserReadingSession(String user, String uuid);
}
