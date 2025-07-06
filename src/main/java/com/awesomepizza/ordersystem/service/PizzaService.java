package com.awesomepizza.ordersystem.service;

import com.awesomepizza.ordersystem.model.Pizza;

import java.util.List;
import java.util.Optional;

/**
 * Interfaccia per il servizio di gestione delle pizze
 */
public interface PizzaService {
    
    /**
     * Ottiene tutte le pizze disponibili
     */
    List<Pizza> getAvailablePizzas();
    
    /**
     * Ottiene tutte le pizze
     */
    List<Pizza> getAllPizzas();
    
    /**
     * Ottiene una pizza per ID
     */
    Optional<Pizza> getPizzaById(Long id);
    
    /**
     * Ottiene una pizza per nome
     */
    Optional<Pizza> getPizzaByName(String name);
    
    /**
     * Salva una nuova pizza
     */
    Pizza savePizza(Pizza pizza);
    
    /**
     * Aggiorna la disponibilità di una pizza
     */
    Optional<Pizza> updatePizzaAvailability(Long id, boolean available);

    /**
     * Verifica se esiste una pizza con il nome specificato
     */
    boolean existsByName(String name);
    
    /**
     * Conta le pizze disponibili
     */
    long countAvailablePizzas();
}
