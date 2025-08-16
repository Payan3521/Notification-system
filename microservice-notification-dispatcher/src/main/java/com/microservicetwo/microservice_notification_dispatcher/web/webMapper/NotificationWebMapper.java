package com.microservicetwo.microservice_notification_dispatcher.web.webMapper;

import com.microservicetwo.microservice_notification_dispatcher.domain.model.Notification;
import com.microservicetwo.microservice_notification_dispatcher.web.dto.NotificationResponse;

public class NotificationWebMapper {
    
    public NotificationResponse toResponse(Notification notification){

        NotificationResponse notificationResponse = NotificationResponse.builder()
            .id(notification.getId())
            .info(notification.getInfo())
            .subject(notification.getSubject())
            .body(notification.getBody())
            .channel(notification.getChannel().name())
            .createdAt(notification.getCreatedAt())
            .status(notification.getStatus().name())
            .sentTime(notification.getSentTime())
            .retryCount(notification.getRetryCount())
            .build();

        return notificationResponse;
    }
}