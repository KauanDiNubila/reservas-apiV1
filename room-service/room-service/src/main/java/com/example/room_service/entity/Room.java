package com.example.room_service.entity;

import com.example.room_service.exception.RegraDeNegocioException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "rooms",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_rooms_nome",
                columnNames = "nome"
        )
)
@Getter
@Setter
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome da sala é obrigatório")
    @Column(nullable = false)
    private String nome;

    @Min(value = 1, message = "Capacidade mínima é 1")
    @Column(nullable = false)
    private Integer capacidade;

    private String localizacao;

    @Column(nullable = false)
    private boolean ativa = true;

    public void validarDisponibilidade() {
        if (!this.ativa) {
            throw new RegraDeNegocioException(
                    "A sala '" + this.nome + "' está inativa."
            );
        }
    }
}