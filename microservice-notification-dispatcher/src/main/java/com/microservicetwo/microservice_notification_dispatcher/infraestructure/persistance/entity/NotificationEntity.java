package com.microservicetwo.microservice_notification_dispatcher.infraestructure.persistance.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "notifications")
@Data
@Builder
public class NotificationEntity {

    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "info", nullable = false)
    private String info;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "channel", nullable = false)
    private String channel;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "sent_time")
    private LocalDateTime sentTime;
    
    @Column(name = "retry_count", nullable = false)
    private int retryCount;

}