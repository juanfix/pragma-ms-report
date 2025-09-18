package co.com.pragma.api.report.dto;

public record UnauthorizedDTO(
        String timestamp,
        String status,
        String error,
        String message
) {
}
