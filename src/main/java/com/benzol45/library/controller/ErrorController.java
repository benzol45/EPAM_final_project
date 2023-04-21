package com.benzol45.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {
    @GetMapping("/access_denied")
    public String getAccessDeniedPage() {
        return "/error/AccessDenied";
    }
}
