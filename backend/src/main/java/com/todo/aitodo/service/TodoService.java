package com.todo.aitodo.service;

import com.todo.aitodo.model.Todo;
import com.todo.aitodo.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    // 👉 统一管理当前用户（以后这里接登录系统）
    private Long getCurrentUserId() {
        return 1L;
    }

    // 获取所有
    public List<Todo> getAllTodos() {
        return todoRepository.findByUserId(getCurrentUserId());
    }

    // 创建
    public Todo createTodo(Todo todo) {
        if (todo.getTitle() == null || todo.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title不能为空");
        }

        todo.setUserId(getCurrentUserId());

        if (todo.getCompleted() == null) {
            todo.setCompleted(false);
        }

        return todoRepository.save(todo);
    }

    //创建多任务
    @Transactional
    public List<Todo> createTodos(List<Todo> todos) {
        for(Todo todo : todos) {
            if (todo.getTitle() == null || todo.getTitle().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title不能为空");
            }

            todo.setUserId(getCurrentUserId());

            if (todo.getCompleted() == null) {
                todo.setCompleted(false);
            }
        }

        return todoRepository.saveAll(todos);
    }

    // 删除
    public void deleteTodo(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!todo.getUserId().equals(getCurrentUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        todoRepository.delete(todo);
    }

    //清空
    @Transactional
    public int deleteAllTodos() {
        return todoRepository.deleteByUserId(getCurrentUserId());
    }

    //删除已完成任务
    @Transactional
    public int deleteCompletedTodos() {
        return todoRepository.deleteByUserIdAndCompletedTrue(getCurrentUserId());
    }

    // 更新
    public Todo updateTodo(Long id, Todo todo) {
        Todo existing = todoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!existing.getUserId().equals(getCurrentUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

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