package com.awesomepizza.ordersystem.dto;

import com.awesomepizza.ordersystem.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO per la risposta con i dettagli dell'ordine
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    
    private Long id;
    private String orderCode;
    private String customerName;
    private String customerPhone;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime takenAt;
    private LocalDateTime completedAt;
    private List<OrderItemResponse> items;

}