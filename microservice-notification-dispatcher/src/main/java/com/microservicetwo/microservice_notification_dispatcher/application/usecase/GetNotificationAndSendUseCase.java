package com.microservicetwo.microservice_notification_dispatcher.application.usecase;

import java.time.Duration;
import org.springframework.stereotype.Service;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.Notification;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.in.IGetNotificationAndSend;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.out.INotificationPullerSQS;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.out.ISendNotification;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class GetNotificationAndSendUseCase implements IGetNotificationAndSend{

    private final INotificationPullerSQS notificationPullerSQS;
    private final ISendNotification sendNotification;

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
        return notificationPullerSQS.pullNotifications().doOnNext(notificationSQS -> {
            if(notificationSQS.getChannel().equals("MAIL")) {
                sendNotification.sendEmail(notificationSQS)
                    .doOnNext(enviado -> {
                        if (enviado) {
                            System.out.println("Email enviado: " + notificationSQS);
                        } else {
                            System.out.println("Error al enviar email: " + notificationSQS);
                        }
                    }).subscribe();
            }else if(notificationSQS.getChannel().equals("SMS")) {
                sendNotification.sendSMS(notificationSQS)
                    .doOnNext(enviado -> {
                        if (enviado) {
                            System.out.println("SMS enviado: " + notificationSQS);
                        } else {
                            System.out.println("Error al enviar SMS: " + notificationSQS);
                        }
                    }).subscribe();
            }
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