package com.espressoprogrammer.library.persistence.filesystem;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FilesystemConfiguration {

    @Value("${filesystem.dao.rootfolder}")
    private String libraryFolder;

    public String getLibraryFolder() {
        return libraryFolder;
    }
}
