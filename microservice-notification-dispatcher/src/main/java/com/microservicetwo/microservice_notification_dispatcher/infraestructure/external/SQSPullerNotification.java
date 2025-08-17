package com.microservicetwo.microservice_notification_dispatcher.infraestructure.external;

import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.Notification;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.SQSNotificationMessage;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.out.ISQSPullerNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class SQSPullerNotification implements ISQSPullerNotification {

    private final SqsAsyncClient sqsAsyncClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${aws.sqs.notification-queue}")
    private String queueUrl;

    @Override
    public Flux<SQSNotificationMessage> pullNotifications() {
        return Flux.interval(Duration.ofSeconds(2)) // Polling cada 2 segundos
            .flatMap(tick -> pullMessagesFromSQS())
            .onErrorResume(error -> {
                log.error("❌ Error en el polling de SQS: {}", error.getMessage());
                return Mono.delay(Duration.ofSeconds(5))
                    .then(Mono.empty()); // Continuar después del error
            })
            .repeat(); // Repetir indefinidamente
    }

    private Flux<SQSNotificationMessage> pullMessagesFromSQS() {
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            .maxNumberOfMessages(10) // Máximo 10 mensajes por request
            .waitTimeSeconds(20) // Long polling de 20 segundos
            .build();

        return Mono.fromFuture(sqsAsyncClient.receiveMessage(receiveRequest))
            .subscribeOn(Schedulers.boundedElastic())
            .flatMapMany(this::processMessages)
            .doOnNext(sqsMessage -> log.debug("📥 Mensaje recibido de SQS: {}", 
                sqsMessage.getNotification().getId()));
    }

    private Flux<SQSNotificationMessage> processMessages(ReceiveMessageResponse response) {
        List<Message> messages = response.messages();
        
        if (messages.isEmpty()) {
            log.debug("📭 No hay mensajes en SQS");
            return Flux.empty();
        }

        log.info("📬 Recibidos {} mensajes de SQS", messages.size());
        
        return Flux.fromIterable(messages)
            .flatMap(this::parseMessage)
            .onErrorContinue((error, item) -> {
                log.error("❌ Error procesando mensaje SQS: {}", error.getMessage());
            });
    }

    private Mono<SQSNotificationMessage> parseMessage(Message sqsMessage) {
        return Mono.fromCallable(() -> {
            try {
                String messageBody = sqsMessage.body();
                log.debug("📄 Parseando mensaje: {}", messageBody);
                
                // Parsear el JSON del mensaje a objeto Notification
                Notification notification = objectMapper.readValue(messageBody, Notification.class);
                
                // Crear el wrapper con el receiptHandle para poder eliminar el mensaje después
                SQSNotificationMessage sqsNotificationMessage = new SQSNotificationMessage(
                    sqsMessage.receiptHandle(), 
                    notification
                );
                
                log.debug("✅ Mensaje parseado exitosamente: ID={}", notification.getId());
                return sqsNotificationMessage;
                
            } catch (Exception e) {
                log.error("❌ Error parseando mensaje SQS: {}", e.getMessage());
                throw new RuntimeException("Error parsing SQS message", e);
            }
        })
        .subscribeOn(Schedulers.boundedElastic());
    }
}