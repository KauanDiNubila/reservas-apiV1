package com.example.room_service.dto;

import com.example.room_service.entity.Room;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoomResponse {

    private Long id;
    private String nome;
    private Integer capacidade;
    private String localizacao;
    private boolean ativa;

    public static RoomResponse de(Room room) {
        RoomResponse response = new RoomResponse();
        response.setId(room.getId());
        response.setNome(room.getNome());
        response.setCapacidade(room.getCapacidade());
        response.setLocalizacao(room.getLocalizacao());
        response.setAtiva(room.isAtiva());
        return response;
    }
}
