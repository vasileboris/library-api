package com.espressoprogrammer.library.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class LibraryController {

    @RequestMapping("/")
    public String index() {
        return "Library";
    }

    @RequestMapping("/books")
    public String manageBooks() {
        return index();
    }

    @RequestMapping("/books/{bookUuid}")
    public String manageReadingSessions() {
        return index();
    }

}
