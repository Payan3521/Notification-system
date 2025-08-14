package com.microserviceone.microservice_notification_producer.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microserviceone.microservice_notification_producer.application.service.NotificationService;
import com.microserviceone.microservice_notification_producer.domain.model.Notification;
import com.microserviceone.microservice_notification_producer.web.dto.ApiResponse;
import com.microserviceone.microservice_notification_producer.web.dto.NotificationRequest;
import com.microserviceone.microservice_notification_producer.web.dto.NotificationResponse;
import com.microserviceone.microservice_notification_producer.web.webMapper.NotificationWebMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    private final NotificationWebMapper notificationWebMapper;


    @PostMapping("/create")
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(
        @Valid @RequestBody NotificationRequest notificationRequest){
            Notification notification = notificationWebMapper.toDomain(notificationRequest);
            Notification response = notificationService.createNotification(notification);
            NotificationResponse notificationResponse = notificationWebMapper.toResponse(response);
            return ResponseEntity.ok(ApiResponse.success("Notificación creada exitosamente!", notificationResponse));
    }
}
