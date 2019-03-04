package com.espressoprogrammer.library.persistence.filesystem;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
class FilesystemConfiguration {

    @Value(value = "${filesystem.dao.rootfolder:}")
    private String libraryFolder;

    String getLibraryFolder() {
        if(!StringUtils.isEmpty(libraryFolder)) {
            return libraryFolder;
        }
        return System.getProperty("user.home") + "/Library";
    }
}
