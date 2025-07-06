package com.awesomepizza.ordersystem.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Enum che definisce i codici di errore specifici per gli ordini
 */
@Getter
public enum OrderErrorCode {
    
    // Errori relativi agli ordini
    ORDER_NOT_FOUND("Ordine con codice '%s' non trovato", HttpStatus.NOT_FOUND),
    ORDER_ALREADY_TAKEN("Ordine '%s' già preso in carico", HttpStatus.CONFLICT),
    ORDER_INVALID_STATUS("Stato ordine non valido per questa operazione. Stato attuale: %s", HttpStatus.CONFLICT),
    ORDER_EMPTY("Impossibile creare un ordine senza pizze", HttpStatus.BAD_REQUEST),
    ORDER_DUPLICATE("Ordine duplicato: un ordine simile è stato creato di recente", HttpStatus.CONFLICT),
    INVALID_QUANTITY("Quantità non valida: %s. Deve essere maggiore di 0", HttpStatus.BAD_REQUEST);

    private final String descrizione;
    private final HttpStatus httpStatus;
    
    OrderErrorCode(String descrizione, HttpStatus httpStatus) {
        this.descrizione = descrizione;
        this.httpStatus = httpStatus;
    }
}
