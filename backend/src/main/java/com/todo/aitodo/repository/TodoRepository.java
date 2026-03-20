package com.todo.aitodo.repository;

import com.todo.aitodo.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    //查找所有当前id的任务
    List<Todo> findByUserId(Long userId);

    //清空所有任务
    int deleteByUserId(Long userId);

    //删除已完成任务
    int deleteByUserIdAndCompletedTrue(Long userId);
}