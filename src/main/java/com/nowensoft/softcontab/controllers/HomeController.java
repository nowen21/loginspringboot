package com.nowensoft.softcontab.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/home")
    public String home() {
        return "home"; // Nombre de la plantilla Thymeleaf
    }
}
