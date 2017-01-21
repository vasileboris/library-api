package com.espressoprogrammer.library.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LibraryController {

    @RequestMapping("/")
    public String root() {
        return "search";
    }

    @RequestMapping("/search")
    public String search() {
        return "search";
    }
}
