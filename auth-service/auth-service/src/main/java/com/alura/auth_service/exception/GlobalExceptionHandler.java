package com.alura.auth_service.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Component
@Order(-1)
@RequiredArgsConstructor
public class GlobalExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status;
        String mensagem;

        if (ex instanceof RecursoNaoEncontradoException) {
            status = HttpStatus.NOT_FOUND;
            mensagem = ex.getMessage();
        } else if (ex instanceof RegraDeNegocioException) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
            mensagem = ex.getMessage();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            mensagem = "Erro interno inesperado.";
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        try {
            ErroResponse erro = new ErroResponse(
                    status.value(), mensagem, LocalDateTime.now()
            );
            byte[] bytes = objectMapper.writeValueAsBytes(erro);
            DataBuffer buffer = exchange.getResponse()
                    .bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}

