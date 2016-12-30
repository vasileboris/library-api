package com.espressoprogrammer.library.persistence.filesystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

abstract class FilesystemAbstractDao<T> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    protected static final String FILE_EXTENSION = ".json";

    protected List<T> getUserItems(String user) {
        try {
            String storageFolder = createStorageFolderIfMissing(user);
            logger.debug("Look for item into {}", storageFolder);

            return Files.list(Paths.get(storageFolder))
                .filter(p -> p.getFileName().toFile().getName().endsWith(FILE_EXTENSION))
                .map(p -> fromJson(p))
                .collect(toList());
        } catch(FilesystemDaoException ex) {
            throw ex;
        } catch(Exception ex) {
            throw new FilesystemDaoException(ex);
        }
    }

    protected String createUserItem(String user, T item) {
        try {
            String storageFolder = createStorageFolderIfMissing(user);
            logger.debug("Add new item into {}", storageFolder);

            String uuid = UUID.randomUUID().toString();
            T persistedItem = createItem(uuid, item);
            Files.write(Paths.get(storageFolder, uuid + FILE_EXTENSION), toJson(persistedItem).getBytes());
            return uuid;
        } catch(Exception ex) {
            throw new FilesystemDaoException(ex);
        }
    }

    protected Optional<T> getUserItem(String user, String id) {
        try {
            String storageFolder = createStorageFolderIfMissing(user);
            logger.debug("Look for item with uuid {} into {}", id, storageFolder);

            Path pathToItem = Paths.get(storageFolder, id + FILE_EXTENSION);
            if(pathToItem.toFile().exists()) {
                return Optional.of(fromJson(pathToItem));
            } else {
                return Optional.empty();
            }
        } catch(Exception ex) {
            throw new FilesystemDaoException(ex);
        }
    }

    protected Optional<String> updateUserItem(String user, String uuid, T item) {
        try {
            String storageFolder = createStorageFolderIfMissing(user);
            logger.debug("Update item with uuid {} for user {}", uuid, user);

            Path pathToItem = Paths.get(storageFolder, uuid + FILE_EXTENSION);
            if(pathToItem.toFile().exists()) {
                T persistedItem = createItem(uuid, item);
                Files.write(pathToItem, toJson(persistedItem).getBytes());
                return Optional.of(uuid);
            } else {
                return Optional.empty();
            }
        } catch(Exception ex) {
            throw new FilesystemDaoException(ex);
        }
    }

    protected void deleteUserItem(String user, String id) {
        try {
            String storageFolder = createStorageFolderIfMissing(user);
            logger.debug("Delete item with uuid {} for user {}", id, user);

            Path pathToItem = Paths.get(storageFolder, id + FILE_EXTENSION);
            if(pathToItem.toFile().exists()) {
                pathToItem.toFile().delete();
            }
        } catch(Exception ex) {
            throw new FilesystemDaoException(ex);
        }
    }

    protected abstract T createItem(String uuid, T item);

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
