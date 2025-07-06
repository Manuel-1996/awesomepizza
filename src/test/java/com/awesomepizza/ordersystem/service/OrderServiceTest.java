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
    public void readyOrderWorkflowTest() {
        List<OrderResponse> orders = orderService.getInProgressOrders();
        if (orders != null && !orders.isEmpty()) {
            OrderResponse order = orders.get(0);
            String orderCode = order.getOrderCode();

            // Step 1: Ready order (IN_PROGRESS → READY)
            Optional<OrderResponse> takenOrder = orderService.markOrderAsReady(orderCode);
            Assertions.assertTrue(takenOrder.isPresent());
            Assertions.assertEquals("READY", takenOrder.get().getStatus().toString());

            // Step 2: Complete order (READY → COMPLETED)
            Optional<OrderResponse> completedOrder = orderService.completeOrder(orderCode);
            Assertions.assertTrue(completedOrder.isPresent());
            Assertions.assertEquals("COMPLETED", completedOrder.get().getStatus().toString());

            List<OrderResponse> activeOrders = orderService.getActiveOrders();
            boolean isStillActive = activeOrders.stream()
                    .anyMatch(o -> o.getOrderCode().equals(orderCode));
            Assertions.assertFalse(isStillActive, "L'ordine completato non dovrebbe essere più attivo");

        }
    }
}
