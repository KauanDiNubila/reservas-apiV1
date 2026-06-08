package com.example.booking_service.service;

import com.example.booking_service.dto.RoomClientResponse;
import com.example.booking_service.entity.Booking;
import com.example.booking_service.entity.StatusBooking;
import com.example.booking_service.exception.RegraDeNegocioException;
import com.example.booking_service.repository.BookingRepository;
import com.example.booking_service.roomClient.RoomClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingValidacaoService {

    private final BookingRepository bookingRepository;
    private final RoomClient roomClient;

    public void validar(Booking booking) {
        validarDatas(booking);
        validarSalaAtiva(booking);
        validarConflitoDeHorario(booking);
    }

    private void validarDatas(Booking booking) {
        booking.validarDatas();
    }

    private void validarSalaAtiva(Booking booking) {
        RoomClientResponse room = roomClient.buscarSala(booking.getRoomId());
        if (!room.isAtiva()) {
            throw new RegraDeNegocioException(
                    "A sala '" + room.getNome() + "' está inativa e não pode ser reservada."
            );
        }
    }

    private void validarConflitoDeHorario(Booking booking) {
        Long idAtual = booking.getId() != null ? booking.getId() : -1L;

        List<Booking> conflitos = bookingRepository.buscarConflitos(
                booking.getRoomId(),
                booking.getInicio(),
                booking.getFim(),
                StatusBooking.ATIVA,
                idAtual
        );

        if (!conflitos.isEmpty()) {
            Booking conflito = conflitos.get(0);
            throw new RegraDeNegocioException(
                    "Conflito de horário: sala reservada das "
                            + conflito.getInicio() + " às " + conflito.getFim() + "."
            );
        }
    }
}
