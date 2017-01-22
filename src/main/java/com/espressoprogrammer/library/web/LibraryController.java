package com.espressoprogrammer.library.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LibraryController {

    @RequestMapping("/")
    public String index() {
        return "search-books";
    }

    @RequestMapping("/search-books")
    public String searchBooks() {
        return "search-books";
    }

    @RequestMapping("/add-book")
    public String addBook() {
        return "add-book";
    }
}
