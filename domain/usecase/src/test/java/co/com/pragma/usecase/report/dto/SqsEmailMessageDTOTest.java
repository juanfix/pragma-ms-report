package co.com.pragma.usecase.report.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SqsEmailMessageDTOTest {
    @Test
    void shouldCreateALoanTypeWithArgs() {
        String to = "juanfix@gmail.com";
        String subject = "Subject";
        String body = "Body message";

        SqsEmailMessageDTO sqsEmailMessageDTO = new SqsEmailMessageDTO(to, subject,body);

        assertNotNull(sqsEmailMessageDTO);
        assertEquals(to, sqsEmailMessageDTO.to());
        assertEquals(subject, sqsEmailMessageDTO.subject());
        assertEquals(body, sqsEmailMessageDTO.body());
    }

}
