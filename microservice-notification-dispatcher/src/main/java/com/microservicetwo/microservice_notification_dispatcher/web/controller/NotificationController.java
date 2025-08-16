package com.microservicetwo.microservice_notification_dispatcher.web.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservicetwo.microservice_notification_dispatcher.application.service.NotificationService;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.Notification;
import com.microservicetwo.microservice_notification_dispatcher.web.dto.ApiResponse;
import com.microservicetwo.microservice_notification_dispatcher.web.dto.NotificationResponse;
import com.microservicetwo.microservice_notification_dispatcher.web.webMapper.NotificationWebMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationWebMapper notificationWebMapper;
    
    @GetMapping("/id/{id}")
    public ResponseEntity<ApiResponse<NotificationResponse>> getByUserIdNotification(
        @PathVariable String id){

            Optional<Notification> notificationOptional = notificationService.getByIdNotification(id);

            Notification notification = notificationOptional.get();

            NotificationResponse notificationResponse = notificationWebMapper.toResponse(notification);

            return ResponseEntity.ok(ApiResponse.success("Notification found successfully", notificationResponse));
        }
}