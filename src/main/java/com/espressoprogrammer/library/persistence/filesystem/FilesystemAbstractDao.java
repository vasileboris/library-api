package com.espressoprogrammer.library.persistence.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

abstract class FilesystemAbstractDao {

    protected void createFolderIfMissing(String folder) throws IOException {
        Path path = Paths.get(folder);
        if(!path.toFile().exists()) {
            Files.createDirectories(path);
        }
    }

}
