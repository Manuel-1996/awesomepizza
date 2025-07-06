package com.awesomepizza.ordersystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configurazione per Swagger/OpenAPI
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Awesome Pizza - Order Management API")
                        .description("Sistema di gestione ordini per la pizzeria Awesome Pizza. " +
                                   "API REST per creare e gestire ordini di pizze, monitorare lo stato " +
                                   "degli ordini e gestire la coda del pizzaiolo.")
                        .version("v1.0.0")
                );
    }
}
