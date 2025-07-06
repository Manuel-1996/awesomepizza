package com.awesomepizza.ordersystem.service;

import com.awesomepizza.ordersystem.dto.CreateOrderRequest;
import com.awesomepizza.ordersystem.dto.OrderResponse;
import com.awesomepizza.ordersystem.exception.OrderException;
import com.awesomepizza.ordersystem.exception.PizzaException;

import java.util.List;
import java.util.Optional;

/**
 * Interfaccia per il servizio di gestione degli ordini
 */
public interface OrderService {

    /**
     * Crea un nuovo ordine
     */
    OrderResponse createOrder(CreateOrderRequest request) throws OrderException, PizzaException;

    /**
     * Ottiene un ordine dal codice
     */
    Optional<OrderResponse> getOrderByCode(String orderCode);

    /**
     * Ottiene tutti gli ordini in attesa (coda del pizzaiolo)
     */
    List<OrderResponse> getPendingOrders();

    /**
     * Ottiene tutti gli ordini attivi
     */
    List<OrderResponse> getActiveOrders();

    /**
     * Ottiene tutti gli ordini in progress
     */
    List<OrderResponse> getInProgressOrders();

    //Prende in carico un ordine (pizzaiolo) - THREAD-SAFE
    Optional<OrderResponse> takeOrder(String orderCode) throws OrderException;

    /**
     * Segna un ordine come pronto
     *
     * @throws PizzaException se l'ordine non è in markOrderAsReady
     */
    Optional<OrderResponse> markOrderAsReady(String orderCode);

    /**
     * Completa un ordine
     *
     * @throws PizzaException se l'ordine non è pronto
     */
    Optional<OrderResponse> completeOrder(String orderCode);
}
