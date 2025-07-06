package com.awesomepizza.ordersystem.controller;

import com.awesomepizza.ordersystem.exception.PizzaErrorCode;
import com.awesomepizza.ordersystem.exception.PizzaException;
import com.awesomepizza.ordersystem.model.Pizza;
import com.awesomepizza.ordersystem.service.PizzaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Controller REST per la gestione delle pizze
 */
@RestController
@RequestMapping("/api/v1/pizzas")
@Tag(name = "Pizzas", description = "API per la gestione del menu delle pizze")
public class PizzaController {


    @Autowired
    PizzaService pizzaService;

    /**
     * GET /api/v1/pizzas - Ottiene tutte le pizze disponibili
     */
    @GetMapping
    @Operation(
        summary = "Ottiene tutte le pizze disponibili", 
        description = "Restituisce la lista di tutte le pizze attualmente disponibili nel menu"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista delle pizze disponibili",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = Pizza.class)))
    })
    public ResponseEntity<List<Pizza>> getAllAvailablePizzas() {
        List<Pizza> pizzas = pizzaService.getAvailablePizzas();
        return ResponseEntity.ok(pizzas);
    }
    
    /**
     * GET /api/v1/pizzas/all - Ottiene tutte le pizze (disponibili e non)
     */
    @GetMapping("/all")
    public ResponseEntity<List<Pizza>> getAllPizzas() {
        List<Pizza> pizzas = pizzaService.getAllPizzas();
        return ResponseEntity.ok(pizzas);
    }
    
    /**
     * GET /api/v1/pizzas/{id} - Ottiene una pizza per ID
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Ottiene una pizza per ID", 
        description = "Restituisce i dettagli di una specifica pizza tramite il suo ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pizza trovata",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = Pizza.class))),
        @ApiResponse(responseCode = "404", description = "Pizza non trovata"),
        @ApiResponse(responseCode = "400", description = "ID non valido")
    })
    public ResponseEntity<Pizza> getPizzaById(
            @Parameter(description = "ID della pizza da cercare", required = true)
            @PathVariable Long id) {
        
        // Validazione dell'ID
        if (id == null || id <= 0) {
            throw new PizzaException(PizzaErrorCode.INVALID_PIZZA_ID, String.valueOf(id));
        }
        
        Optional<Pizza> pizza = pizzaService.getPizzaById(id);
        if (pizza.isEmpty()) {
            throw new PizzaException(PizzaErrorCode.PIZZA_NOT_FOUND, String.valueOf(id));
        }
        
        return ResponseEntity.ok(pizza.get());
    }
    
    /**
     * POST /api/v1/pizzas - Crea una nuova pizza
     */
    @PostMapping
    @Operation(
        summary = "Crea una nuova pizza", 
        description = "Aggiunge una nuova pizza al menu"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pizza creata con successo"),
        @ApiResponse(responseCode = "400", description = "Dati pizza non validi"),
        @ApiResponse(responseCode = "409", description = "Pizza già esistente")
    })
    public ResponseEntity<Pizza> createPizza(@Valid @RequestBody Pizza pizza) {
        // Validazione aggiuntiva dei campi
        validatePizzaData(pizza);
        
        if (pizzaService.existsByName(pizza.getName())) {
            throw new PizzaException(PizzaErrorCode.PIZZA_ALREADY_EXISTS, pizza.getName());
        }
        
        Pizza savedPizza = pizzaService.savePizza(pizza);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPizza);
    }
    
    /**
     * PUT /api/v1/pizzas/{id}/availability - Aggiorna la disponibilità di una pizza
     */
    @PutMapping("/{id}/availability")
    @Operation(
        summary = "Aggiorna disponibilità pizza", 
        description = "Modifica la disponibilità di una pizza esistente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilità aggiornata"),
        @ApiResponse(responseCode = "404", description = "Pizza non trovata"),
        @ApiResponse(responseCode = "400", description = "ID non valido")
    })
    public ResponseEntity<Pizza> updatePizzaAvailability(
            @Parameter(description = "ID della pizza", required = true)
            @PathVariable Long id, 
            @Parameter(description = "Nuova disponibilità", required = true)
            @RequestParam boolean available) {
        
        // Validazione dell'ID
        if (id == null || id <= 0) {
            throw new PizzaException(PizzaErrorCode.INVALID_PIZZA_ID, String.valueOf(id));
        }
        
        Optional<Pizza> updatedPizza = pizzaService.updatePizzaAvailability(id, available);
        if (updatedPizza.isEmpty()) {
            throw new PizzaException(PizzaErrorCode.PIZZA_NOT_FOUND, String.valueOf(id));
        }
        
        return ResponseEntity.ok(updatedPizza.get());
    }

    /**
     * Metodo privato per validare i dati della pizza
     */
    private void validatePizzaData(Pizza pizza) {
        // Validazione nome
        if (pizza.getName() == null || !StringUtils.hasText(pizza.getName().trim())) {
            throw new PizzaException(PizzaErrorCode.PIZZA_NAME_EMPTY);
        }

        if (pizza.getName().trim().length() > 100) {
            throw new PizzaException(PizzaErrorCode.PIZZA_NAME_TOO_LONG);
        }

        // Validazione prezzo
        if (pizza.getPrice() == null) {
            throw new PizzaException(PizzaErrorCode.PIZZA_PRICE_NULL);
        }

        if (pizza.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PizzaException(PizzaErrorCode.PIZZA_PRICE_NEGATIVE);
        }

        // Validazione descrizione (opzionale ma se presente deve rispettare i limiti)
        if (pizza.getDescription() != null && pizza.getDescription().length() > 500) {
            throw new PizzaException(PizzaErrorCode.PIZZA_DESCRIPTION_TOO_LONG);
        }

        // Normalizzazione dei dati
        pizza.setName(pizza.getName().trim());
        if (pizza.getDescription() != null) {
            pizza.setDescription(pizza.getDescription().trim());
        }

        if (pizza.getAvailable() == null) {
            pizza.setAvailable(true);
        }
    }
}
