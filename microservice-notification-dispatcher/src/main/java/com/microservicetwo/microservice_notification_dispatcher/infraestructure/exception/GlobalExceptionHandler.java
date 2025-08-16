package com.microservicetwo.microservice_notification_dispatcher.infraestructure.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.microservicetwo.microservice_notification_dispatcher.domain.exception.NotificationAlreadySentException;
import com.microservicetwo.microservice_notification_dispatcher.domain.exception.NotificationNotFoundException;
import com.microservicetwo.microservice_notification_dispatcher.domain.exception.UserNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(NotificationAlreadySentException.class)
    public ResponseEntity<Map<String, Object>> handleNotificationAlreadySentException(NotificationAlreadySentException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Notification Already Sent");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotificationNotFoundException(NotificationNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Notification Not Found");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "User Not Found");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}