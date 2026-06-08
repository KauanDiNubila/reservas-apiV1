package com.example.booking_service.controller;

import com.example.booking_service.dto.BookingRequest;
import com.example.booking_service.dto.BookingResponse;
import com.example.booking_service.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<List<BookingResponse>> listar() {
        return ResponseEntity.ok(bookingService.listarBookings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.buscarBookingPorId(id));
    }

    @PostMapping
    public ResponseEntity<BookingResponse> criar(@RequestBody @Valid BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.criarBooking(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid BookingRequest request) {
        return ResponseEntity.ok(bookingService.atualizarBooking(id, request));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        bookingService.cancelarBooking(id);
        return ResponseEntity.noContent().build();
    }
}
