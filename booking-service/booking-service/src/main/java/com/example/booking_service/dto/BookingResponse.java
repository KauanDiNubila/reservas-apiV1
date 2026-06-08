package com.example.booking_service.dto;

import com.example.booking_service.entity.Booking;
import com.example.booking_service.entity.StatusBooking;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class BookingResponse {

    private Long id;
    private Long roomId;
    private Long userId;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private StatusBooking status;

    public static BookingResponse de(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setRoomId(booking.getRoomId());
        response.setUserId(booking.getUserId());
        response.setInicio(booking.getInicio());
        response.setFim(booking.getFim());
        response.setStatus(booking.getStatus());
        return response;
    }
}
