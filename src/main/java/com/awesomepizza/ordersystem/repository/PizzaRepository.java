package com.awesomepizza.ordersystem.repository;

import com.awesomepizza.ordersystem.model.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository per la gestione delle pizze
 */
@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Long> {
    
    /**
     * Trova tutte le pizze disponibili
     */
    List<Pizza> findByAvailableTrue();
    
    /**
     * Trova una pizza per nome
     */
    Optional<Pizza> findByName(String name);
    
    /**
     * Verifica se esiste una pizza con il nome specificato
     */
    boolean existsByName(String name);
    
    /**
     * Conta le pizze disponibili
     */
    @Query("SELECT COUNT(p) FROM Pizza p WHERE p.available = true")
    long countAvailablePizzas();
}
