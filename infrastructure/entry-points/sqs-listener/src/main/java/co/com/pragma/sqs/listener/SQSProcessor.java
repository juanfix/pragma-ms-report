package co.com.pragma.sqs.listener;

import co.com.pragma.sqs.listener.dto.UpdateReportRequestDTO;
import co.com.pragma.usecase.report.ReportUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {

    private static final Logger log = LoggerFactory.getLogger(SQSProcessor.class);

    private static final String REPORT_ID = "approvedLoans";
    private final ReportUseCase reportUseCase;

    @Override
    public Mono<Void> apply(Message message) {
        System.out.println(message.body());
        ObjectMapper mapper = new ObjectMapper();
        try {
            log.info("Se recibio un mensaje nuevo a la cola de SQS para actualizar la tabla de reportes: {}", message.body());

            UpdateReportRequestDTO body = mapper.readValue(message.body().toString(), UpdateReportRequestDTO.class);

            return reportUseCase.incrementCounterAndAmount(REPORT_ID, body.countToAdd(), body.totalAmountToAdd()).then();
        } catch (Exception e) {
            log.error("Error procesando mensaje: {}", e.getMessage(), e);
            return Mono.error(e);
        }
    }
}
