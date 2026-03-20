package com.todo.aitodo.controller;

import com.todo.aitodo.model.Todo;
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
    public List<Todo> getAll() {
        return todoService.getAllTodos();
    }

    // 新建
    @PostMapping
    public Todo create(@RequestBody Todo todo) {
        return todoService.createTodo(todo);
    }

    // 删除
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        todoService.deleteTodo(id);
    }

    //清空所有
    @DeleteMapping
    public Map<String, Integer> deleteAll() {
        int count = todoService.deleteAllTodos();
        return Map.of("deleted", count);
    }

    //删除已完成任务
    @DeleteMapping("/completed")
    public Map<String, Integer> deleteCompleted() {
        int count = todoService.deleteCompletedTodos();
        return Map.of("deleted", count);
    }

    // 更新
    @PutMapping("/{id}")
    public Todo update(@PathVariable Long id,
                       @RequestBody Todo todo) {
        return todoService.updateTodo(id, todo);
    }
}