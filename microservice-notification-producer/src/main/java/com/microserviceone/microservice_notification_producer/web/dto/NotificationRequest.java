package com.microserviceone.microservice_notification_producer.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationRequest {
    
    @NotBlank(message = "The info is required")
    @Size(max = 20, message = "La info no puede exceder más de 20 caracteres")
    private String info;

    @NotBlank(message = "subject is required")
    private String subject;

    @NotBlank(message = "body is required")
    private String body;


}