package com.microservicetwo.microservice_notification_dispatcher.application.usecase;

import java.time.Duration;
import org.springframework.stereotype.Service;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.Notification;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.in.IGetNotificationAndSend;
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
public class GetNotificationAndSendUseCase implements IGetNotificationAndSend{

    private final INotificationPullerSQS notificationPullerSQS;
    private final ISendNotification sendNotification;
    private final ISQSMessageDeleter sqsMessageDeleter;

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
            .flatMap(sqsMessage -> {
                Notification notification = sqsMessage.getNotification();
                String receiptHandle = sqsMessage.getReceiptHandle();
                
                log.info("Procesando notificación: {}", notification.getId());
                
                // Determinar el canal y enviar
                Mono<Boolean> sendResult = switch (notification.getChannel()) {
                    case MAIL -> sendNotification.sendEmail(notification);
                    case SMS -> sendNotification.sendSMS(notification);
                    case UNKNOWN -> Mono.just(false);
                };

                // Procesar el resultado del envío
                return sendResult.flatMap(success -> {
                    if (success) {
                        log.info("Notificación {} enviada exitosamente", notification.getId());
                        // ✅ Eliminar mensaje de SQS solo si se envió correctamente
                        return sqsMessageDeleter.deleteMessage(receiptHandle)
                            .thenReturn(notification);
                    } else {
                        log.error("Error al enviar notificación {}", notification.getId());
                        // ✅ NO eliminar el mensaje, se procesará en el siguiente ciclo
                        return Mono.empty();
                    }
                });
            });
    }
    
}

/*las maneras que hay de hacerlo reactivo
 * Loop reactivo con Flux.interval (WebFlux)
 * Scheduler con @Scheduled (Spring)
 * Spring Cloud Stream Binder SQS
 * AWS Lambda Trigger
 * Worker dedicado / Thread infinito
 * Endpoint manual (por ejemplo, GET para probar)
 * Frameworks de integración (Apache Camel, Spring Integration, etc.)
 */

 1. las notificaciones no se estan enviando, hice la prueba para enviar una notificacion por email, y no se envio, lo reintentaba y reintentaba y nada... no se envia... 

2. quiero que solo reintente 3 veces, despues de esos 3 reintentos, necesito enviarlo a una cola de fallidos, para despues pasar a valorar a ver porq fue que no se envio, entonces necesito implementar eso, ese reintento.