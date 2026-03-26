package com.todo.aitodo.controller;

import com.todo.aitodo.dto.TodoRequest;
import com.todo.aitodo.dto.TodoResponse;
import com.todo.aitodo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/todos")
public class TodoController {

    @Autowired
    private TodoService todoService;

    // 获取全部
    @GetMapping
    public List<TodoResponse> getTodos() {
        return todoService.getCurrentUserTodos();
    }

    // 新建
    @PostMapping
    public TodoResponse create(@RequestBody TodoRequest request) {
        return todoService.createTodo(request);
    }

    // 批量创建
    @PostMapping("/batch")
    public List<TodoResponse> createTodos(@RequestBody List<TodoRequest> requests) {
        return todoService.createTodos(requests);
    }

    // 删除
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        todoService.deleteTodo(id);
    }

    // 清空所有
    @DeleteMapping
    public Map<String, Integer> deleteAll() {
        int count = todoService.deleteAllTodos();
        return Map.of("deleted", count);
    }

    // 删除已完成任务
    @DeleteMapping("/completed")
    public Map<String, Integer> deleteCompleted() {
        int count = todoService.deleteCompletedTodos();
        return Map.of("deleted", count);
    }

    // ✅ 更新
    @PutMapping("/{id}")
    public TodoResponse update(@PathVariable Long id,
                               @RequestBody TodoRequest request) {
        return todoService.updateTodo(id, request);
    }
}