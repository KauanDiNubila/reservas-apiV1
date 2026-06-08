package com.example.room_service.controller;

import com.example.room_service.dto.RoomRequest;
import com.example.room_service.dto.RoomResponse;
import com.example.room_service.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<List<RoomResponse>> listar() {
        return ResponseEntity.ok(roomService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<RoomResponse> criar(@RequestBody @Valid RoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roomService.criar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid RoomRequest request) {
        return ResponseEntity.ok(roomService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        roomService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
