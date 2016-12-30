package com.espressoprogrammer.library.persistence.filesystem;

import com.espressoprogrammer.library.dto.ReadingSession;
import com.espressoprogrammer.library.persistence.ReadingSessionsDao;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class FilesystemReadingSessionsDao extends FilesystemAbstractDao<ReadingSession> implements ReadingSessionsDao {

    @Override
    public List<ReadingSession> getUserReadingSessions(String user) {
        return getUserItems(user);
    }

    @Override
    public String createUserReadingSession(String user, ReadingSession readingSession) {
        return createUserItem(user, readingSession);
    }

    @Override
    public Optional<ReadingSession> getUserReadingSession(String user, String uuid) {
        return getUserItem(user, uuid);
    }

    @Override
    public Optional<String> updateUserReadingSession(String user, String uuid, ReadingSession readingSession) {
        return updateUserItem(user, uuid, readingSession);
    }

    @Override
    public void deleteUserReadingSession(String user, String uuid) {
        deleteUserItem(user, uuid);
    }

    @Override
    protected ReadingSession createItem(String uuid, ReadingSession readingSession) {
        return new ReadingSession(uuid,
            readingSession.getBookUuid(),
            new ArrayList<>(readingSession.getReadingSessions()));
    }

    @Override
    protected String getStorageFolder(String user) {
        return filesystemConfiguration.getLibraryFolder() + "/" + user + "/reading-sessions";
    }

}
