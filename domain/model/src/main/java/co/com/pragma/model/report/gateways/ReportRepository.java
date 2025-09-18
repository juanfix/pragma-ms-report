package co.com.pragma.model.report.gateways;

import co.com.pragma.model.report.Report;
import reactor.core.publisher.Mono;

public interface ReportRepository {
    Mono<Report> incrementCounterAndAmount(String id, Long countToAdd, Double totalAmountToAdd);
    Mono<Report> getReport();
}
