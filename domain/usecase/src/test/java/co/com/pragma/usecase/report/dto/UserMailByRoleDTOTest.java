package co.com.pragma.usecase.report.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserMailByRoleDTOTest {
    @Test
    void shouldCreateALoanTypeWithArgs() {
        String name = "Juanjo";
        String email = "juanfix@gmail.com";

        UserMailByRoleDTO userMailByRoleDTO = new UserMailByRoleDTO(name, email);

        assertNotNull(userMailByRoleDTO);
        assertEquals(name, userMailByRoleDTO.name());
        assertEquals(email, userMailByRoleDTO.email());
    }
}
