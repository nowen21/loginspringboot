package com.nowensoft.softcontab.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class LoginController { 
     private final HttpSessionRequestCache requestCache = new HttpSessionRequestCache();

    @GetMapping("/login")
    public String login(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        // Si el usuario ya está autenticado, redirigir a la página anterior o a /home
        if (authentication != null && authentication.isAuthenticated()) {
            SavedRequest savedRequest = requestCache.getRequest(request, response);
            if (savedRequest != null) {
                return "redirect:" + savedRequest.getRedirectUrl(); // Redirige a la URL original
            }
            return "redirect:/home"; // Si no hay URL guardada, redirige a /home
        }
        return "login"; // Muestra la página de login
    }
}
