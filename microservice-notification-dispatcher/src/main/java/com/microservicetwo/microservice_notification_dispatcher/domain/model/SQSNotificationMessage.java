package com.microservicetwo.microservice_notification_dispatcher.domain.model;

public class SQSNotificationMessage {
    private String receiptHandle;
    private Notification notification;

    public SQSNotificationMessage(String receiptHandle, Notification notification) {
        this.receiptHandle = receiptHandle;
        this.notification = notification;
    }

    public SQSNotificationMessage() {}

    public String getReceiptHandle() {
        return receiptHandle;
    }

    public void setReceiptHandle(String receiptHandle) {
        this.receiptHandle = receiptHandle;
    }

    public Notification getNotification() { 
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
