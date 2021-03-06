package com.espressoprogrammer.library.persistence;

import com.espressoprogrammer.library.dto.ReadingSession;

import java.util.List;
import java.util.Optional;

public interface ReadingSessionsDao {

    List<ReadingSession> getUserReadingSessions(String user, String bookUuid);

    ReadingSession createUserReadingSession(String user, String bookUuid, ReadingSession readingSession);

    Optional<ReadingSession> getUserReadingSession(String user, String bookUuid, String uuid);

    Optional<String> updateUserReadingSession(String user, String bookUuid, String uuid, ReadingSession readingSession);

    Optional<String> deleteUserReadingSession(String user, String bookUuid, String uuid);
}
