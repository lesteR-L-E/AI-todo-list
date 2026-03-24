package com.todo.aitodo.controller;

import com.todo.aitodo.security.JwtUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}