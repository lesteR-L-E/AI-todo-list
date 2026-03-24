package com.todo.aitodo.service;

import com.todo.aitodo.model.User;
import com.todo.aitodo.repository.UserRepository;
import com.todo.aitodo.dto.LoginResponse;
import com.todo.aitodo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // 注册
    public User register(String username, String password) {

        // 1. 判空
        if (username == null || username.isEmpty() ||
                password == null || password.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户名或密码不能为空");
        }

        // 2. 检查用户是否存在
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户名已存在");
        }

        // 3. 创建用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); //后面加密

        return userRepository.save(user);
    }

    // 登录
    public LoginResponse login(String username, String password) {

        if (username == null || username.isEmpty() ||
                password == null || password.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户名或密码不能为空");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误"));

        if (!user.getPassword().equals(password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getUsername());

        return new LoginResponse(
                token,
                user.getId(),
                user.getUsername()
        );
    }
}