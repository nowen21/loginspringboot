package com.nowensoft.softcontab.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {
    @GetMapping
    @PreAuthorize("hasAuthority('PER_LIST_USERS')")
    public String list() {
        return "users/list"; // Nombre de la plantilla Thymeleaf
    }
}
