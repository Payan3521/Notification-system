package com.microservicetwo.microservice_notification_dispatcher.infraestructure.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.Notification;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.out.INotificationPullerSQS;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@Component
public class NotificationPullerSQS implements INotificationPullerSQS {

    private final SqsAsyncClient sqsAsyncClient;
    private final ObjectMapper objectMapper;
    private final String queueUrl;

    public NotificationPullerSQS(SqsAsyncClient sqsAsyncClient, ObjectMapper objectMapper,
                                @Value("${aws.sqs.notification-queue}") String queueUrl) {
        this.sqsAsyncClient = sqsAsyncClient;
        this.objectMapper = objectMapper;
        this.queueUrl = queueUrl;
    }

    @Override
    public Flux<Notification> pullNotifications() {
            ReceiveMessageRequest request = ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            .maxNumberOfMessages(10)
            .waitTimeSeconds(20)
            .build();

        return Flux.defer(() ->
            Flux.fromIterable(
                sqsAsyncClient.receiveMessage(request)
                    .thenApply(response -> response.messages())
                    .join()
            )
        ).flatMap(message -> {
            try {
                Notification notification = objectMapper.readValue(message.body(), Notification.class);
                // Aquí procesas la notificación (ejemplo: enviar mail/SMS)
                boolean enviado = enviarNotificacion(notification); // tu lógica
                if (enviado) {
                    // Solo eliminas si el envío fue exitoso
                    sqsAsyncClient.deleteMessage(DeleteMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .receiptHandle(message.receiptHandle())
                            .build());
                }
                return Flux.just(notification);
            } catch (Exception e) {
                // Si hay error, NO eliminas el mensaje
                return Flux.empty();
            }
        });
    }
    
}