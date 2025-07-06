package com.awesomepizza.ordersystem.controller;

import com.awesomepizza.ordersystem.dto.CreateOrderRequest;
import com.awesomepizza.ordersystem.dto.ErrorResponse;
import com.awesomepizza.ordersystem.dto.OrderResponse;
import com.awesomepizza.ordersystem.exception.OrderErrorCode;
import com.awesomepizza.ordersystem.exception.OrderException;
import com.awesomepizza.ordersystem.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller REST per la gestione degli ordini
 */
@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin(origins = "*")
@Tag(name = "Orders", description = "API per la gestione degli ordini di pizze")
public class OrderController {

    @Autowired
    OrderService orderService;
    /**
     * POST /api/v1/orders - Crea un nuovo ordine
     */
    @PostMapping
    @Operation(
            summary = "Crea un nuovo ordine",
            description = "Crea un nuovo ordine di pizze per un cliente. Restituisce il codice ordine per il tracciamento."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ordine creato con successo",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dati dell'ordine non validi")
    })
    public ResponseEntity<?> createOrder(
            @Parameter(description = "Dettagli dell'ordine da creare", required = true)
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    /**
     * GET /api/v1/orders/{orderCode} - Ottiene un ordine dal codice
     */
    @GetMapping("/{orderCode}")
    @Operation(
            summary = "Ottiene un ordine tramite codice",
            description = "Restituisce i dettagli di un ordine utilizzando il codice ordine fornito al cliente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ordine trovato",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "404", description = "Ordine non trovato")
    })
    public ResponseEntity<OrderResponse> getOrderByCode(
            @Parameter(description = "Codice dell'ordine da cercare", required = true)
            @PathVariable String orderCode) {
        Optional<OrderResponse> order = orderService.getOrderByCode(orderCode);
        return order.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/v1/orders/queue - Ottiene la coda degli ordini in attesa (per il pizzaiolo)
     */
    @GetMapping("/queue")
    public ResponseEntity<List<OrderResponse>> getOrderQueue() {
        List<OrderResponse> orders = orderService.getPendingOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * PUT /api/v1/orders/{orderCode}/take - Prende in carico un ordine (pizzaiolo)
     */
    @PutMapping("/{orderCode}/take")
    @Operation(
            summary = "Prende in carico un ordine",
            description = "Il pizzaiolo prende in carico un ordine in attesa"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ordine preso in carico con successo"),
            @ApiResponse(responseCode = "404", description = "Ordine non trovato"),
            @ApiResponse(responseCode = "409", description = "Ordine già preso in carico o stato non valido"),
            @ApiResponse(responseCode = "423", description = "Ordine bloccato da operazione concorrente")
    })
    public ResponseEntity<OrderResponse> takeOrder(@PathVariable String orderCode) {
        Optional<OrderResponse> order = orderService.takeOrder(orderCode);
        return order.map(ResponseEntity::ok)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND, orderCode));
    }

    /**
     * PUT /api/v1/orders/{orderCode}/ready - Segna un ordine come pronto
     */
    @PutMapping("/{orderCode}/ready")
    @Operation(
            summary = "Segna un ordine come pronto",
            description = "Il pizzaiolo segna un ordine come pronto per il ritiro"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ordine segnato come pronto"),
            @ApiResponse(responseCode = "404", description = "Ordine non trovato"),
            @ApiResponse(responseCode = "409", description = "Stato dell'ordine non valido"),
            @ApiResponse(responseCode = "423", description = "Ordine bloccato da operazione concorrente")
    })
    public ResponseEntity<OrderResponse> markOrderAsReady(@PathVariable String orderCode) {
        Optional<OrderResponse> order = orderService.markOrderAsReady(orderCode);
        return order.map(ResponseEntity::ok)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND, orderCode));
    }

    /**
     * PUT /api/v1/orders/{orderCode}/complete - Completa un ordine
     */
    @PutMapping("/{orderCode}/complete")
    @Operation(
            summary = "Completa un ordine",
            description = "Segna un ordine come completato quando viene ritirato dal cliente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ordine completato con successo"),
            @ApiResponse(responseCode = "404", description = "Ordine non trovato"),
            @ApiResponse(responseCode = "409", description = "Stato dell'ordine non valido"),
            @ApiResponse(responseCode = "423", description = "Ordine bloccato da operazione concorrente")
    })
    public ResponseEntity<OrderResponse> completeOrder(@PathVariable String orderCode) {
        Optional<OrderResponse> order = orderService.completeOrder(orderCode);
        return order.map(ResponseEntity::ok)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND, orderCode));
    }

}
