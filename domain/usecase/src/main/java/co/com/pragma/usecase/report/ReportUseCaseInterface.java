package co.com.pragma.usecase.report;

import co.com.pragma.model.report.Report;
import reactor.core.publisher.Mono;

public interface ReportUseCaseInterface {
    Mono<Report> incrementCounterAndAmount(String id, Long countToAdd, Double totalAmountToAdd);
    Mono<Report> getTotalAmount();
}
