package com.microservicetwo.microservice_notification_dispatcher.infraestructure.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.microservicetwo.microservice_notification_dispatcher.domain.port.out.ISQSMessageDeleter;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;

@Component
public class SQSMessageDeleter implements ISQSMessageDeleter{

    private final SqsAsyncClient sqsAsyncClient;
    private final String queueUrl;

    public SQSMessageDeleter(SqsAsyncClient sqsAsyncClient,
                            @Value("${aws.sqs.notification-queue}") String queueUrl) {
        this.sqsAsyncClient = sqsAsyncClient;
        this.queueUrl = queueUrl;
    }

    @Override
    public Mono<Void> deleteMessage(String receiptHandle) {
        DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
            .queueUrl(queueUrl)
            .receiptHandle(receiptHandle)
            .build();

        return Mono.fromFuture(sqsAsyncClient.deleteMessage(deleteRequest))
            .then();
    }
    
}
