package co.com.pragma.usecase.report.interfaces;

import co.com.pragma.usecase.report.dto.SqsEmailMessageDTO;
import reactor.core.publisher.Mono;

public interface SqsUseCaseInterface {
    Mono<Void> publishEmailRequest(SqsEmailMessageDTO sqsEmailMessageDTO);
}
