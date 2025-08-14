package com.microserviceone.microservice_notification_producer.web.webMapper;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.microserviceone.microservice_notification_producer.domain.model.Notification;
import com.microserviceone.microservice_notification_producer.domain.model.Notification.Status;
import com.microserviceone.microservice_notification_producer.web.dto.NotificationRequest;
import com.microserviceone.microservice_notification_producer.web.dto.NotificationResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationWebMapper {
    
    public Notification toDomain(NotificationRequest request){

        Notification notification = new Notification(
            UUID.randomUUID().toString(),
            request.getInfo(), 
            request.getSubject(), 
            request.getBody(), 
            null, 
            LocalDateTime.now(), 
            Status.PENDING, 
            null, 
            0);

        return notification;
    }

    public NotificationResponse toResponse(Notification notification){

        NotificationResponse notificationResponse = NotificationResponse.builder()
            .id(notification.getId())
            .info(notification.getInfo())
            .subject(notification.getSubject())
            .body(notification.getBody())
            .channel(notification.getChannel())
            .createdAt(notification.getCreatedAt())
            .status(notification.getStatus().name())
            .sentTime(notification.getSentTime())
            .retryCount(notification.getRetryCount())
            .build();

        return notificationResponse;
    }
}