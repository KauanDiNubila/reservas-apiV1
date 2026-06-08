package com.example.booking_service.roomClient;

import com.example.booking_service.dto.RoomClientResponse;
import com.example.booking_service.exception.RecursoNaoEncontradoException;
import com.example.booking_service.exception.ServicoIndisponivelException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class RoomClient {

    private final RestTemplate restTemplate;

    @Value("${room.service.url}")
    private String roomServiceUrl;

    public RoomClientResponse buscarSala(Long roomId) {
        String url = roomServiceUrl + "/api/v1/rooms/" + roomId;
        try {
            return restTemplate.getForObject(url, RoomClientResponse.class);

        } catch (HttpClientErrorException.NotFound ex) {
            // room-service retornou 404 — sala não existe
            throw new RecursoNaoEncontradoException("Sala", roomId);

        } catch (ResourceAccessException ex) {
            // room-service fora do ar ou timeout
            throw new ServicoIndisponivelException(
                    "room-service indisponível. Tente novamente em instantes."
            );
        }
    }
}
