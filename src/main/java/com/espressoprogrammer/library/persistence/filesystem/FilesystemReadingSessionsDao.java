package com.espressoprogrammer.library.persistence.filesystem;

import com.espressoprogrammer.library.dto.ReadingSession;
import com.espressoprogrammer.library.persistence.ReadingSessionsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class FilesystemReadingSessionsDao extends FilesystemAbstractDao<ReadingSession> implements ReadingSessionsDao {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<ReadingSession> getUserReadingSessions(String user) {
        return null;
    }

    @Override
    public String createUserReadingSession(String user, ReadingSession readingSession) {
        return null;
    }

    @Override
    public Optional<ReadingSession> getUserReadingSession(String user, String uuid) {
        return null;
    }

    @Override
    public Optional<ReadingSession> updateUserReadingSession(String user, ReadingSession readingSession) {
        return null;
    }

    @Override
    public void deleteUserReadingSession(String user, String uuid) {

    }

    @Override
    protected String getStorageFolder(String user) {
        return filesystemConfiguration.getLibraryFolder() + "/" + user + "/reading-sessions";
    }

}
