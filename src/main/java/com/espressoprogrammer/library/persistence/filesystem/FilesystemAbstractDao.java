package com.espressoprogrammer.library.persistence.filesystem;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

abstract class FilesystemAbstractDao {
    protected static final String FILE_EXTENSION = ".json";

    @Autowired
    protected FilesystemConfiguration filesystemConfiguration;


    protected String createBooksFolderIfMissing(String user) throws IOException {
        String booksFolder = getBooksFolder(user);
        createFolderIfMissing(booksFolder);
        return booksFolder;
    }

    private String getBooksFolder(String user) {
        return filesystemConfiguration.getLibraryFolder() + "/" + user + "/books";
    }

    private void createFolderIfMissing(String folder) throws IOException {
        Path path = Paths.get(folder);
        if(!path.toFile().exists()) {
            Files.createDirectories(path);
        }
    }

}
