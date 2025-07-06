package com.awesomepizza.ordersystem.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Response DTO per gestire gli errori in modo standardizzato
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Risposta di errore standardizzata")
public class ErrorResponse {
    
    @Schema(description = "Timestamp dell'errore", example = "2025-07-05T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    @Schema(description = "Codice di status HTTP", example = "400")
    private int status;
    
    @Schema(description = "Descrizione dell'errore HTTP", example = "Bad Request")
    private String error;
    
    @Schema(description = "Messaggio di errore dettagliato", example = "Pizza con ID 1 non trovata")
    private String message;
    
    @Schema(description = "Path dell'endpoint che ha generato l'errore", example = "/api/v1/pizzas/1")
    private String path;
    
    @Schema(description = "Codice di errore applicativo", example = "PIZZA_NOT_FOUND")
    private String errorCode;

}