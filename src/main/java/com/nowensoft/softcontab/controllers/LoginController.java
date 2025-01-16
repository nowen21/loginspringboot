package com.nowensoft.softcontab.controllers;

import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController { 
    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/home"; // Redirige a /home si ya está autenticado
        }
        return "login"; // Nombre de la plantilla Thymeleaf
    }
}
