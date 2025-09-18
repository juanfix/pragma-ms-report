package co.com.pragma.api.report;

import co.com.pragma.api.config.GlobalPath;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {
    private final GlobalPath globalPath;
    private final Handler userHandler;

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET(globalPath.getGlobal() + "/report"), userHandler::listenGetReports);
    }
}
