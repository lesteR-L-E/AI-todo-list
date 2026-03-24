package com.todo.aitodo.model;

import jakarta.persistence.*;

@Entity
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Boolean completed = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Todo() {}

    public Todo(Long id, String title, Boolean completed, User user) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.user = user;
    }

    public Long getId() { return id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public Boolean getCompleted() { return completed; }

    public void setCompleted(Boolean completed) { this.completed = completed; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }
}