package com.awesomepizza.ordersystem.controller;

import com.awesomepizza.ordersystem.model.Pizza;
import com.awesomepizza.ordersystem.service.PizzaService;
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
public class PizzaControllerTest {

    private final String path = "/api/v1/pizzas";

    protected MockMvc mvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    private PizzaService pizzaService;

    protected void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void getAllAvailablePizzasTest() {
        String uri = path;

        List<Pizza> pizzas = pizzaService.getAvailablePizzas();
        if (pizzas != null && !pizzas.isEmpty()) {
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
            Assertions.assertEquals(pizzas.size(), length);
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
    public void getAllPizzasTest() {
        String uri = path + "/all";

        List<Pizza> pizzas = pizzaService.getAllPizzas();
        if (pizzas != null && !pizzas.isEmpty()) {
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
            Assertions.assertEquals(pizzas.size(), length);
        }
    }

    @Test
    public void getByIdTest() {
        List<Pizza> pizzas = pizzaService.getAllPizzas();

        if (pizzas != null && !pizzas.isEmpty()) {
            for (Pizza pizza : pizzas) {
                Long id = pizza.getId();

                int status;
                String pizzaName = "";
                String pizzaDescription = "";
                try {
                    String URL = path;
                    if (id != null) { URL += "/" + id; }

                    setUp();
                    MvcResult mvcResult = mvc
                            .perform(MockMvcRequestBuilders
                                    .get(URL)
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                            .andReturn();

                    MockHttpServletResponse response = mvcResult.getResponse();
                    String content = response.getContentAsString();
                    JSONObject object = new JSONObject(content);
                    if (object.has("name")) { pizzaName = String.valueOf(object.get("name")); }
                    if (object.has("description")) { pizzaDescription = String.valueOf(object.get("description")); }

                    status = response.getStatus();
                } catch (Exception e) {
                    status = 0;
                }

                Assertions.assertEquals(200, status);
                Assertions.assertEquals(pizzaName, pizza.getName());
                Assertions.assertEquals(pizzaDescription, pizza.getDescription());
            }
        }
    }

    @Test
    public void getByIdNullTest() {
        Long id = 9999L;

        int status;
        try {
            setUp();
            MvcResult mvcResult = mvc
                    .perform(MockMvcRequestBuilders
                            .get(path + "/" + id)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

            status = mvcResult.getResponse().getStatus();
        } catch (Exception e) {
            status = 0;
        }

        Assertions.assertEquals(404, status);
    }

    @Test
    public void createPizzaTest() {
        String uri = path;

        int status;
        String pizzaName = "";
        String expectedPizzaName = "Test Pizza " + System.currentTimeMillis();
        try {
            String request = "{ \"name\": \"" + expectedPizzaName + "\", \"description\": \"Pizza di test\", \"price\": 15.50, \"ingredients\": [\"Pomodoro\", \"Mozzarella\"] }";
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
            if (object.has("name")) { pizzaName = String.valueOf(object.get("name")); }

            status = response.getStatus();
        } catch (Exception e) {
            status = 0;
        }

        Assertions.assertEquals(201, status);
        Assertions.assertEquals(expectedPizzaName, pizzaName);
    }

    @Test
    public void createPizzaInvalidTest() {
        String uri = path;

        int status;
        try {
            String request = "{ \"name\": \"\", \"description\": \"\", \"price\": -1 }";
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
}
