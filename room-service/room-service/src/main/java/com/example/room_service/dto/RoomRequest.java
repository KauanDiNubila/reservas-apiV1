package com.example.room_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoomRequest {

    @NotBlank(message = "Nome da sala é obrigatório")
    private String nome;

    @Min(value = 1, message = "Capacidade mínima é 1")
    private Integer capacidade;

    private String localizacao;

    private boolean ativa = true;
}
