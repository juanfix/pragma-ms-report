package co.com.pragma.webclient;

import co.com.pragma.jjwtsecurity.jwt.provider.JwtProvider;
import co.com.pragma.usecase.report.dto.UserMailByRoleDTO;
import co.com.pragma.usecase.report.interfaces.UserUseCaseInterface;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Component
public class UserWebClientAdapter implements UserUseCaseInterface {

    private final WebClient webClient;
    private final JwtProvider jwtProvider;
    private static final Logger logger = Logger.getLogger(UserWebClientAdapter.class.getName());

    public UserWebClientAdapter(@Qualifier("userWebClient") WebClient webClient, JwtProvider jwtProvider) {
        this.webClient = webClient;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Flux<UserMailByRoleDTO> getAllUserMailByRole(Long id) {
        logger.info("UserWebClient: Searching user mail list by role=" + id);
        return jwtProvider.generateToken()
                .flatMapMany(jwtToken ->  // 👈 importante: flatMapMany para devolver Flux
                        webClient.get()
                                .uri("/api/v1/user/by-role/{id}", id)
                                .header("Authorization", "Bearer " + jwtToken)
                                .exchangeToFlux(response -> {
                                    if (response.statusCode().is2xxSuccessful()) {
                                        return response.bodyToFlux(UserMailByRoleDTO.class)
                                                .doOnNext(valid -> logger.info("✅ User found: " + valid));
                                    } else {
                                        return Flux.just(new UserMailByRoleDTO("Not found", "Not found"))
                                                .doOnNext(valid -> logger.info("❌ User not found: " + valid));
                                    }
                                })
                )
                .onErrorResume(ex -> {
                    logger.info("WebClient Error: " + ex.getMessage());
                    return Flux.just(new UserMailByRoleDTO("Not found", "Not found"));
                });
    }
}
