package com.todo.aitodo.controller;

import com.todo.aitodo.dto.LoginRequest;
import com.todo.aitodo.dto.LoginResponse;
import com.todo.aitodo.model.User;
import com.todo.aitodo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.register(user.getUsername(), user.getPassword());
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userService.login(request.getUsername(), request.getPassword());
    }
}