package com.awesomepizza.ordersystem.repository;

import com.awesomepizza.ordersystem.model.Order;
import com.awesomepizza.ordersystem.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository per la gestione degli ordini
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Trova un ordine dal codice ordine
     */
    Optional<Order> findByOrderCode(String orderCode);
    
    /**
     * Trova un ordine dal codice ordine con lock pessimistico per aggiornamenti di stato
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.orderCode = :orderCode")
    Optional<Order> findByOrderCodeForUpdate(String orderCode);

    /**
     * Trova tutti gli ordini in attesa ordinati per data di creazione
     */
    List<Order> findByStatusOrderByCreatedAtAsc(OrderStatus status);

    /**
     * Trova tutti gli ordini attivi (non completati)
     */
    @Query("SELECT o FROM Order o WHERE o.status != com.awesomepizza.ordersystem.model.OrderStatus.COMPLETED ORDER BY o.createdAt ASC")
    List<Order> findActiveOrders();

    /**
     * Trova ordini creati dopo una certa data
     */
    List<Order> findByCreatedAtAfterOrderByCreatedAtAsc(LocalDateTime dateTime);

    @Query("SELECT o FROM Order o WHERE o.status = com.awesomepizza.ordersystem.model.OrderStatus.IN_PROGRESS ORDER BY o.createdAt ASC")
    List<Order> findInProgressOrders();

}
