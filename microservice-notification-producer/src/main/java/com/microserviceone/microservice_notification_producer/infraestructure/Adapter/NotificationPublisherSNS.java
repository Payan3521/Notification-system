package com.microserviceone.microservice_notification_producer.infraestructure.Adapter;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microserviceone.microservice_notification_producer.domain.model.Notification;
import com.microserviceone.microservice_notification_producer.domain.port.out.INotificationPublisherSNS;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Component
public class NotificationPublisherSNS implements INotificationPublisherSNS {

    private final SnsAsyncClient snsAsyncClient;
    private final ObjectMapper objectMapper;
    private final String topicArn;

    public NotificationPublisherSNS(SnsAsyncClient snsAsyncClient, ObjectMapper objectMapper,
                                    @Value("${aws.sns.notification-topic-arn}") String topicArn) {
        this.snsAsyncClient = snsAsyncClient;
        this.objectMapper = objectMapper;
        this.topicArn = topicArn;
    }

    @Override
    public Mono<Notification> publishNotification(Notification notification) {
        try {
            Map<String, Object> eventData = createEventData(notification);
            String message = objectMapper.writeValueAsString(eventData);

            PublishRequest publishRequest = PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(message)
                    .subject("Notification Event")
                    .build();

            // Usar Mono.fromFuture con el método que retorna CompletableFuture
            return Mono.fromFuture(snsAsyncClient.publish(publishRequest))
                .map(PublishResponse::messageId)
                .thenReturn(notification);

        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to publish notification", e));
        }
    }

    private Map<String, Object> createEventData(Notification notification) {
        Map<String, Object> eventData = new HashMap<>();

        eventData.put("id", notification.getId());
        eventData.put("info", notification.getInfo());
        eventData.put("subject", notification.getSubject());
        eventData.put("body", notification.getBody());
        eventData.put("channel", notification.getChannel().name());
        eventData.put("createdAt", notification.getCreatedAt());
        eventData.put("status", notification.getStatus().name());
        eventData.put("sentTime", notification.getSentTime());
        eventData.put("retryCount", notification.getRetryCount());
        
        return eventData;
    }
    
}