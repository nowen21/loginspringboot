package com.nowensoft.softcontab.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {
    @GetMapping("/error/403")
    public String accessDenied() {
        return "error/403"; // Vista para el error 403
    }
}
