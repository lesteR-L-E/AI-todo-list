package com.todo.aitodo.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理业务异常
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handle(ResponseStatusException ex) {

        String message = ex.getReason();
        if (message == null) {
            message = ex.getStatusCode().toString(); // 或你自定义默认文案
        }

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of(
                        "code", ex.getStatusCode().value(),
                        "message", message
                ));
    }

    // 兜底异常（必须有）
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(Exception ex) {
        return ResponseEntity
                .status(500)
                .body(Map.of(
                        "code", 500,
                        "message", "服务器内部错误"
                ));
    }
}