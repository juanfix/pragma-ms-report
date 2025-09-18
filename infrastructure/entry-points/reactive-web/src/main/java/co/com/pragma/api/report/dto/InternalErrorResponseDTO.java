package co.com.pragma.api.report.dto;

public record InternalErrorResponseDTO(
        String timestamp,
        String path,
        String status,
        String error,
        String requestId,
        String message,
        String trace
) {
}
