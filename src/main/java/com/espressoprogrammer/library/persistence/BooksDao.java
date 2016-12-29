package com.espressoprogrammer.library.persistence;

import com.espressoprogrammer.library.dto.Book;

import java.util.List;
import java.util.Optional;

public interface BooksDao {

    List<Book> getUserBooks(String user);

    String createUserBook(String user, Book book);

    Optional<Book> getUserBook(String user, String isbn);

    Optional<Book> getUserBook(String user, Book book);
}
