package co.com.pragma.usecase.report;

import co.com.pragma.model.report.Report;
import co.com.pragma.model.report.gateways.ReportRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ReportUseCase implements ReportUseCaseInterface {

    private final ReportRepository reportRepository;

    @Override
    public Mono<Report> incrementCounterAndAmount(String id, Long countToAdd, Double totalAmountToAdd) {
        return reportRepository.incrementCounterAndAmount(id, countToAdd, totalAmountToAdd);
    }

    @Override
    public Mono<Report> getTotalAmount() {
        return reportRepository.getReport();
    }
}
