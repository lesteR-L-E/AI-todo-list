package com.todo.aitodo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.aitodo.dto.AITaskDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.*;

@Service
public class AIService {

    private final ObjectMapper mapper = new ObjectMapper()
            .findAndRegisterModules();

    @Value("${openai.api.key}")
    private String apiKey;

    public List<AITaskDTO> parseTask(String input) {

        LocalDateTime now = LocalDateTime.now();
        String nowStr = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        try {

            RestTemplate restTemplate = new RestTemplate();

            String prompt = """
            You are a strict task parser.
            
            Current time: %s
            
            You must return ONLY valid JSON.
            
            Format:
            [
              {
                "title": string,
                "dueDate": string (ISO 8601 format) OR null
              }
            ]
            
            STRICT RULES:
            
            1. Split input into multiple tasks if needed
            2. Each task must be a REAL actionable task or event
            3. Remove time-related words from title
            
            4. ONLY assign dueDate IF the user EXPLICITLY mentions a date or time
               - If NO time information is present → dueDate MUST be null
               - DO NOT guess
               - DO NOT infer
               - DO NOT create fake deadlines
            
            5. If time is vague (e.g. "以后", "有空") → dueDate = null
            
            6. Use current time ONLY to resolve explicit phrases like:
               - 明天 / tomorrow
               - 后天 / the day after tomorrow
               - 下周一 / next Monday
            
            IMPORTANT FILTER RULES:
            
            - Ignore meta instructions such as:
              "提醒我", "记得", "别忘了", "帮我", "please remind me"
            - Do NOT treat reminders as tasks
            - Only keep actual actions (做家务, 写作业) or events (开会)
            
            EXAMPLES:
            
            Input: 写作业
            Output:
            [
              {
                "title": "写作业",
                "dueDate": null
              }
            ]
            
            Input: 明天写作业
            Output:
            [
              {
                "title": "写作业",
                "dueDate": "2026-03-27T18:00:00"
              }
            ]
            
            Input: 下周一开会
            Output:
            [
              {
                "title": "开会",
                "dueDate": "2026-03-30T09:00:00"
              }
            ]
            
            """.formatted(nowStr);

            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-4o-mini");

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", prompt));
            messages.add(Map.of("role", "user", "content", input));

            body.put("messages", messages);

            // 自动转 JSON
            String requestBody = mapper.writeValueAsString(body);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            String response = restTemplate.postForObject(
                    "https://api.agicto.cn/v1/chat/completions",
                    entity,
                    String.class
            );

            System.out.println("RAW RESPONSE = " + response);

            if (response == null) {
                throw new RuntimeException("AI response is null");
            }

            // 解析 response
            JsonNode root = mapper.readTree(response);

            if (root.has("error")) {
                throw new RuntimeException("OpenAI error: " + root.get("error").toString());
            }

            JsonNode choices = root.get("choices");
            if (choices == null || !choices.isArray() || choices.size() == 0) {
                throw new RuntimeException("Invalid AI response: no choices");
            }

            JsonNode contentNode = choices.get(0).get("message").get("content");
            if (contentNode == null) {
                throw new RuntimeException("Invalid AI response: no content");
            }

            String content = contentNode.asText();
            System.out.println("AI content = " + content);

            return mapper.readValue(
                    content,
                    mapper.getTypeFactory().constructCollectionType(List.class, AITaskDTO.class)
            );

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException("AI 调用失败: " + e.getMessage());
        }
    }
}