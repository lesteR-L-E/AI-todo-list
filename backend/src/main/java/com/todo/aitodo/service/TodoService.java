package com.todo.aitodo.service;

import com.todo.aitodo.dto.TodoResponse;
import com.todo.aitodo.model.Todo;
import com.todo.aitodo.model.User;
import com.todo.aitodo.repository.TodoRepository;
import com.todo.aitodo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

//    // 返回当前用户的id
//    private Long getCurrentUserId() {
//        String username = SecurityContextHolder.getContext()
//                .getAuthentication()
//                .getName();
//
//        return userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("用户不存在"))
//                .getId();
//    }
    //返回当前用户
    private User getCurrentUser() {
    var auth = SecurityContextHolder.getContext().getAuthentication();

    // 判断有没有登录
    if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
    }

    // 查用户
    return userRepository.findByUsername(auth.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户不存在"));
}

    // 获取所有
    public List<TodoResponse> getCurrentUserTodos() {
        User user = getCurrentUser();

        return todoRepository.findByUser(user)
                .stream()
                .map(todo -> new TodoResponse(
                        todo.getId(),
                        todo.getTitle(),
                        todo.getCompleted()
                ))
                .toList();
    }

    // 创建
    public Todo createTodo(Todo todo) {
        if (todo.getTitle() == null || todo.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title不能为空");
        }

        todo.setUser(getCurrentUser());

        if (todo.getCompleted() == null) {
            todo.setCompleted(false);
        }

        return todoRepository.save(todo);
    }

    //创建多任务
    @Transactional
    public List<Todo> createTodos(List<Todo> todos) {
        User user = getCurrentUser();
        for(Todo todo : todos) {
            if (todo.getTitle() == null || todo.getTitle().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title不能为空");
            }

            todo.setUser(user);

            if (todo.getCompleted() == null) {
                todo.setCompleted(false);
            }
        }

        return todoRepository.saveAll(todos);
    }

    // 删除
    public void deleteTodo(Long id) {
        System.out.println(">>> 进入 deleteTodo");
        User user = getCurrentUser();

        Todo todo = todoRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        System.out.println(todo);

        todoRepository.delete(todo);
    }

    //清空
    @Transactional
    public int deleteAllTodos() {
        return todoRepository.deleteByUser(getCurrentUser());
    }

    //删除已完成任务
    @Transactional
    public int deleteCompletedTodos() {
        return todoRepository.deleteByUserAndCompletedTrue(getCurrentUser());
    }

    // 更新
    public Todo updateTodo(Long id, Todo todo) {
        User user = getCurrentUser();

        Todo existing = todoRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (todo.getTitle() != null) {
            if (todo.getTitle().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title不能为空");
            }
            existing.setTitle(todo.getTitle());
        }

        if (todo.getCompleted() != null) {
            existing.setCompleted(todo.getCompleted());
        }

        return todoRepository.save(existing);
    }
}