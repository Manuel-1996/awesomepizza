package com.awesomepizza.ordersystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entità che rappresenta un ordine di pizze
 */
@Entity
@Table(name = "orders")
@AllArgsConstructor
@Getter
@Setter
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderCode;
    
    @NotBlank(message = "Il nome del cliente è obbligatorio")
    @Column(nullable = false)
    private String customerName;
    
    @Column
    private String customerPhone;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    
    @NotNull
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime takenAt;
    
    @Column
    private LocalDateTime completedAt;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

    // Costruttore per nuovi ordini
    public Order() {
        this.orderCode = generateOrderCode();
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.items = new ArrayList<>();
    }
    
    // Metodi di utilità
    private String generateOrderCode() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    public void takeOrder() {
        this.status = OrderStatus.IN_PROGRESS;
        this.takenAt = LocalDateTime.now();
    }
    
    public void markAsReady() {
        this.status = OrderStatus.READY;
    }
    
    public void complete() {
        this.status = OrderStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
    
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderCode='" + orderCode + '\'' +
                ", customerName='" + customerName + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
