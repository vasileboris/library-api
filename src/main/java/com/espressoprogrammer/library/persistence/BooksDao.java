package com.espressoprogrammer.library.persistence;

import com.espressoprogrammer.library.dto.Book;

import java.util.List;

public interface BooksDao {

    List<Book> getUserBooks(String user);

    String createBook(String user, Book book);

}
