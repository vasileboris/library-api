package com.espressoprogrammer.library.persistence.filesystem;

import com.espressoprogrammer.library.dto.ReadingSession;
import com.espressoprogrammer.library.persistence.ReadingSessionsDao;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Repository
public class FilesystemReadingSessionsDao extends FilesystemAbstractDao<ReadingSession> implements ReadingSessionsDao {

    @Override
    public List<ReadingSession> getUserReadingSessions(String user, String bookUuid) {
        return getUserItems(user).stream()
            .filter(r -> r.getBookUuid().equals(bookUuid))
            .collect(toList());
    }

    @Override
    public ReadingSession createUserReadingSession(String user, String bookUuid, ReadingSession readingSession) {
        return createUserItem(user, readingSession);
    }

    @Override
    public Optional<ReadingSession> getUserReadingSession(String user, String bookUuid, String uuid) {
        return getUserItem(user, uuid);
    }

    @Override
    public Optional<String> updateUserReadingSession(String user, String bookUuid, String uuid, ReadingSession readingSession) {
        return updateUserItem(user, uuid, readingSession);
    }

    @Override
    public Optional<String> deleteUserReadingSession(String user, String bookUuid, String uuid) {
        return deleteUserItem(user, uuid);
    }

    @Override
    protected boolean applySearchCriteria(ReadingSession readingSession, String searchText) {
        return true;
    }

    @Override
    protected ReadingSession createItem(String uuid, ReadingSession readingSession) {
        return new ReadingSession(uuid,
            readingSession.getBookUuid(),
            readingSession.getDeadline(),
            new ArrayList<>(readingSession.getDateReadingSessions()));
    }

    @Override
    protected String getStorageFolder(String user) {
        return filesystemConfiguration.getLibraryFolder() + "/" + user + "/reading-sessions";
    }

}
