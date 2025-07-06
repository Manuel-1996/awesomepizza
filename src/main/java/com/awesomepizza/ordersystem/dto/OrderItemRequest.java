package com.awesomepizza.ordersystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO per un elemento dell'ordine
 */
@Schema(description = "Elemento di un ordine (pizza e quantità)")
@Getter
@Setter
public class OrderItemRequest {
    
    @Schema(description = "ID della pizza", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "L'ID della pizza è obbligatorio")
    private Long pizzaId;
    
    @Schema(description = "Quantità della pizza", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La quantità è obbligatoria")
    @Min(value = 1, message = "La quantità deve essere almeno 1")
    private Integer quantity;
    
    @Schema(description = "Note aggiuntive per la pizza", example = "Senza cipolle")
    @Size(max = 200, message = "Le note non possono superare i 200 caratteri")
    private String notes;
}
