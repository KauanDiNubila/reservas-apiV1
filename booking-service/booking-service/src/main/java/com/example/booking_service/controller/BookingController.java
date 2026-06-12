package com.example.booking_service.controller;

import com.example.booking_service.dto.BookingRequest;
import com.example.booking_service.dto.BookingResponse;
import com.example.booking_service.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Operações de gerenciamento de reservas de salas")
public class BookingController {

    private final BookingService bookingService;

    @Operation(summary = "Listar reservas", description = "Retorna todas as reservas cadastradas")
    @GetMapping
    public ResponseEntity<List<BookingResponse>> listar() {
        return ResponseEntity.ok(bookingService.listarBookings());
    }

    @Operation(summary = "Buscar reserva por ID", description = "Retorna uma reserva específica pelo seu identificador")
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.buscarBookingPorId(id));
    }

    @Operation(summary = "Criar reserva", description = "Cria uma nova reserva, validando conflitos de horário na sala")
    @PostMapping
    public ResponseEntity<BookingResponse> criar(@RequestBody @Valid BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.criarBooking(request));
    }

    @Operation(summary = "Atualizar reserva", description = "Atualiza os dados de uma reserva existente")
    @PutMapping("/{id}")
    public ResponseEntity<BookingResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid BookingRequest request) {
        return ResponseEntity.ok(bookingService.atualizarBooking(id, request));
    }

    @Operation(summary = "Cancelar reserva", description = "Cancela uma reserva, alterando seu status para CANCELADA")
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        bookingService.cancelarBooking(id);
        return ResponseEntity.noContent().build();
    }
}