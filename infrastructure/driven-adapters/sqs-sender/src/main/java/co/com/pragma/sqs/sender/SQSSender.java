package co.com.pragma.sqs.sender;

import co.com.pragma.sqs.sender.config.SQSSenderProperties;
import co.com.pragma.usecase.report.dto.SqsEmailMessageDTO;
import co.com.pragma.usecase.report.interfaces.SqsUseCaseInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements SqsUseCaseInterface {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Mono<String> send(String message) {
        return Mono.fromCallable(() -> buildRequest(message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }

    @Override
    public Mono<Void> publishEmailRequest(SqsEmailMessageDTO sqsEmailMessageDTO) {
        return Mono.fromCallable(() -> toJson(sqsEmailMessageDTO))
                .flatMap(json -> {
                    return send(json);
                })
                .doOnSuccess(response -> log.info("Mensaje enviado a SQS email: {}", response))
                .doOnError(err -> log.error("Error enviando mensaje a SQS email: {}", err.getMessage()))
                .then();
    }

    private String toJson(Record sqsMessageDTO) throws JsonProcessingException {
        return objectMapper.writeValueAsString(sqsMessageDTO);
    }
}
