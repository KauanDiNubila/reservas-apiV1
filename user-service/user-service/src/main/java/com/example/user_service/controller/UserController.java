package com.example.user_service.controller;

import com.example.user_service.dto.UserRequest;
import com.example.user_service.dto.UserResponse;
import com.example.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> listar() {
        return ResponseEntity.ok(userService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<UserResponse> criar(@RequestBody @Valid UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.criar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid UserRequest request) {
        return ResponseEntity.ok(userService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        userService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
