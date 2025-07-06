package com.awesomepizza.ordersystem.service;

import com.awesomepizza.ordersystem.dto.OrderResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void getOrderByCodeTest() {
        List<OrderResponse> orders = orderService.getPendingOrders();
        if (orders != null && !orders.isEmpty()) {
            OrderResponse order = orders.get(0);
            Optional<OrderResponse> foundOrder = orderService.getOrderByCode(order.getOrderCode());
            Assertions.assertTrue(foundOrder.isPresent());
            Assertions.assertEquals(order.getOrderCode(), foundOrder.get().getOrderCode());
        }
    }

    @Test
    public void getOrderByCodeNotFoundTest() {
        Optional<OrderResponse> foundOrder = orderService.getOrderByCode("INVALID_CODE");
        Assertions.assertFalse(foundOrder.isPresent());
    }

    @Test
    public void takeOrderTest() {
        List<OrderResponse> orders = orderService.getPendingOrders();
        if (orders != null && !orders.isEmpty()) {
            OrderResponse order = orders.get(0);
            Optional<OrderResponse> takenOrder = orderService.takeOrder(order.getOrderCode());
            
            if (takenOrder.isPresent()) {
                Assertions.assertEquals("IN_PROGRESS", takenOrder.get().getStatus().toString());
            }
        }
    }

    @Test
    public void takeOrderConflictTest() {
        List<OrderResponse> orders = orderService.getPendingOrders();
        if (orders != null && !orders.isEmpty()) {
            OrderResponse order = orders.get(0);
            String orderCode = order.getOrderCode();

            // Primo takeOrder - dovrebbe andare bene
            Optional<OrderResponse> firstTake = orderService.takeOrder(orderCode);
            Assertions.assertTrue(firstTake.isPresent());
            Assertions.assertEquals("IN_PROGRESS", firstTake.get().getStatus().toString());

            // Secondo takeOrder sullo stesso ordine - dovrebbe generare conflitto
            try {
                orderService.takeOrder(orderCode);
                Assertions.fail("Doveva lanciare un'eccezione per ordine già preso");
            } catch (Exception ex) {
                // Verifica che sia stata lanciata l'eccezione corretta
                Assertions.assertTrue(ex.getMessage().contains("non è in attesa") ||
                        ex.getMessage().contains("INVALID_STATUS") ||
                        ex.getMessage().contains("IN_PROGRESS"));
            }
        }
    }

    @Test
    public void readyOrderWorkflowTest() {
        List<OrderResponse> orders = orderService.getPendingOrders();
        if (orders != null && !orders.isEmpty()) {
            OrderResponse order = orders.get(0);
            String orderCode = order.getOrderCode();

            // Step 1: Take order (PENDING → IN_PROGRESS)
            Optional<OrderResponse> takenOrder = orderService.takeOrder(orderCode);
            Assertions.assertTrue(takenOrder.isPresent());
            Assertions.assertEquals("IN_PROGRESS", takenOrder.get().getStatus().toString());

            // Step 2: Mark as ready (IN_PROGRESS → READY)
            Optional<OrderResponse> readyOrder = orderService.markOrderAsReady(orderCode);
            Assertions.assertTrue(readyOrder.isPresent());
            Assertions.assertEquals("READY", readyOrder.get().getStatus().toString());

        }
    }


    @Test
    public void completeOrderWorkflowTest() {
        List<OrderResponse> orders = orderService.getPendingOrders();
        if (orders != null && !orders.isEmpty()) {
            OrderResponse order = orders.get(0);
            String orderCode = order.getOrderCode();

            // Step 3: Complete order (READY → COMPLETED)
            Optional<OrderResponse> completedOrder = orderService.completeOrder(orderCode);
            Assertions.assertTrue(completedOrder.isPresent());
            Assertions.assertEquals("COMPLETED", completedOrder.get().getStatus().toString());

            // Verifica che l'ordine non sia più negli ordini attivi
            List<OrderResponse> activeOrders = orderService.getActiveOrders();
            boolean isStillActive = activeOrders.stream()
                    .anyMatch(o -> o.getOrderCode().equals(orderCode));
            Assertions.assertFalse(isStillActive, "L'ordine completato non dovrebbe essere più attivo");
        }
    }
}
