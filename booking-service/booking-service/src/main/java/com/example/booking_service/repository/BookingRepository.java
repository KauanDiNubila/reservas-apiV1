package com.example.booking_service.repository;

import com.example.booking_service.entity.Booking;
import com.example.booking_service.entity.StatusBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
        SELECT b FROM Booking b
        WHERE b.roomId = :roomId
          AND b.status = :status
          AND b.id <> :bookingId
          AND b.inicio < :novoFim
          AND b.fim > :novoInicio
    """)
    List<Booking> buscarConflitos(
            @Param("roomId")     Long roomId,
            @Param("novoInicio") LocalDateTime novoInicio,
            @Param("novoFim")    LocalDateTime novoFim,
            @Param("status") StatusBooking status,
            @Param("bookingId")  Long bookingId
    );
}
