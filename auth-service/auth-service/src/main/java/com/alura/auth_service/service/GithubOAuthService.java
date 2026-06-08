package com.alura.auth_service.service;

import com.alura.auth_service.config.GithubOAuthConfig;
import com.alura.auth_service.config.JwtConfig;
import com.alura.auth_service.dto.GithubUserResponse;
import com.alura.auth_service.dto.LoginResponse;
import com.alura.auth_service.entity.UserCredential;
import com.alura.auth_service.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.Map;

@Service
@RequiredArgsConstructor
public class GithubOAuthService {

    private final GithubOAuthConfig githubConfig;
    private final UserCredentialRepository repository;
    private final JwtConfig jwtConfig;

    private final WebClient webClient = WebClient.create();

    public String gerarUrlAutorizacao() {
        return GithubOAuthConfig.AUTHORIZE_URL
                + "?client_id=" + githubConfig.getClientId()
                + "&redirect_uri=" + githubConfig.getRedirectUri()
                + "&scope=user:email";
    }

    @Transactional
    public LoginResponse processarCallback(String code) {
        String accessToken     = trocarCodePorToken(code);
        GithubUserResponse githubUser = buscarPerfilGithub(accessToken);
        UserCredential usuario = salvarOuAtualizar(githubUser);

        String jwt = jwtConfig.gerarToken(
                usuario.getEmail(),
                usuario.getRole().name(),
                usuario.getId()
        );

        return new LoginResponse(jwt, usuario.getRole().name(),
                jwtConfig.getExpiration());
    }

    private String trocarCodePorToken(String code) {
        Map response = webClient.post()
                .uri(GithubOAuthConfig.TOKEN_URL)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(Map.of(
                        "client_id",     githubConfig.getClientId(),
                        "client_secret", githubConfig.getClientSecret(),
                        "code",          code,
                        "redirect_uri",  githubConfig.getRedirectUri()
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        System.out.println(">>> RESPONSE GITHUB TOKEN: " + response);
        String accessToken = (String) response.get("access_token");
        System.out.println(">>> ACCESS TOKEN: " + accessToken);
        return accessToken;
    }

    private GithubUserResponse buscarPerfilGithub(String accessToken) {
        return webClient.get()
                .uri(GithubOAuthConfig.USER_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(GithubUserResponse.class)
                .block();
    }

    private UserCredential salvarOuAtualizar(GithubUserResponse githubUser) {
        return repository.findByGithubId(githubUser.getId())
                .map(usuario -> {
                    usuario.setGithubLogin(githubUser.getLogin());
                    return repository.save(usuario);
                })
                .orElseGet(() -> {
                    String email = githubUser.getEmail() != null
                            ? githubUser.getEmail()
                            : githubUser.getLogin() + "@github.com";

                    return repository.findByEmail(email)
                            .map(usuario -> {
                                usuario.setGithubId(githubUser.getId());
                                usuario.setGithubLogin(githubUser.getLogin());
                                return repository.save(usuario);
                            })
                            .orElseGet(() -> {
                                UserCredential novo = new UserCredential();
                                novo.setEmail(email);
                                novo.setGithubId(githubUser.getId());
                                novo.setGithubLogin(githubUser.getLogin());
                                novo.setRole(UserCredential.Role.USER);
                                return repository.save(novo);
                            });
                });
    }
}
