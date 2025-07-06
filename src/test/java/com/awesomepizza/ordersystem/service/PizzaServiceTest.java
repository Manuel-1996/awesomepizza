package com.awesomepizza.ordersystem.service;

import com.awesomepizza.ordersystem.model.Pizza;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class PizzaServiceTest {

    @Autowired
    private PizzaService pizzaService;

    @Test
    public void getAllPizzasTest() {
        List<Pizza> pizzas = pizzaService.getAllPizzas();
        Assertions.assertNotNull(pizzas);
    }

    @Test
    public void getAvailablePizzasTest() {
        List<Pizza> availablePizzas = pizzaService.getAvailablePizzas();
        Assertions.assertNotNull(availablePizzas);
        
        for (Pizza pizza : availablePizzas) {
            Assertions.assertTrue(pizza.getAvailable());
        }
    }

    @Test
    public void getPizzaByIdTest() {
        List<Pizza> pizzas = pizzaService.getAllPizzas();
        if (pizzas != null && !pizzas.isEmpty()) {
            Pizza pizza = pizzas.get(0);
            Optional<Pizza> foundPizza = pizzaService.getPizzaById(pizza.getId());
            Assertions.assertTrue(foundPizza.isPresent());
            Assertions.assertEquals(pizza.getId(), foundPizza.get().getId());
            Assertions.assertEquals(pizza.getName(), foundPizza.get().getName());
        }
    }

    @Test
    public void getPizzaByIdNotFoundTest() {
        Optional<Pizza> foundPizza = pizzaService.getPizzaById(9999L);
        Assertions.assertFalse(foundPizza.isPresent());
    }

    @Test
    public void getPizzaByNameTest() {
        List<Pizza> pizzas = pizzaService.getAllPizzas();
        if (pizzas != null && !pizzas.isEmpty()) {
            Pizza pizza = pizzas.get(0);
            Optional<Pizza> foundPizza = pizzaService.getPizzaByName(pizza.getName());
            Assertions.assertTrue(foundPizza.isPresent());
            Assertions.assertEquals(pizza.getName(), foundPizza.get().getName());
        }
    }

    @Test
    public void existsByNameTest() {
        List<Pizza> pizzas = pizzaService.getAllPizzas();
        if (pizzas != null && !pizzas.isEmpty()) {
            Pizza pizza = pizzas.get(0);
            boolean exists = pizzaService.existsByName(pizza.getName());
            Assertions.assertTrue(exists);
        }
        
        boolean notExists = pizzaService.existsByName("Pizza_Inesistente_Test");
        Assertions.assertFalse(notExists);
    }

    @Test
    public void countAvailablePizzasTest() {
        long count = pizzaService.countAvailablePizzas();
        Assertions.assertTrue(count >= 0);
        
        List<Pizza> availablePizzas = pizzaService.getAvailablePizzas();
        Assertions.assertEquals(availablePizzas.size(), count);
    }
}
