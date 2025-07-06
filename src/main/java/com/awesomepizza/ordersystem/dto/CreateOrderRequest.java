package com.awesomepizza.ordersystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO per la creazione di un nuovo ordine
 */
@Getter
@Setter
@Schema(description = "Richiesta per la creazione di un nuovo ordine")
public class CreateOrderRequest {
    
    @Schema(description = "Nome del cliente", example = "Mario Rossi", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Il nome del cliente è obbligatorio")
    @Size(max = 100, message = "Il nome del cliente non può superare i 100 caratteri")
    private String customerName;
    
    @Schema(description = "Numero di telefono del cliente", example = "+39 123 456 7890")
    @Size(max = 20, message = "Il numero di telefono non può superare i 20 caratteri")
    private String customerPhone;
    
    @Schema(description = "Lista degli elementi dell'ordine", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "L'ordine deve contenere almeno un elemento")
    private List<OrderItemRequest> items;
}
