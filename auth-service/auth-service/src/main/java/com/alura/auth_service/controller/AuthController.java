package com.alura.auth_service.controller;

import com.alura.auth_service.dto.LoginRequest;
import com.alura.auth_service.dto.LoginResponse;
import com.alura.auth_service.dto.RegisterRequest;
import com.alura.auth_service.service.AuthService;
import com.alura.auth_service.service.GithubOAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final GithubOAuthService githubOAuthService;

    @PostMapping("/register")
    public ResponseEntity<Void> registrar(@RequestBody @Valid RegisterRequest request) {
        authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/github")
    public ResponseEntity<Void> loginGithub() {
        String url = githubOAuthService.gerarUrlAutorizacao();
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }

    @GetMapping("/github/callback")
    public ResponseEntity<LoginResponse> githubCallback(@RequestParam String code) {
        System.out.println(">>> CODE RECEBIDO: " + code);
        return ResponseEntity.ok(githubOAuthService.processarCallback(code));
    }
}

