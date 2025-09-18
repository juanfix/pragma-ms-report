package co.com.pragma.api.report;

import co.com.pragma.api.report.dto.InternalErrorResponseDTO;
import co.com.pragma.api.report.dto.UnauthorizedDTO;
import co.com.pragma.model.report.Report;
import co.com.pragma.usecase.report.ReportUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RestController
@Tag(name = "Reports", description = "Reports endpoints")
@Slf4j
public class Handler {

    private final ReportUseCase reportUseCase;

    public Handler(ReportUseCase reportUseCase) {
        this.reportUseCase = reportUseCase;
    }

    @GetMapping(path = "/api/v1/report", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Find a report of the approved loans.",
            description = "Returns the report about approved loans.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ok",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Report.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad request",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UnauthorizedDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UnauthorizedDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UnauthorizedDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = InternalErrorResponseDTO.class)))
            }
    )
    public Mono<ServerResponse> listenGetReports(ServerRequest serverRequest) {
        return reportUseCase.getTotalAmount()
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response)
                );
    }
}
