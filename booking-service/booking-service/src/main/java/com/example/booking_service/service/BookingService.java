package com.example.booking_service.service;

import com.example.booking_service.client.UserClient;
import com.example.booking_service.dto.BookingRequest;
import com.example.booking_service.dto.BookingResponse;
import com.example.booking_service.entity.Booking;
import com.example.booking_service.entity.StatusBooking;
import com.example.booking_service.exception.RecursoNaoEncontradoException;
import com.example.booking_service.exception.RegraDeNegocioException;
import com.example.booking_service.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingValidacaoService validacaoService;
    private final UserClient userClient;

    @Transactional(readOnly = true)
    public List<BookingResponse> listarBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(BookingResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookingResponse buscarBookingPorId(Long id) {
        return BookingResponse.de(buscarEntidadePorId(id));
    }

    @Transactional
    public BookingResponse criarBooking(BookingRequest request) {
        userClient.buscarUsuario(request.getUserId());

        Booking booking = new Booking();
        booking.setRoomId(request.getRoomId());
        booking.setUserId(request.getUserId());
        booking.setInicio(request.getInicio());
        booking.setFim(request.getFim());

        validacaoService.validar(booking);

        return BookingResponse.de(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponse atualizarBooking(Long id, BookingRequest request) {
        Booking booking = buscarEntidadePorId(id);

        if (booking.getStatus() == StatusBooking.CANCELADA) {
            throw new RegraDeNegocioException(
                    "Não é possível editar uma reserva cancelada."
            );
        }

        userClient.buscarUsuario(request.getUserId());

        booking.setRoomId(request.getRoomId());
        booking.setUserId(request.getUserId());
        booking.setInicio(request.getInicio());
        booking.setFim(request.getFim());

        validacaoService.validar(booking);

        return BookingResponse.de(bookingRepository.save(booking));
    }

    @Transactional
    public void cancelarBooking(Long id) {
        Booking booking = buscarEntidadePorId(id);
        booking.cancelar();
        bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public Booking buscarEntidadePorId(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva", id));
    }
}
