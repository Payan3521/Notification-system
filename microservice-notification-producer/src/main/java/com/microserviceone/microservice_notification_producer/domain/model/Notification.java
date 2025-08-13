package com.microserviceone.microservice_notification_producer.domain.model;

import java.time.LocalDateTime;

public class Notification {
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