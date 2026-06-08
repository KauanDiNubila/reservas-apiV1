package com.example.booking_service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter @Setter @NoArgsConstructor
public class RoomClientResponse {
    private Long id;
    private String nome;
    private boolean ativa;
}
