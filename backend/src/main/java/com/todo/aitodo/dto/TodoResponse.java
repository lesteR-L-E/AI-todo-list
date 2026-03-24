package com.todo.aitodo.dto;

public class TodoResponse {

    private Long id;
    private String title;
    private Boolean completed;

    public TodoResponse(Long id, String title, Boolean completed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Boolean getCompleted() { return completed; }
}
