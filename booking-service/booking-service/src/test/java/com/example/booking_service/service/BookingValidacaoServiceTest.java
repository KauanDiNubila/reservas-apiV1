package com.example.booking_service.service;

import com.example.booking_service.dto.RoomClientResponse;
import com.example.booking_service.entity.Booking;
import com.example.booking_service.entity.StatusBooking;
import com.example.booking_service.exception.RegraDeNegocioException;
import com.example.booking_service.repository.BookingRepository;
import com.example.booking_service.roomClient.RoomClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingValidacaoServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomClient roomClient;

    @InjectMocks
    private BookingValidacaoService validacaoService;

    private static final LocalDateTime BASE = LocalDateTime.now().plusDays(1);

    private Booking booking;
    private RoomClientResponse roomAtivo;
    private RoomClientResponse roomInativo;

    @BeforeEach
    void setUp() {
        booking = new Booking();
        booking.setRoomId(1L);
        booking.setUserId(1L);
        booking.setInicio(BASE.plusHours(2));
        booking.setFim(BASE.plusHours(4));
        booking.setStatus(StatusBooking.ATIVA);

        roomAtivo = new RoomClientResponse();
        roomAtivo.setId(1L);
        roomAtivo.setNome("Sala A");
        roomAtivo.setAtiva(true);

        roomInativo = new RoomClientResponse();
        roomInativo.setId(1L);
        roomInativo.setNome("Sala A");
        roomInativo.setAtiva(false);
    }

    @Test
    @DisplayName("Deve validar sem erros quando tudo está correto")
    void deveValidarSemErros() {
        when(roomClient.buscarSala(1L)).thenReturn(roomAtivo);
        when(bookingRepository.buscarConflitos(any(), any(), any(), any(), any()))
                .thenReturn(List.of());

        assertThatNoException().isThrownBy(() -> validacaoService.validar(booking));
    }

    @Test
    @DisplayName("Deve lançar exceção quando sala está inativa")
    void deveLancarExcecaoParaSalaInativa() {
        when(roomClient.buscarSala(1L)).thenReturn(roomInativo);

        assertThatThrownBy(() -> validacaoService.validar(booking))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("inativa");
    }

    @Test
    @DisplayName("Deve lançar exceção quando há conflito de horário")
    void deveLancarExcecaoQuandoHaConflito() {
        Booking conflito = new Booking();
        conflito.setId(99L);
        conflito.setInicio(BASE.plusHours(2));
        conflito.setFim(BASE.plusHours(4));

        when(roomClient.buscarSala(1L)).thenReturn(roomAtivo);
        when(bookingRepository.buscarConflitos(any(), any(), any(), any(), any()))
                .thenReturn(List.of(conflito));

        assertThatThrownBy(() -> validacaoService.validar(booking))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Conflito");
    }

    @Test
    @DisplayName("Deve permitir quando fim é igual ao início da reserva existente")
    void devePermitirQuandoFimIgualAoInicioExistente() {
        when(roomClient.buscarSala(1L)).thenReturn(roomAtivo);
        when(bookingRepository.buscarConflitos(any(), any(), any(), any(), any()))
                .thenReturn(List.of());

        // Nova termina exatamente quando a existente começa — sem conflito
        booking.setInicio(BASE);
        booking.setFim(BASE.plusHours(2));

        assertThatNoException().isThrownBy(() -> validacaoService.validar(booking));
    }

    @Test
    @DisplayName("Reserva cancelada não deve gerar conflito")
    void reservaCanceladaNaoDeveConflitar() {
        when(roomClient.buscarSala(1L)).thenReturn(roomAtivo);
        // Query já filtra ATIVA — mock retorna vazio simulando esse comportamento
        when(bookingRepository.buscarConflitos(any(), any(), any(), any(), any()))
                .thenReturn(List.of());

        assertThatNoException().isThrownBy(() -> validacaoService.validar(booking));
    }
}
