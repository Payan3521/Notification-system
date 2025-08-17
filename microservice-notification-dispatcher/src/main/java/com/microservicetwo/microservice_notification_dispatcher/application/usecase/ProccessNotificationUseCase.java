package com.microservicetwo.microservice_notification_dispatcher.application.usecase;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.Notification;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.SQSNotificationMessage;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.User;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.out.IPortNotification;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.out.IPortUser;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.out.ISQSMessageDeleter;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.out.ISQSPullerNotification;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.out.ISendNotification;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProccessNotificationUseCase {
    private final ISQSPullerNotification sqsPullerNotification;
    private final IPortNotification portNotification;
    private final IPortUser portUser;
    private final ISQSMessageDeleter sqsMessageDeleter;
    //private final ISendNotification sendNotification;
   
    @PostConstruct
    public void startProcessing() {
        log.info("Iniciando procesamiento de notificaciones desde SQS");
        processNotifications()
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe();
    }


    public Mono<Void> processNotifications() {
        return sqsPullerNotification.pullNotifications()
            .delayElements(Duration.ofSeconds(1)) // Para evitar saturar el sistema
            .flatMap(this::processNotificationMessage)
            .doOnError(error -> log.error("❌ Error en el procesamiento general: {}", error.getMessage()))
            .onErrorResume(error -> {
                log.error("🔄 Reiniciando procesamiento después de error", error);
                return Mono.delay(Duration.ofSeconds(5))
                    .then(processNotifications());
            })
            .then();
    }

    private Mono<Void> processNotificationMessage(SQSNotificationMessage sqsMessage) {
        log.info("📨 Procesando notificación ID: {}", sqsMessage.getNotification().getId());
        
        return Mono.fromCallable(() -> sqsMessage)
            .flatMap(this::validateAndProcessNotification)
            .doOnSuccess(success -> {
                if (success) {
                    log.info("✅ Notificación {} procesada exitosamente", 
                        sqsMessage.getNotification().getId());
                } else {
                    log.warn("⚠️ Notificación {} no se pudo procesar completamente", 
                        sqsMessage.getNotification().getId());
                }
            })
            .doOnError(error -> log.error("❌ Error procesando notificación {}: {}", 
                sqsMessage.getNotification().getId(), error.getMessage()))
            .onErrorReturn(false)
            .then();
    }

    private Mono<Boolean> validateAndProcessNotification(SQSNotificationMessage sqsMessage) {
        Notification incomingNotification = sqsMessage.getNotification();
        String notificationId = incomingNotification.getId();

        return checkExistingNotification(notificationId)
            .flatMap(existingNotification -> {
                if (existingNotification.isPresent()) {
                    return handleExistingNotification(existingNotification.get(), sqsMessage);
                } else {
                    return handleNewNotification(incomingNotification, sqsMessage);
                }
            });
    }

    private Mono<Optional<Notification>> checkExistingNotification(String notificationId) {
        return Mono.fromCallable(() -> portNotification.GetByIdNotification(notificationId))
            .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<Boolean> handleExistingNotification(Notification existingNotification, SQSNotificationMessage sqsMessage) {
        log.info("🔍 Notificación {} ya existe en DB con estado: {}", 
            existingNotification.getId(), existingNotification.getStatus());

        // Si ya fue enviada exitosamente, eliminar mensaje de SQS
        if (existingNotification.isSuccessful()) {
            log.info("✅ Notificación {} ya fue enviada, eliminando de SQS", existingNotification.getId());
            return deleteMessageFromSQS(sqsMessage.getReceiptHandle()).thenReturn(true);
        }

        // Si no puede reintentarse más, eliminar mensaje de SQS
        if (!existingNotification.canRetry()) {
            log.warn("🚫 Notificación {} alcanzó el límite de reintentos, eliminando de SQS", 
                existingNotification.getId());
            existingNotification.markAsFailed("Límite de reintentos alcanzado");
            return updateNotificationInDB(existingNotification)
                .then(deleteMessageFromSQS(sqsMessage.getReceiptHandle()))
                .thenReturn(false);
        }

        // Intentar reenvío
        return attemptRetry(existingNotification, sqsMessage);
    }

    private Mono<Boolean> handleNewNotification(Notification incomingNotification, SQSNotificationMessage sqsMessage) {
        log.info("🆕 Nueva notificación {}, validando usuario", incomingNotification.getId());

        return validateUser(incomingNotification)
            .flatMap(userValid -> {
                if (!userValid) {
                    log.warn("👤 Usuario inválido para notificación {}, eliminando de SQS", 
                        incomingNotification.getId());
                    incomingNotification.markAsFailed("Usuario inválido o inactivo");
                    return saveNotificationToDB(incomingNotification)
                        .then(deleteMessageFromSQS(sqsMessage.getReceiptHandle()))
                        .thenReturn(false);
                } 

                // Usuario válido, procesar notificación
                incomingNotification.setCreatedAt(LocalDateTime.now());
                incomingNotification.setStatus(Notification.Status.PENDING);
                
                return saveNotificationToDB(incomingNotification)
                    .then(attemptSending(incomingNotification, sqsMessage));
            });
    }

    private Mono<Boolean> validateUser(Notification notification) {
        return Mono.fromCallable(() -> {
            String contactInfo = notification.getInfo();
            Notification.Channel channel = notification.getChannel();

            Optional<User> userOpt = Optional.empty();

            // Buscar usuario según el canal
            if (channel == Notification.Channel.MAIL) {
                userOpt = portUser.findByEmail(contactInfo);
            } else if (channel == Notification.Channel.SMS) {
                userOpt = portUser.findByPhoneNumber(contactInfo);
            }

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (user.isActive()) {
                    log.info("👤 Usuario encontrado y activo: {}", user.getId());
                    return true;
                } else {
                    log.warn("👤 Usuario encontrado pero inactivo: {}", user.getId());
                    return false;
                }
            } else {
                // Usuario no existe, crear uno nuevo
                return createNewUser(contactInfo, channel);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

   private boolean createNewUser(String contactInfo, Notification.Channel channel) {
    try {
        User newUser = new User();
        // ✅ NO asignar ID manualmente - JPA lo generará automáticamente
        newUser.setName("Usuario Automático");
        newUser.setStatus(User.UserStatus.ACTIVE);

        if (channel == Notification.Channel.MAIL) {
            newUser.setEmail(contactInfo);
            newUser.setPhoneNumber(""); // Vacío por ahora
        } else if (channel == Notification.Channel.SMS) {
            newUser.setPhoneNumber(contactInfo);
            newUser.setEmail(""); // Vacío por ahora
        }

        User savedUser = portUser.saveUser(newUser);
        log.info("👤 Usuario creado automáticamente: {} con contacto: {}", 
            savedUser.getId(), contactInfo);
        return true;

    } catch (Exception e) {
        log.error("❌ Error creando usuario automático: {}", e.getMessage(), e);
        return false;
    }
}

    private Mono<Boolean> attemptRetry(Notification notification, SQSNotificationMessage sqsMessage) {
        log.info("🔄 Intentando reenvío #{} para notificación {}", 
            notification.getRetryCount() + 1, notification.getId());
        
        notification.incrementRetry();
        
        return updateNotificationInDB(notification)
            .then(attemptSending(notification, sqsMessage));
    }

    private Mono<Boolean> attemptSending(Notification notification, SQSNotificationMessage sqsMessage) {
        return sendNotificationByChannel(notification)
            .flatMap(success -> {
                if (success) {
                    notification.markAsSent();
                    log.info("✅ Notificación {} enviada exitosamente", notification.getId());
                    
                    return updateNotificationInDB(notification)
                        .then(deleteMessageFromSQS(sqsMessage.getReceiptHandle()))
                        .thenReturn(true);
                } else {
                    notification.markAsFailed("Error en el envío");
                    log.error("❌ Fallo en el envío de notificación {}", notification.getId());
                    
                    return updateNotificationInDB(notification)
                        .thenReturn(false); // No eliminar de SQS para reintentar
                }
            });
    }

    private Mono<Boolean> sendNotificationByChannel(Notification notification) {
        return Mono.just(true);
        /*return switch (notification.getChannel()) {
            case MAIL -> sendNotification.sendEmail(notification);
            case SMS -> sendNotification.sendSMS(notification);
            default -> {
                log.error("❌ Canal desconocido: {}", notification.getChannel());
                yield Mono.just(false);
            }
        };*/
    }

    private Mono<Notification> saveNotificationToDB(Notification notification) {
        return Mono.fromCallable(() -> portNotification.saveNotification(notification))
            .subscribeOn(Schedulers.boundedElastic())
            .doOnSuccess(saved -> log.debug("💾 Notificación guardada: {}", saved.getId()))
            .doOnError(error -> log.error("❌ Error guardando notificación: {}", error.getMessage()));
    }

    private Mono<Optional<Notification>> updateNotificationInDB(Notification notification) {
        return Mono.fromCallable(() -> portNotification.updateNotification(notification.getId(), notification))
            .subscribeOn(Schedulers.boundedElastic())
            .doOnSuccess(updated -> log.debug("🔄 Notificación actualizada: {}", notification.getId()))
            .doOnError(error -> log.error("❌ Error actualizando notificación: {}", error.getMessage()));
    }

    private Mono<Void> deleteMessageFromSQS(String receiptHandle) {
        return sqsMessageDeleter.deleteMessage(receiptHandle)
            .doOnSuccess(v -> log.debug("🗑️ Mensaje eliminado de SQS"))
            .doOnError(error -> log.error("❌ Error eliminando mensaje de SQS: {}", error.getMessage()))
            .onErrorResume(error -> Mono.empty()); // Continue even if deletion fails
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