package com.awesomepizza.ordersystem.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

/**
 * Entità che rappresenta una pizza nel menu
 */
@Entity
@Table(name = "pizzas")
@Schema(description = "Pizza del menu")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Pizza {
    
    @Schema(description = "ID univoco della pizza", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Schema(description = "Nome della pizza", example = "Margherita")
    @NotBlank(message = "Il nome della pizza è obbligatorio")
    @Column(nullable = false, unique = true)
    private String name;
    
    @Schema(description = "Descrizione della pizza", example = "Pomodoro, mozzarella, basilico")
    @Column(length = 500)
    private String description;
    
    @Schema(description = "Prezzo della pizza", example = "8.50")
    @NotNull(message = "Il prezzo è obbligatorio")
    @Positive(message = "Il prezzo deve essere positivo")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Boolean available = true;
    
    @Override
    public String toString() {
        return "Pizza{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", available=" + available +
                '}';
    }
}
