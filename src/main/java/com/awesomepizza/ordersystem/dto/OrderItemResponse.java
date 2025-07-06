package com.awesomepizza.ordersystem.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * DTO per la risposta con i dettagli di un elemento dell'ordine
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrderItemResponse {
    
    private Long id;
    private Long pizzaId;
    private String pizzaName;
    private String pizzaDescription;
    private BigDecimal pizzaPrice;
    private Integer quantity;
    private String notes;

}
