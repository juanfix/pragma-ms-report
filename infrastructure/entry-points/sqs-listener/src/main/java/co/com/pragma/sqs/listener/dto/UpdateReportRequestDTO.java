package co.com.pragma.sqs.listener.dto;

public record UpdateReportRequestDTO(
        String id,
        Long countToAdd,
        Double totalAmountToAdd
) {
}
