package com.example.booking_service.client;

import com.example.booking_service.dto.UserClientResponse;
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
public class UserClient {

    private final RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    public UserClientResponse buscarUsuario(Long userId) {
        String url = userServiceUrl + "/api/v1/users/" + userId;
        try {
            return restTemplate.getForObject(url, UserClientResponse.class);

        } catch (HttpClientErrorException.NotFound ex) {
            throw new RecursoNaoEncontradoException("Usuário", userId);

        } catch (ResourceAccessException ex) {
            throw new ServicoIndisponivelException(
                    "user-service indisponível. Tente novamente em instantes."
            );
        }
    }
}