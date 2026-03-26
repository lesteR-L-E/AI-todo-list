package com.todo.aitodo.controller;

import com.todo.aitodo.dto.AITaskDTO;
import com.todo.aitodo.service.AIService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/ai")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/parse")
    public List<AITaskDTO> parse(@RequestBody Map<String, String> body) {
        String text = body.get("text");
        return aiService.parseTask(text);
    }
}