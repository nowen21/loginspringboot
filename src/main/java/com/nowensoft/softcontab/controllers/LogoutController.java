package com.nowensoft.softcontab.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class LogoutController {
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Invalida la sesión y elimina la autenticación
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();

        // Opcional: Borra cookies manualmente
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // Expira inmediatamente
        response.addCookie(cookie);

        return "redirect:/"; // Redirige al índice después del logout
    }
}
