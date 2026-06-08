package com.example.booking_service.entity;

import com.example.booking_service.exception.RegraDeNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.*;

class BookingEntityTest {

    private static final LocalDateTime FUTURO = LocalDateTime.now().plusDays(1);

    @Test
    @DisplayName("Deve lançar exceção quando fim é anterior ao início")
    void deveLancarExcecaoQuandoFimAntesDoInicio() {
        Booking booking = new Booking();
        booking.setInicio(FUTURO.plusHours(4));
        booking.setFim(FUTURO.plusHours(2));

        assertThatThrownBy(booking::validarDatas)
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("anterior");
    }

    @Test
    @DisplayName("Deve lançar exceção quando início é igual ao fim")
    void deveLancarExcecaoQuandoInicioIgualFim() {
        Booking booking = new Booking();
        booking.setInicio(FUTURO);
        booking.setFim(FUTURO);

        assertThatThrownBy(booking::validarDatas)
                .isInstanceOf(RegraDeNegocioException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção quando início está no passado")
    void deveLancarExcecaoQuandoInicioNoPasado() {
        Booking booking = new Booking();
        booking.setInicio(LocalDateTime.now().minusHours(1));
        booking.setFim(LocalDateTime.now().plusHours(1));

        assertThatThrownBy(booking::validarDatas)
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("passado");
    }

    @Test
    @DisplayName("Deve aceitar datas válidas sem lançar exceção")
    void deveAceitarDatasValidas() {
        Booking booking = new Booking();
        booking.setInicio(FUTURO);
        booking.setFim(FUTURO.plusHours(2));

        assertThatNoException().isThrownBy(booking::validarDatas);
    }

    @Test
    @DisplayName("Deve cancelar booking ativo com sucesso")
    void deveCancelarBookingAtivo() {
        Booking booking = new Booking();
        booking.setStatus(StatusBooking.ATIVA);

        booking.cancelar();

        assertThat(booking.getStatus()).isEqualTo(StatusBooking.CANCELADA);
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar booking já cancelado")
    void deveLancarExcecaoAoCancelarJaCancelado() {
        Booking booking = new Booking();
        booking.setStatus(StatusBooking.CANCELADA);

        assertThatThrownBy(booking::cancelar)
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("já está cancelada");
    }
}
