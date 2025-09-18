package co.com.pragma.model.report;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Report {
    private String id;
    private Long count;
    private Double totalAmount;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
