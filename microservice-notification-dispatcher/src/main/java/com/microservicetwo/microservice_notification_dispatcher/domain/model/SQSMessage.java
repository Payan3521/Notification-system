package com.microservicetwo.microservice_notification_dispatcher.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SQSMessage {
    private Notification notification;
    private String receiptHandle;
}