package com.espressoprogrammer.library.persistence.filesystem;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

abstract class FilesystemAbstractDao {
    protected static final String FILE_EXTENSION = ".json";

    @Autowired
    protected FilesystemConfiguration filesystemConfiguration;


    protected String createBooksFolderIfMissing(String user) throws IOException {
        return createFolderIfMissing(user, u -> getBooksFolder(u));
    }

    protected String createReadingSessionsFolderIfMissing(String user) throws IOException {
        return createFolderIfMissing(user, u -> getReadingSessionsFolder(u));
    }

    private String createFolderIfMissing(String user, Function<String, String> getFolder) throws IOException {
        String folder = getFolder.apply(user);
        createFolderIfMissing(folder);
        return folder;
    }

    private String getBooksFolder(String user) {
        return filesystemConfiguration.getLibraryFolder() + "/" + user + "/books";
    }

    private String getReadingSessionsFolder(String user) {
        return filesystemConfiguration.getLibraryFolder() + "/" + user + "/reading-sessions";
    }

    private void createFolderIfMissing(String folder) throws IOException {
        Path path = Paths.get(folder);
        if(!path.toFile().exists()) {
            Files.createDirectories(path);
        }
    }

}
