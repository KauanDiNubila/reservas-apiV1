package com.example.user_service.controller;

import com.example.user_service.dto.UserRequest;
import com.example.user_service.dto.UserResponse;
import com.example.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Operações de gerenciamento de usuários")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Listar usuários", description = "Retorna todos os usuários cadastrados")
    @GetMapping
    public ResponseEntity<List<UserResponse>> listar() {
        return ResponseEntity.ok(userService.listar());
    }

    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário específico pelo seu identificador")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.buscarPorId(id));
    }

    @Operation(summary = "Cadastrar usuário", description = "Cria um novo usuário no sistema")
    @PostMapping
    public ResponseEntity<UserResponse> criar(@RequestBody @Valid UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.criar(request));
    }

    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid UserRequest request) {
        return ResponseEntity.ok(userService.atualizar(id, request));
    }

    @Operation(summary = "Remover usuário", description = "Exclui um usuário do sistema pelo seu identificador")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        userService.remover(id);
        return ResponseEntity.noContent().build();
    }
}