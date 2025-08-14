package com.microserviceone.microservice_notification_producer.web.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class NotificationResponse {

    private String id;
    private String info;
    private String subject;
    private String body;
    private String channel;
    private LocalDateTime createdAt;
    private String status;
    private LocalDateTime sentTime;
    private int retryCount;
}