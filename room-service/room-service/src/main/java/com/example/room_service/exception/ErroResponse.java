package com.example.room_service.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErroResponse {

    private final int status;
    private final String mensagem;
    private final LocalDateTime timestamp;
    private final Map<String, String> campos;

    public ErroResponse(int status, String mensagem, LocalDateTime timestamp) {
        this(status, mensagem, timestamp, null);
    }

    public ErroResponse(int status, String mensagem, LocalDateTime timestamp,
                        Map<String, String> campos) {
        this.status    = status;
        this.mensagem  = mensagem;
        this.timestamp = timestamp;
        this.campos    = campos;
    }
}
