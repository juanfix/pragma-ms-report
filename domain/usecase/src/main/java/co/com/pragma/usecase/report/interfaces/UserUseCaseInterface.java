package co.com.pragma.usecase.report.interfaces;

import co.com.pragma.usecase.report.dto.UserMailByRoleDTO;
import reactor.core.publisher.Flux;

public interface UserUseCaseInterface {
    Flux<UserMailByRoleDTO> getAllUserMailByRole(Long id);
}
