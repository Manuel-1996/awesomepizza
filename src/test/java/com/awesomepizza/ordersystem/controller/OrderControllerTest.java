package com.awesomepizza.ordersystem.controller;

import com.awesomepizza.ordersystem.dto.OrderResponse;
import com.awesomepizza.ordersystem.service.OrderService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@SpringBootTest
public class OrderControllerTest {

    private final String path = "/api/v1/orders";

    protected MockMvc mvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    private OrderService orderService;

    protected void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void getOrderQueueTest() {
        String uri = path + "/queue";

        List<OrderResponse> orders = orderService.getPendingOrders();
        if (orders != null && !orders.isEmpty()) {
            int status;
            int length;
            try {
                setUp();
                MvcResult mvcResult = mvc
                        .perform(MockMvcRequestBuilders
                                .get(uri)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn();

                MockHttpServletResponse response = mvcResult.getResponse();
                String content = response.getContentAsString();
                JSONArray jsonArray = new JSONArray(content);
                length = jsonArray.length();

                status = response.getStatus();
            } catch (Exception e) {
                status = 0;
                length = 0;
            }

            Assertions.assertEquals(200, status);
            Assertions.assertEquals(orders.size(), length);
        } else {
            int status;
            try {
                setUp();
                MvcResult mvcResult = mvc
                        .perform(MockMvcRequestBuilders
                                .get(uri)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn();

                status = mvcResult.getResponse().getStatus();
            } catch (Exception e) {
                status = 0;
            }

            Assertions.assertEquals(200, status);
        }
    }

    @Test
    public void getByOrderCodeTest() {
        List<OrderResponse> orders = orderService.getPendingOrders();

        if (orders != null && !orders.isEmpty()) {
            for (OrderResponse order : orders) {
                String orderCode = order.getOrderCode();

                int status;
                String customerName = "";
                try {
                    String URL = path;
                    if (orderCode != null) { URL += "/" + orderCode; }

                    setUp();
                    MvcResult mvcResult = mvc
                            .perform(MockMvcRequestBuilders
                                    .get(URL)
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                            .andReturn();

                    MockHttpServletResponse response = mvcResult.getResponse();
                    String content = response.getContentAsString();
                    JSONObject object = new JSONObject(content);
                    if (object.has("customerName")) { customerName = String.valueOf(object.get("customerName")); }

                    status = response.getStatus();
                } catch (Exception e) {
                    status = 0;
                }

                Assertions.assertEquals(200, status);
                Assertions.assertEquals(customerName, order.getCustomerName());
            }
        }
    }

    @Test
    public void createOrderTest() {
        String uri = path;

        int status;
        String orderCode = "";
        try {
            String customerName = "Test Customer " + System.currentTimeMillis();
            String request = "{ \"customerName\": \"" + customerName + "\", \"customerPhone\": \"1234567890\", \"items\": [{\"pizzaId\": 1, \"quantity\": 2}] }";
            setUp();
            MvcResult mvcResult = mvc
                    .perform(MockMvcRequestBuilders
                            .post(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(request))
                    .andReturn();

            MockHttpServletResponse response = mvcResult.getResponse();
            String content = response.getContentAsString();
            JSONObject object = new JSONObject(content);
            if (object.has("orderCode")) { orderCode = String.valueOf(object.get("orderCode")); }

            status = response.getStatus();
        } catch (Exception e) {
            status = 0;
        }

        Assertions.assertEquals(201, status);
        Assertions.assertNotNull(orderCode);
        Assertions.assertFalse(orderCode.isEmpty());
    }

    @Test
    public void createOrderInvalidTest() {
        String uri = path;

        int status;
        try {
            String request = "{ \"customerName\": \"\", \"customerPhone\": \"\", \"items\": [] }";
            setUp();
            MvcResult mvcResult = mvc
                    .perform(MockMvcRequestBuilders
                            .post(uri)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(request))
                    .andReturn();

            status = mvcResult.getResponse().getStatus();
        } catch (Exception e) {
            status = 0;
        }

        Assertions.assertEquals(400, status);
    }

    @Test
    public void takeOrderTest() {
        List<OrderResponse> orders = orderService.getPendingOrders();

        if (orders != null && !orders.isEmpty()) {
            OrderResponse order = orders.get(0);
            String orderCode = order.getOrderCode();

            int status;
            String orderStatus = "";
            try {
                String URL = path + "/" + orderCode + "/take";

                setUp();
                MvcResult mvcResult = mvc
                        .perform(MockMvcRequestBuilders
                                .put(URL)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn();

                MockHttpServletResponse response = mvcResult.getResponse();
                String content = response.getContentAsString();
                JSONObject object = new JSONObject(content);
                if (object.has("status")) { orderStatus = String.valueOf(object.get("status")); }

                status = response.getStatus();
            } catch (Exception e) {
                status = 0;
            }

            Assertions.assertEquals(200, status);
            Assertions.assertEquals("IN_PROGRESS", orderStatus);
        }
    }

    @Test
    public void getByOrderCodeNullTest() {
        String orderCode = "INVALID_CODE";

        int status;
        try {
            setUp();
            MvcResult mvcResult = mvc
                    .perform(MockMvcRequestBuilders
                            .get(path + "/" + orderCode)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

            status = mvcResult.getResponse().getStatus();
        } catch (Exception e) {
            status = 0;
        }

        Assertions.assertEquals(404, status);
    }
}
