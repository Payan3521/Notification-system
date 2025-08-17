package com.microservicetwo.microservice_notification_dispatcher.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SNSEvent {
    
    @JsonProperty("Type")
    private String type;
    
    @JsonProperty("MessageId")
    private String messageId; 
    
    @JsonProperty("TopicArn")
    private String topicArn;
    
    @JsonProperty("Subject")
    private String subject;
    
    @JsonProperty("Message")
    private String message; // ✅ Este campo contiene la notificación JSON
    
    @JsonProperty("Timestamp")
    private String timestamp;
    
    @JsonProperty("SignatureVersion")
    private String signatureVersion;
    
    @JsonProperty("Signature")
    private String signature;
    
    @JsonProperty("SigningCertURL")
    private String signingCertURL;
    
    @JsonProperty("UnsubscribeURL")
    private String unsubscribeURL;
}