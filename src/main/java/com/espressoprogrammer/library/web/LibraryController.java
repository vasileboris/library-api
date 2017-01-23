package com.espressoprogrammer.library.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


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

    @RequestMapping("/edit-book")
    public String editBook(@RequestParam("uuid") String uuid) {
        return "edit-book";
    }

    @RequestMapping("/delete-book")
    public String deleteBook(@RequestParam("uuid") String uuid) {
        return "delete-book";
    }
}
