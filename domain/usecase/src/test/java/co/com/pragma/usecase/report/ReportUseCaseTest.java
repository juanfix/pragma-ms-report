package co.com.pragma.usecase.report;

import co.com.pragma.model.report.Report;
import co.com.pragma.model.report.gateways.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class ReportUseCaseTest {
    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportUseCase reportUseCase;

    private Report sampleReport;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleReport = Report.builder()
                .id("123")
                .count(10L)
                .totalAmount(500.0)
                .build();
    }

    @Test
    void incrementCounterAndAmount_ShouldReturnUpdatedReport() {
        // arrange
        Long countToAdd = 5L;
        Double amountToAdd = 200.0;

        Report updatedReport = Report.builder()
                .id("123")
                .count(sampleReport.getCount() + countToAdd)
                .totalAmount(sampleReport.getTotalAmount() + amountToAdd)
                .build();

        when(reportRepository.incrementCounterAndAmount("123", countToAdd, amountToAdd))
                .thenReturn(Mono.just(updatedReport));

        // act & assert
        StepVerifier.create(reportUseCase.incrementCounterAndAmount("123", countToAdd, amountToAdd))
                .expectNext(updatedReport)
                .verifyComplete();

        verify(reportRepository, times(1)).incrementCounterAndAmount("123", countToAdd, amountToAdd);
    }

    @Test
    void getTotalAmount_ShouldReturnReport() {
        // arrange
        when(reportRepository.getReport()).thenReturn(Mono.just(sampleReport));

        // act & assert
        StepVerifier.create(reportUseCase.getTotalAmount())
                .expectNext(sampleReport)
                .verifyComplete();

        verify(reportRepository, times(1)).getReport();
    }

    @Test
    void getTotalAmount_ShouldReturnEmpty_WhenNoReport() {
        // arrange
        when(reportRepository.getReport()).thenReturn(Mono.empty());

        // act & assert
        StepVerifier.create(reportUseCase.getTotalAmount())
                .verifyComplete();

        verify(reportRepository, times(1)).getReport();
    }
}
