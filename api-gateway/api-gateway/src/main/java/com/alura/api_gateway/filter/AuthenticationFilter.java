package com.alura.api_gateway.filter;

import com.alura.api_gateway.config.JwtConfig;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AuthenticationFilter extends
        AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtConfig jwtConfig;

    private static final List<String> ROTAS_ADMIN = List.of(
            "/api/v1/rooms"
    );

    public AuthenticationFilter(JwtConfig jwtConfig) {
        super(Config.class);
        this.jwtConfig = jwtConfig;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return escreverErro(exchange, HttpStatus.UNAUTHORIZED,
                        "Token ausente, invalido ou expirado.");
            }

            String token = authHeader.substring(7);

            if (!jwtConfig.tokenValido(token)) {
                return escreverErro(exchange, HttpStatus.UNAUTHORIZED,
                        "Token ausente, invalido ou expirado.");
            }

            Claims claims = jwtConfig.extrairClaims(token);
            String role = claims.get("role", String.class);

            if (rotaExigeAdmin(exchange) && !"ADMIN".equals(role)) {
                return escreverErro(exchange, HttpStatus.FORBIDDEN,
                        "Voce nao tem permissao para acessar este recurso.");
            }

            var request = exchange.getRequest().mutate()
                    .header("X-User-Id", claims.get("userId", Long.class).toString())
                    .header("X-User-Role", role)
                    .header("X-User-Email", claims.getSubject())
                    .build();

            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    private boolean rotaExigeAdmin(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        return ROTAS_ADMIN.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> escreverErro(ServerWebExchange exchange,
                                    HttpStatus status, String mensagem) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        String body = """
                {
                    "status": %d,
                    "mensagem": "%s",
                    "timestamp": "%s"
                }
                """.formatted(status.value(), mensagem, LocalDateTime.now());

        byte[] bytes = body.getBytes();
        DataBuffer buffer = exchange.getResponse()
                .bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    public static class Config {}
}
