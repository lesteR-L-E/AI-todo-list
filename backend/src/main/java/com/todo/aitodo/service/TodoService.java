package com.todo.aitodo.service;

import com.todo.aitodo.dto.TodoRequest;
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
                .map(this::toResponse)
                .toList();
    }

    // 创建
    public TodoResponse createTodo(TodoRequest request) {

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title不能为空");
        }

        User user = getCurrentUser();

        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setUser(user);
        todo.setCompleted(
                request.getCompleted() != null ? request.getCompleted() : false
        );
        todo.setDueDate(request.getDueDate());

        Todo saved = todoRepository.save(todo);

        return toResponse(saved);
    }

    private TodoResponse toResponse(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getCompleted(),
                todo.getDueDate(),
                todo.getCreatedAt(),
                todo.getUpdatedAt()
        );
    }

    //创建多任务
    @Transactional
    public List<TodoResponse> createTodos(List<TodoRequest> requests) {

        User user = getCurrentUser();

        List<Todo> todos = requests.stream().map(req -> {
            if (req.getTitle() == null || req.getTitle().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title不能为空");
            }

            Todo todo = new Todo();
            todo.setTitle(req.getTitle());
            todo.setUser(user);
            todo.setCompleted(req.getCompleted() != null ? req.getCompleted() : false);
            todo.setDueDate(req.getDueDate());

            return todo;
        }).toList();

        return todoRepository.saveAll(todos)
                .stream()
                .map(this::toResponse)
                .toList();
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
    public TodoResponse updateTodo(Long id, TodoRequest request) {
        User user = getCurrentUser();

        Todo existing = todoRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (request.getTitle() != null) {
            if (request.getTitle().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title不能为空");
            }
            existing.setTitle(request.getTitle());
        }

        if (request.getCompleted() != null) {
            existing.setCompleted(request.getCompleted());
        }

        if (request.getDueDate() != null) {
            existing.setDueDate(request.getDueDate());
        }

        Todo saved = todoRepository.save(existing);

        return toResponse(saved);
    }
}