package com.alura.auth_service.controller;

import com.alura.auth_service.dto.LoginRequest;
import com.alura.auth_service.dto.LoginResponse;
import com.alura.auth_service.dto.RegisterRequest;
import com.alura.auth_service.service.AuthService;
import com.alura.auth_service.service.GithubOAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Operações de autenticação, registro e login via JWT e OAuth2 GitHub")
public class AuthController {

    private final AuthService authService;
    private final GithubOAuthService githubOAuthService;

    @Operation(summary = "Registrar usuário", description = "Cria uma nova credencial de acesso com email, senha e papel (role)")
    @PostMapping("/register")
    public ResponseEntity<Void> registrar(@RequestBody @Valid RegisterRequest request) {
        authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Login", description = "Autentica o usuário e retorna um token JWT")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Iniciar login com GitHub", description = "Redireciona o usuário para a página de autorização do GitHub")
    @GetMapping("/github")
    public ResponseEntity<Void> loginGithub() {
        String url = githubOAuthService.gerarUrlAutorizacao();
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }

    @Operation(summary = "Callback do GitHub", description = "Recebe o código de autorização do GitHub e retorna um token JWT")
    @GetMapping("/github/callback")
    public ResponseEntity<LoginResponse> githubCallback(@RequestParam String code) {
        return ResponseEntity.ok(githubOAuthService.processarCallback(code));
    }
}