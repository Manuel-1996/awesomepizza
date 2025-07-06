package com.awesomepizza.ordersystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entità che rappresenta un elemento di un ordine (pizza e quantità)
 *
 *
 * Usa LAZY per default (migliori performance)
 * Usa EAGER solo se sai che userai sempre quella relazione
 * Per Pizza ha senso EAGER perché probabilmente mostri sempre nome/prezzo
 * Per Order ha senso LAZY perché potresti non aver sempre bisogno dei dettagli dell'ordine
 *
 *
 */
@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pizza_id", nullable = false)
    private Pizza pizza;
    
    @NotNull(message = "La quantità è obbligatoria")
    @Min(value = 1, message = "La quantità deve essere almeno 1")
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(length = 200)
    private String notes;

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", pizza=" + (pizza != null ? pizza.getName() : "null") +
                ", quantity=" + quantity +
                ", notes='" + notes + '\'' +
                '}';
    }
}
