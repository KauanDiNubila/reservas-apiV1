package com.example.booking_service.entity;

import com.example.booking_service.exception.RegraDeNegocioException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter @Setter @NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Sala é obrigatória")
    @Column(nullable = false)
    private Long roomId;

    @NotNull(message = "Usuário é obrigatório")
    @Column(nullable = false)
    private Long userId;

    @NotNull(message = "Início é obrigatório")
    @Column(nullable = false)
    private LocalDateTime inicio;

    @NotNull(message = "Fim é obrigatório")
    @Column(nullable = false)
    private LocalDateTime fim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusBooking status = StatusBooking.ATIVA;

    public void cancelar() {
        if (this.status == StatusBooking.CANCELADA) {
            throw new RegraDeNegocioException("Esta reserva já está cancelada.");
        }
        this.status = StatusBooking.CANCELADA;
    }

    public void validarDatas() {
        if (this.inicio == null || this.fim == null) {
            throw new RegraDeNegocioException("Datas de início e fim são obrigatórias.");
        }
        if (!this.inicio.isBefore(this.fim)) {
            throw new RegraDeNegocioException(
                    "A data de início deve ser anterior à data de fim."
            );
        }
        if (this.inicio.isBefore(LocalDateTime.now())) {
            throw new RegraDeNegocioException(
                    "Não é possível criar uma reserva com data de início no passado."
            );
        }
    }
}
