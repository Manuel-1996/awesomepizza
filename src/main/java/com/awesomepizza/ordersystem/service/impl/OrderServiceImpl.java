package com.awesomepizza.ordersystem.service.impl;

import com.awesomepizza.ordersystem.dto.CreateOrderRequest;
import com.awesomepizza.ordersystem.dto.OrderItemRequest;
import com.awesomepizza.ordersystem.dto.OrderItemResponse;
import com.awesomepizza.ordersystem.dto.OrderResponse;
import com.awesomepizza.ordersystem.exception.OrderErrorCode;
import com.awesomepizza.ordersystem.exception.OrderException;
import com.awesomepizza.ordersystem.exception.PizzaErrorCode;
import com.awesomepizza.ordersystem.exception.PizzaException;
import com.awesomepizza.ordersystem.model.Order;
import com.awesomepizza.ordersystem.model.OrderItem;
import com.awesomepizza.ordersystem.model.OrderStatus;
import com.awesomepizza.ordersystem.model.Pizza;
import com.awesomepizza.ordersystem.repository.OrderRepository;
import com.awesomepizza.ordersystem.service.OrderService;
import com.awesomepizza.ordersystem.service.PizzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementazione del servizio per la gestione degli ordini
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PizzaService pizzaService;

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = new Order();

        // Validazione lista vuota
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new OrderException(OrderErrorCode.ORDER_EMPTY);
        }

        if (isDuplicateOrder(request)) {
            throw new OrderException(OrderErrorCode.ORDER_DUPLICATE);
        }

        // Aggiungi gli elementi all'ordine
        for (OrderItemRequest itemRequest : request.getItems()) {
            Optional<Pizza> pizzaOpt = pizzaService.getPizzaById(itemRequest.getPizzaId());
            if (pizzaOpt.isEmpty()) {
                throw new PizzaException(PizzaErrorCode.PIZZA_NOT_FOUND, itemRequest.getPizzaId().toString());
            }

            Pizza pizza = pizzaOpt.get();
            if (!pizza.getAvailable()) {
                throw new PizzaException(PizzaErrorCode.PIZZA_NOT_AVAILABLE, pizza.getName());
            }

            if (itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
                throw new OrderException(OrderErrorCode.INVALID_QUANTITY, 
                    itemRequest.getQuantity() != null ? itemRequest.getQuantity().toString() : "null");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setPizza(pizza);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setNotes(itemRequest.getNotes());
            order.addItem(orderItem);
        }
        // Imposto i dettagli del cliente
        order.setCustomerName(request.getCustomerName());
        order.setCustomerPhone(request.getCustomerPhone());
        Order savedOrder = orderRepository.save(order);
        return convertToResponse(savedOrder);
    }

    @Override
    public Optional<OrderResponse> getOrderByCode(String orderCode) {
        return orderRepository.findByOrderCode(orderCode)
                .map(this::convertToResponse);
    }

    @Override
    public List<OrderResponse> getPendingOrders() {
        return orderRepository.findByStatusOrderByCreatedAtAsc(OrderStatus.PENDING)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getActiveOrders() {
        return orderRepository.findActiveOrders()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getInProgressOrders() {
        return orderRepository.findInProgressOrders()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<OrderResponse> takeOrder(String orderCode) {
        // Usa lock pessimistico per evitare che due pizzaioli prendano lo stesso ordine
        Optional<Order> orderOpt = orderRepository.findByOrderCodeForUpdate(orderCode);

        if (orderOpt.isEmpty()) {
            return Optional.empty();
        }

        Order order = orderOpt.get();
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderException(OrderErrorCode.ORDER_ALREADY_TAKEN,
                "L'ordine " + orderCode + " non è in attesa ma è " + order.getStatus());
        }

        order.takeOrder();
        Order savedOrder = orderRepository.save(order);
        return Optional.of(convertToResponse(savedOrder));
    }

    @Override
    public Optional<OrderResponse> markOrderAsReady(String orderCode) {
        Optional<Order> orderOpt = orderRepository.findByOrderCode(orderCode);
        if (orderOpt.isEmpty()) {
            return Optional.empty();
        }

        Order order = orderOpt.get();
        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new OrderException(OrderErrorCode.ORDER_INVALID_STATUS, 
                "L'ordine " + orderCode + " non è in preparazione ma è " + order.getStatus());
        }

        order.markAsReady();
        Order savedOrder = orderRepository.save(order);
        return Optional.of(convertToResponse(savedOrder));
    }

    @Override
    public Optional<OrderResponse> completeOrder(String orderCode) {
        Optional<Order> orderOpt = orderRepository.findByOrderCode(orderCode);
        if (orderOpt.isEmpty()) {
            return Optional.empty();
        }

        Order order = orderOpt.get();
        if (order.getStatus() != OrderStatus.READY) {
            throw new OrderException(OrderErrorCode.ORDER_INVALID_STATUS, 
                "L'ordine " + orderCode + " non è pronto ma è " + order.getStatus());
        }

        order.complete();
        Order savedOrder = orderRepository.save(order);
        return Optional.of(convertToResponse(savedOrder));
    }

    /**
     * Verifica se è un ordine duplicato (stesso cliente, stessi prodotti, entro 5 minuti)
     */
    private boolean isDuplicateOrder(CreateOrderRequest request) {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        List<Order> recentOrders = orderRepository.findByCreatedAtAfterOrderByCreatedAtAsc(fiveMinutesAgo);

        return recentOrders.stream()
                .anyMatch(order -> order.getCustomerName().equals(request.getCustomerName()) &&
                        order.getCustomerPhone().equals(request.getCustomerPhone()) &&
                        hasSameItems(order, request));
    }

    /**
     * Verifica se due ordini hanno gli stessi elementi (pizze, quantità e note)
     */
    private boolean hasSameItems(Order order, CreateOrderRequest request) {
        // Primo controllo: stesso numero di elementi
        if (order.getItems().size() != request.getItems().size()) {
            return false;
        }

        // Controllo specifico: ogni elemento della richiesta deve avere un match esatto nell'ordine
        return request.getItems().stream()
                .allMatch(requestItem -> order.getItems().stream()
                        .anyMatch(orderItem ->
                                orderItem.getPizza().getId().equals(requestItem.getPizzaId()) &&
                                        orderItem.getQuantity().equals(requestItem.getQuantity()) &&
                                        areNotesEqual(orderItem.getNotes(), requestItem.getNotes())));
    }

    private boolean areNotesEqual(String notes1, String notes2) {
        if (notes1 == null && notes2 == null) {
            return true;
        }
        if (notes1 == null || notes2 == null) {
            return false;
        }
        return notes1.trim().equalsIgnoreCase(notes2.trim());
    }

    // Metodi di utilità
    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderCode(order.getOrderCode());
        response.setCustomerName(order.getCustomerName());
        response.setCustomerPhone(order.getCustomerPhone());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setTakenAt(order.getTakenAt());
        response.setCompletedAt(order.getCompletedAt());

        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::convertToItemResponse)
                .collect(Collectors.toList());
        response.setItems(itemResponses);

        return response;
    }

    private OrderItemResponse convertToItemResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setPizzaId(item.getPizza().getId());
        response.setPizzaName(item.getPizza().getName());
        response.setPizzaDescription(item.getPizza().getDescription());
        response.setPizzaPrice(item.getPizza().getPrice());
        response.setQuantity(item.getQuantity());
        response.setNotes(item.getNotes());
        return response;
    }
}
