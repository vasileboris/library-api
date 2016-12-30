package com.espressoprogrammer.library.persistence.filesystem;

import com.espressoprogrammer.library.dto.Book;
import com.espressoprogrammer.library.persistence.BooksDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Repository
public class FilesystemBooksDao extends FilesystemAbstractDao<Book> implements BooksDao {
    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public List<Book> getUserBooks(String user) {
        try {
            String storageFolder = createStorageFolderIfMissing(user);
            logger.debug("Look for books into {}", storageFolder);

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

    @Override
    public String createUserBook(String user, Book book) {
        try {
            String storageFolder = createStorageFolderIfMissing(user);
            logger.debug("Add new book into {}", storageFolder);

            String uuid = UUID.randomUUID().toString();
            Book persistedBook = new Book(uuid,
                book.getIsbn10(),
                book.getIsbn13(),
                book.getTitle(),
                book.getAuthors(),
                book.getPages());
            Files.write(Paths.get(storageFolder, uuid + FILE_EXTENSION), toJson(persistedBook).getBytes());
            return uuid;
        } catch(Exception ex) {
            throw new FilesystemDaoException(ex);
        }
    }

    @Override
    public Optional<Book> getUserBook(String user, String uuid) {
        try {
            String storageFolder = createStorageFolderIfMissing(user);
            logger.debug("Look for book with uuid {} into {}", uuid, storageFolder);

            Path pathToBook = Paths.get(storageFolder, uuid + FILE_EXTENSION);
            if(pathToBook.toFile().exists()) {
                return Optional.of(fromJson(pathToBook));
            } else {
                return Optional.empty();
            }
        } catch(Exception ex) {
            throw new FilesystemDaoException(ex);
        }
    }

    @Override
    public Optional<Book> updateUserBook(String user, Book book) {
        try {
            String storageFolder = createStorageFolderIfMissing(user);
            logger.debug("Update book with uuid {} for user {}", book.getUuid(), user);

            Path pathToBook = Paths.get(storageFolder, book.getUuid() + FILE_EXTENSION);
            if(pathToBook.toFile().exists()) {
                Files.write(pathToBook, toJson(book).getBytes());
                return Optional.of(book);
            } else {
                return Optional.empty();
            }
        } catch(Exception ex) {
            throw new FilesystemDaoException(ex);
        }
    }

    @Override
    public void deleteUserBook(String user, String uuid) {
        try {
            String storageFolder = createStorageFolderIfMissing(user);
            logger.debug("Delete book with uuid {} for user {}", uuid, user);

            Path pathToBook = Paths.get(storageFolder, uuid + FILE_EXTENSION);
            if(pathToBook.toFile().exists()) {
                pathToBook.toFile().delete();
            }
        } catch(Exception ex) {
            throw new FilesystemDaoException(ex);
        }
    }

    @Override
    protected String getStorageFolder(String user) {
        return filesystemConfiguration.getLibraryFolder() + "/" + user + "/books";
    }

}
