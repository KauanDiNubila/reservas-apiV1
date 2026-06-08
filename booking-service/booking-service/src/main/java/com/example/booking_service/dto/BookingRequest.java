package com.example.booking_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class BookingRequest {

    @NotNull(message = "Sala é obrigatória")
    private Long roomId;

    @NotNull(message = "Usuário é obrigatório")
    private Long userId;

    @NotNull(message = "Início é obrigatório")
    private LocalDateTime inicio;

    @NotNull(message = "Fim é obrigatório")
    private LocalDateTime fim;
}
