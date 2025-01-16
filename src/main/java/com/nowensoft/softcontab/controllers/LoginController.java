package com.nowensoft.softcontab.controllers;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController { 
    @GetMapping("/login")
    public String login() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    System.out.println("mi password: "+encoder.encode("N0w3n2104$"));
        return "login"; // Nombre de la plantilla Thymeleaf
    }
}
