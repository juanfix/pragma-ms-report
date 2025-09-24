package co.com.pragma.schedulertask;

import co.com.pragma.usecase.report.ReportUseCase;
import co.com.pragma.usecase.report.dto.SqsEmailMessageDTO;
import co.com.pragma.usecase.report.interfaces.SqsUseCaseInterface;
import co.com.pragma.usecase.report.interfaces.UserUseCaseInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class DailyTaskScheduler {

    private static final Logger logger = Logger.getLogger(DailyTaskScheduler.class.getName());

    private final ReportUseCase reportUseCase;
    private final UserUseCaseInterface userUseCaseInterface;
    private final SqsUseCaseInterface sqsUseCaseInterface;

    // @Scheduled(cron = "0 0 0 * * ?") // todos los días a medianoche
    @Scheduled(cron = "0/60 * * * * *")
    public Flux<Void> runDailyTask() {
        logger.info("Enviando reporte diario");
        return reportUseCase.getTotalAmount() // (1) Mono<Report>
                .flatMapMany(report ->
                        userUseCaseInterface.getAllUserMailByRole(1L) // (2) Flux<UserMailByRoleDTO>
                                .flatMap(userMailByRoleDTO -> {
                                    logger.info("📧 Enviando correo a: " + userMailByRoleDTO);
                                    if(userMailByRoleDTO.email().equals("Not found")){
                                        return Flux.empty();
                                    } else {
                                        return sqsUseCaseInterface.publishEmailRequest(SqsEmailMessageDTO.builder()
                                                .to(userMailByRoleDTO.email())
                                                .subject("Reporte diario de préstamos aprobados")
                                                .body(String.format(
                                                        "Hola %s, se ha generado el reporte diario de préstamos aprobados:" +
                                                                " \nCantidad de prestamos: %s \n" +
                                                                "Monto total: %,.2f",
                                                        userMailByRoleDTO.name(), report.getCount(), report.getTotalAmount()
                                                ))
                                                .build());
                                    }
                                })
                )
                .onErrorResume(throwable -> {
                    logger.severe("Error al generar el reporte diario: " + throwable.getMessage());
                    return Flux.empty();
                });
    }
}
