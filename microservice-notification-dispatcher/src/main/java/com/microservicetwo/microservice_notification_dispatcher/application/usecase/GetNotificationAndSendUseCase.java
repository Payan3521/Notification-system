package com.microservicetwo.microservice_notification_dispatcher.application.usecase;

import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.Notification;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.Notification.Status;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.SQSMessage;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.in.IGetNotificationAndSend;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.out.IAdapterNotification;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.out.INotificationPullerSQS;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.out.ISQSMessageDeleter;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.out.ISendNotification;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetNotificationAndSendUseCase implements IGetNotificationAndSend {

    private static final int MAX_RETRY_COUNT = 3;
    
    private final INotificationPullerSQS notificationPullerSQS;
    private final ISendNotification sendNotification;
    private final ISQSMessageDeleter sqsMessageDeleter;
    private final IAdapterNotification notificationAdapter;

    @PostConstruct
    public void startPolling() {
        Flux.interval(Duration.ofSeconds(5)) // cada 5 segundos
            .flatMap(tick -> notificationPullerSQS.pullNotifications())
            .doOnNext(notification -> System.out.println("Notificación recibida: " + notification))
            .subscribe();
    }

        /*@Scheduled(fixedDelay = 5000) // cada 5 segundos
    public void pollNotificationsPeriodically() {
        notificationPullerSQS.pullNotifications()
            .doOnNext(notification -> System.out.println("Notificación recibida: " + notification))
            .subscribe();
    }*/

    @Override
    public Flux<Notification> getNotificationAndSend() {
        return processNotifications();
    }

    private Flux<Notification> processNotifications() {
        return notificationPullerSQS.pullNotifications()
            .flatMap(this::processSingleNotification)
            .onErrorResume(e -> {
                log.error("Error en procesamiento: {}", e.getMessage());
                return Flux.empty();
            });
    }

    private Mono<Notification> processSingleNotification(SQSMessage sqsMessage) {
        return Mono.just(sqsMessage)
            .flatMap(message -> {
                Notification notification = message.getNotification();
                String receiptHandle = message.getReceiptHandle();
                
                return findOrCreateNotification(notification)
                    .flatMap(existing -> handleNotificationProcessing(existing, receiptHandle));
            });
    }

    private Mono<Notification> handleNotificationProcessing(Notification notification, String receiptHandle) {
        // Caso 1: Ya fue enviada exitosamente
        if (notification.getStatus() == Status.SENT) {
            log.info("Notificación {} ya enviada. Eliminando de SQS", notification.getId());
            return sqsMessageDeleter.deleteMessage(receiptHandle)
                .then(Mono.empty());
        }
        
        // Caso 2: Excedió reintentos
        if (notification.getRetryCount() >= MAX_RETRY_COUNT) {
            log.error("Notificación {} excedió reintentos", notification.getId());
            notification.setStatus(Status.FAILED);
            notification.setSentTime(LocalDateTime.now());
            
            return updateAndDelete(notification, receiptHandle)
                .then(Mono.empty());
        }
        
        // Caso 3: Procesar normalmente
        notification.setRetryCount(notification.getRetryCount() + 1);
        log.info("Procesando notificación {} (intento {}/{})", 
            notification.getId(), notification.getRetryCount(), MAX_RETRY_COUNT);
        
        return updateNotification(notification)
            .flatMap(updated -> sendAndHandleResult(updated, receiptHandle));
    }

    private Mono<Notification> findOrCreateNotification(Notification notification) {
        return Mono.fromCallable(() -> notificationAdapter.GetByIdNotification(notification.getId()))
            .flatMap(optional -> optional
                .map(Mono::just)
                .orElseGet(() -> {
                    return Mono.fromCallable(() -> notificationAdapter.saveNotification(notification));
                })
            );
    }

    private Mono<Notification> updateNotification(Notification notification) {
        return Mono.fromCallable(() -> 
            notificationAdapter.updateNotification(notification.getId(), notification))
            .flatMap(optional -> optional.map(Mono::just)
                .orElseGet(() -> Mono.error(new RuntimeException("No se pudo actualizar notificación"))));
    }

    private Mono<Void> updateAndDelete(Notification notification, String receiptHandle) {
        return updateNotification(notification)
            .flatMap(updated -> sqsMessageDeleter.deleteMessage(receiptHandle));
    }

    private Mono<Notification> sendAndHandleResult(Notification notification, String receiptHandle) {
        Mono<Boolean> sendResult = switch (notification.getChannel()) {
            case MAIL -> sendNotification.sendEmail(notification);
            case SMS -> sendNotification.sendSMS(notification);
            case UNKNOWN -> {
                log.error("Canal desconocido para notificación {}", notification.getId());
                yield Mono.just(false);
            }
        };

        return sendResult.flatMap(success -> {
            if (success) {
                notification.setStatus(Status.SENT);
                notification.setSentTime(LocalDateTime.now());
                return updateNotification(notification)
                    .flatMap(updated -> sqsMessageDeleter.deleteMessage(receiptHandle)
                        .thenReturn(updated));
            }
            return Mono.just(notification);
        });
    }
}

/*las maneras que hay de hacerlo reactivo
 * Loop reactivo con Flux.interval (WebFlux)
 * Scheduler con @Scheduled (Spring)
 * Spring Cloud Stream Binder SQS
 * AWS Lambda Trigger
 * Worker dedicado / Thread infinito
 * Frameworks de integración (Apache Camel, Spring Integration, etc.)


 1. las notificaciones no se estan enviando, hice la prueba para enviar una notificacion por email, y no se envio, lo reintentaba y reintentaba y nada... no se envia... 
*/