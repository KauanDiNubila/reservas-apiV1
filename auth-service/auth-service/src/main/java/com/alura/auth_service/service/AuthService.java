package com.alura.auth_service.service;

import com.alura.auth_service.config.JwtConfig;
import com.alura.auth_service.dto.LoginRequest;
import com.alura.auth_service.dto.LoginResponse;
import com.alura.auth_service.dto.RegisterRequest;
import com.alura.auth_service.entity.UserCredential;
import com.alura.auth_service.exception.RegraDeNegocioException;
import com.alura.auth_service.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserCredentialRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;

    @Transactional
    public void registrar(RegisterRequest request) {
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new RegraDeNegocioException("Email já cadastrado.");
        }

        UserCredential credential = new UserCredential();
        credential.setEmail(request.getEmail());
        credential.setSenha(passwordEncoder.encode(request.getSenha()));

        if (request.getRole() != null &&
                request.getRole().equalsIgnoreCase("ADMIN")) {
            credential.setRole(UserCredential.Role.ADMIN);
        }

        repository.save(credential);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        UserCredential credential = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Email ou senha inválidos."
                ));

        if (!passwordEncoder.matches(request.getSenha(), credential.getSenha())) {
            throw new RegraDeNegocioException("Email ou senha inválidos.");
        }

        String token = jwtConfig.gerarToken(
                credential.getEmail(),
                credential.getRole().name(),
                credential.getId()
        );

        return new LoginResponse(token, credential.getRole().name(),
                jwtConfig.getExpiration());
    }
}

