package com.microserviceone.microservice_notification_producer.infraestructure.persistance.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationEntity {

    private String id;
    private String info;
    private String subject;
    private String body;
    private String channel;
    private LocalDateTime createdAt;
    private String status;
    private LocalDateTime sentTime;
    private int retryCount;

    public NotificationEntity(String info, String subject, String body, String channel) {
        this.id= UUID.randomUUID().toString();
        this.info = info;
        this.subject = subject;
        this.body = body;
        this.channel = channel;
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
        this.sentTime = null;
        this.retryCount = 0;
    }


}