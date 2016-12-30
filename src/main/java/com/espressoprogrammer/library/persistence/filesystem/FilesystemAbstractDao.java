package com.espressoprogrammer.library.persistence.filesystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

abstract class FilesystemAbstractDao<T> {
    protected static final String FILE_EXTENSION = ".json";

    @Autowired
    protected FilesystemConfiguration filesystemConfiguration;


    protected T fromJson(Path path) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(path.toFile(),
                (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        } catch (IOException ex) {
            throw new FilesystemDaoException(ex);
        }
    }

    protected String toJson(T t) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            objectMapper.writeValue(out, t);
            return out.toString();
        } catch (IOException ex) {
            throw new FilesystemDaoException(ex);
        }
    }

    protected String createStorageFolderIfMissing(String user) throws IOException {
        return createFolderIfMissing(user, u -> getStorageFolder(u));
    }

    protected String createFolderIfMissing(String user, Function<String, String> getFolder) throws IOException {
        String folder = getFolder.apply(user);
        createFolderIfMissing(folder);
        return folder;
    }

    protected abstract String getStorageFolder(String user);

    private void createFolderIfMissing(String folder) throws IOException {
        Path path = Paths.get(folder);
        if(!path.toFile().exists()) {
            Files.createDirectories(path);
        }
    }

}
