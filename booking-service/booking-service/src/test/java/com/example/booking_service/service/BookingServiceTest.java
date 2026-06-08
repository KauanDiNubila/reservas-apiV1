package com.example.booking_service.service;

import com.example.booking_service.client.UserClient;
import com.example.booking_service.dto.BookingRequest;
import com.example.booking_service.dto.UserClientResponse;
import com.example.booking_service.entity.Booking;
import com.example.booking_service.entity.StatusBooking;
import com.example.booking_service.exception.RecursoNaoEncontradoException;
import com.example.booking_service.exception.RegraDeNegocioException;
import com.example.booking_service.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private BookingValidacaoService validacaoService;
    @Mock private UserClient userClient;

    @InjectMocks
    private BookingService bookingService;

    private static final LocalDateTime BASE = LocalDateTime.now().plusDays(1);

    private BookingRequest request;
    private UserClientResponse usuario;

    @BeforeEach
    void setUp() {
        request = new BookingRequest();
        request.setRoomId(1L);
        request.setUserId(1L);
        request.setInicio(BASE);
        request.setFim(BASE.plusHours(2));

        usuario = new UserClientResponse();
        usuario.setId(1L);
        usuario.setNome("João");
        usuario.setEmail("joao@email.com");
    }

    @Test
    @DisplayName("Deve criar booking com sucesso quando dados são válidos")
    void deveCriarBookingComSucesso() {
        when(userClient.buscarUsuario(1L)).thenReturn(usuario); // <- aqui
        when(bookingRepository.save(any())).thenAnswer(i -> {
            Booking b = i.getArgument(0);
            b.setId(1L);
            b.setRoomId(1L);
            b.setUserId(1L);
            b.setInicio(BASE);
            b.setFim(BASE.plusHours(2));
            b.setStatus(StatusBooking.ATIVA);
            return b;
        });

        var response = bookingService.criarBooking(request);

        assertThat(response).isNotNull();
        assertThat(response.getRoomId()).isEqualTo(1L);
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lançar 404 quando usuário não existe")
    void deveLancar404QuandoUsuarioNaoExiste() {
        when(userClient.buscarUsuario(1L))
                .thenThrow(new RecursoNaoEncontradoException("Usuário", 1L));

        assertThatThrownBy(() -> bookingService.criarBooking(request))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Usuário");
    }

    @Test
    @DisplayName("Não deve permitir editar booking cancelado")
    void naoDeveEditarBookingCancelado() {
        Booking cancelado = new Booking();
        cancelado.setId(1L);
        cancelado.setStatus(StatusBooking.CANCELADA);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(cancelado));

        assertThatThrownBy(() -> bookingService.atualizarBooking(1L, request))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("cancelada");
    }

    @Test
    @DisplayName("Deve cancelar booking ativo com sucesso")
    void deveCancelarBookingAtivo() {
        Booking ativo = new Booking();
        ativo.setId(1L);
        ativo.setStatus(StatusBooking.ATIVA);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(ativo));
        when(bookingRepository.save(any())).thenReturn(ativo);

        bookingService.cancelarBooking(1L);

        assertThat(ativo.getStatus()).isEqualTo(StatusBooking.CANCELADA);
        verify(bookingRepository).save(ativo);
    }

    @Test
    @DisplayName("Deve lançar 404 ao cancelar booking inexistente")
    void deveLancar404AoCancelarInexistente() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.cancelarBooking(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
