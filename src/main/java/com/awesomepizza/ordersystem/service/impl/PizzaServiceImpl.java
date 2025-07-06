package com.awesomepizza.ordersystem.service.impl;

import com.awesomepizza.ordersystem.model.Pizza;
import com.awesomepizza.ordersystem.repository.PizzaRepository;
import com.awesomepizza.ordersystem.service.PizzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementazione del servizio per la gestione delle pizze
 */
@Service
public class PizzaServiceImpl implements PizzaService {
    
    @Autowired
    private PizzaRepository pizzaRepository;
    
    @Override
    public List<Pizza> getAvailablePizzas() {
        return pizzaRepository.findByAvailableTrue();
    }
    
    @Override
    public List<Pizza> getAllPizzas() {
        return pizzaRepository.findAll();
    }
    
    @Override
    public Optional<Pizza> getPizzaById(Long id) {
        return pizzaRepository.findById(id);
    }
    
    @Override
    public Optional<Pizza> getPizzaByName(String name) {
        return pizzaRepository.findByName(name);
    }
    
    @Override
    public Pizza savePizza(Pizza pizza) {
        return pizzaRepository.save(pizza);
    }
    
    @Override
    public Optional<Pizza> updatePizzaAvailability(Long id, boolean available) {
        Optional<Pizza> pizzaOpt = pizzaRepository.findById(id);
        if (pizzaOpt.isPresent()) {
            Pizza pizza = pizzaOpt.get();
            pizza.setAvailable(available);
            return Optional.of(pizzaRepository.save(pizza));
        }
        return Optional.empty();
    }
    
    @Override
    public boolean existsByName(String name) {
        return pizzaRepository.existsByName(name);
    }
    
    @Override
    public long countAvailablePizzas() {
        return pizzaRepository.countAvailablePizzas();
    }
}
