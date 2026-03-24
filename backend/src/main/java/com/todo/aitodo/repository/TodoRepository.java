package com.todo.aitodo.repository;

import com.todo.aitodo.model.Todo;
import com.todo.aitodo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    //查找所有当前id的任务
    List<Todo> findByUser(User user);

    //清空所有任务
    int deleteByUser(User user);

    //删除已完成任务
    int deleteByUserAndCompletedTrue(User user);

    //安全查找用户的任务
    Optional<Todo> findByIdAndUser(Long id, User user);
}