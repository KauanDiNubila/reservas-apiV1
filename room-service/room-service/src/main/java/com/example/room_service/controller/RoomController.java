package com.example.room_service.controller;

import com.example.room_service.dto.RoomRequest;
import com.example.room_service.dto.RoomResponse;
import com.example.room_service.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Salas", description = "Operações de gerenciamento de salas")
@RequestMapping("/api/v1/rooms")
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "Listar salas", description = "Retorna todas as salas cadastradas")
    @GetMapping
    public ResponseEntity<List<RoomResponse>> listar() {
        return ResponseEntity.ok(roomService.listar());
    }

    @Operation(summary = "Buscar sala por ID", description = "Retorna uma sala específica pelo seu identificador")
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.buscarPorId(id));
    }

    @Operation(summary = "Cadastrar sala", description = "Cria uma nova sala no sistema")
    @PostMapping
    public ResponseEntity<RoomResponse> criar(@RequestBody @Valid RoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roomService.criar(request));
    }

    @Operation(summary = "Atualizar sala", description = "Atualiza os dados de uma sala existente")
    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid RoomRequest request) {
        return ResponseEntity.ok(roomService.atualizar(id, request));
    }

    @Operation(summary = "Remover sala", description = "Exclui uma sala do sistema pelo seu identificador")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        roomService.remover(id);
        return ResponseEntity.noContent().build();
    }
}