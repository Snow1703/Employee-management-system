package com.prashant.ems.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        // Forward root requests to the static index.html
        return "forward:/index.html";
    }
}

