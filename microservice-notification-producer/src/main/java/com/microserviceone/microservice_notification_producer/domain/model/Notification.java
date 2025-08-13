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


    public Notification(String id, String info, String subject, String body, String channel, LocalDateTime createdAt, String status, LocalDateTime sentTime, int retryCount) {
        this.id = id;
        this.info = info;
        this.subject = subject;
        this.body = body;
        this.channel = channel;
        this.createdAt = createdAt;
        this.status = status;
        this.sentTime = sentTime;
        this.retryCount = retryCount;
    }


    public Notification() {
    }


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getChannel() {
        return this.channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSentTime() {
        return this.sentTime;
    }

    public void setSentTime(LocalDateTime sentTime) {
        this.sentTime = sentTime;
    }

    public int getRetryCount() {
        return this.retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
    
}