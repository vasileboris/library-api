package com.espressoprogrammer.library.persistence.filesystem;

import com.espressoprogrammer.library.dto.Book;
import com.espressoprogrammer.library.persistence.BooksDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Repository
public class FilesystemBooksDao extends FilesystemAbstractDao implements BooksDao {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String BOOK_EXTENSION = ".json";

    @Autowired
    private FilesystemConfiguration filesystemConfiguration;

    @Override
    public List<Book> getUserBooks(String user) {
        try {
            String booksFolder = getBooksFolder(user);
            logger.debug("Looking for books into {}", booksFolder);
            createFolderIfMissing(booksFolder);
            return Files.list(Paths.get(booksFolder))
                .filter(p -> p.getFileName().toFile().getName().endsWith(BOOK_EXTENSION))
                .map(p -> fromJson(p))
                .collect(toList());
        } catch(FilesystemDaoException ex) {
            throw ex;
        } catch(Exception ex) {
            throw new FilesystemDaoException(ex);
        }
    }

    @Override
    public String createBook(String user, Book book) {
        try {
            String booksFolder = getBooksFolder(user);
            logger.debug("Adding new book into {}", booksFolder);
            createFolderIfMissing(booksFolder);

            String isbn = book.getIsbn13() != null ? book.getIsbn13() : book.getIsbn10();
            Files.write(Paths.get(booksFolder, isbn + BOOK_EXTENSION), toJson(book).getBytes());
            return isbn;
        } catch(Exception ex) {
            throw new FilesystemDaoException(ex);
        }
    }

    private String getBooksFolder(String user) {
        return filesystemConfiguration.getLibraryFolder() + "/" + user + "/books";
    }

    private Book fromJson(Path path) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(path.toFile(), Book.class);
        } catch (IOException ex) {
            throw new FilesystemDaoException(ex);
        }
    }

    private String toJson(Book book) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            objectMapper.writeValue(out, book);
            return out.toString();
        } catch (IOException ex) {
            throw new FilesystemDaoException(ex);
        }
    }

}
