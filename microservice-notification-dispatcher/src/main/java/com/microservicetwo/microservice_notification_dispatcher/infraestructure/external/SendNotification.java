package com.microservicetwo.microservice_notification_dispatcher.infraestructure.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.microservicetwo.microservice_notification_dispatcher.domain.model.Notification;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.out.ISendNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.ses.SesAsyncClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendNotification implements ISendNotification {

    private final SesAsyncClient sesAsyncClient;
    private final SnsAsyncClient snsAsyncClient; // ✅ Agregar cliente SNS
    
    @Value("${aws.ses.from-email}")
    private String fromEmail;
    
    @Value("${aws.ses.reply-to-email}")
    private String replyToEmail;

    @Override
    public Mono<Boolean> sendEmail(Notification notification) {
        log.info("�� Iniciando envío de email a: {}", notification.getInfo());
        
        try {
            // ✅ Validar que la dirección de email sea válida
            if (!isValidEmail(notification.getInfo())) {
                log.error("❌ Dirección de email inválida: {}", notification.getInfo());
                return Mono.just(false);
            }

            // ✅ Crear el contenido del email
            Content subject = Content.builder()
                .data(notification.getSubject())
                .charset("UTF-8")
                .build();

            Content textBody = Content.builder()
                .data(notification.getBody()) 
                .charset("UTF-8")
                .build();

            // ✅ Crear el body del email (texto plano)
            Body body = Body.builder()
                .text(textBody)
                .build();

            Message message = Message.builder()
                .subject(subject)
                .body(body)
                .build();

            // ✅ Crear la solicitud de envío
            SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                .source(fromEmail)
                .replyToAddresses(replyToEmail)
                .destination(Destination.builder()
                    .toAddresses(notification.getInfo())
                    .build())
                .message(message)
                .build();

            // ✅ Enviar el email usando WebFlux
            return Mono.fromFuture(sesAsyncClient.sendEmail(sendEmailRequest))
                .map(response -> {
                    log.info("✅ Email enviado exitosamente a {} con ID: {}", 
                        notification.getInfo(), response.messageId());
                    return true;
                })
                .onErrorResume(error -> {
                    log.error("❌ Error al enviar email a {}: {}", 
                        notification.getInfo(), error.getMessage());
                    return Mono.just(false);
                });

        } catch (Exception e) {
            log.error("❌ Error inesperado al enviar email: {}", e.getMessage(), e);
            return Mono.just(false);
        }
    }

    @Override
    public Mono<Boolean> sendSMS(Notification notification) {
        log.info("�� Iniciando envío de SMS a: {}", notification.getInfo());
        
        try {
            // ✅ Validar que el número de teléfono sea válido
            if (!isValidPhoneNumber(notification.getInfo())) {
                log.error("❌ Número de teléfono inválido: {}", notification.getInfo());
                return Mono.just(false);
            }
    
            // ✅ Truncar mensaje si es muy largo (SNS límite: 160 caracteres)
            String messageText = notification.getBody();
            if (messageText.length() > 160) {
                log.warn("⚠️ Mensaje truncado de {} a 160 caracteres", messageText.length());
                messageText = messageText.substring(0, 157) + "...";
            }
    
            // ✅ Crear la solicitud de envío de SMS usando SNS
            PublishRequest publishRequest = PublishRequest.builder()
                .message(messageText)
                .phoneNumber(notification.getInfo())
                .build();
    
            // ✅ Enviar el SMS usando WebFlux
            return Mono.fromFuture(snsAsyncClient.publish(publishRequest))
                .map(response -> {
                    log.info("✅ SMS enviado exitosamente a {} con ID: {}", 
                        notification.getInfo(), response.messageId());
                    return true;
                })
                .onErrorResume(error -> {
                    log.error("❌ Error al enviar SMS a {}: {}", 
                        notification.getInfo(), error.getMessage());
                    return Mono.just(false);
                });
    
        } catch (Exception e) {
            log.error("❌ Error inesperado al enviar SMS: {}", e.getMessage(), e);
            return Mono.just(false);
        }
    }

    // ✅ Método auxiliar para validar emails
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Validación básica de formato de email
        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailRegex);
    }

    // ✅ Método auxiliar para validar números de teléfono
    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        
        // ✅ Validación para números internacionales (formato E.164)
        // Ejemplos válidos: +1234567890, +34612345678, +447911123456
        String phoneRegex = "^\\+[1-9]\\d{1,14}$";
        return phoneNumber.matches(phoneRegex);
    }
}

//toca configurarlo en aws console
/*# 1. Ir a SNS → Text messaging (SMS)
# 2. Configurar tipo de SMS (Transactional o Promotional)
# 3. Establecer límite de gasto mensual
# 4. Verificar números de teléfono (opcional)
tambien hay que configurar dominio en aws console en ses para verificar el codigo y demas
agregar AmazonSESFullAccess
*/