package com.awesomepizza.ordersystem.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Enum che definisce tutti i possibili codici di errore relativi alle pizze
 */
@Getter
public enum PizzaErrorCode {
    
    // Errori relativi alle pizze
    PIZZA_NOT_FOUND("Pizza con ID %s non trovata", HttpStatus.NOT_FOUND),
    PIZZA_NOT_AVAILABLE("Pizza '%s' non è disponibile", HttpStatus.BAD_REQUEST),
    PIZZA_ALREADY_EXISTS("Pizza con nome '%s' già esistente", HttpStatus.CONFLICT),

    // Nuovi codici di errore per validazione più dettagliata
    PIZZA_NAME_EMPTY("Il nome della pizza non può essere vuoto", HttpStatus.BAD_REQUEST),
    PIZZA_NAME_TOO_LONG("Il nome della pizza non può superare i 100 caratteri", HttpStatus.BAD_REQUEST),
    PIZZA_PRICE_NULL("Il prezzo della pizza è obbligatorio", HttpStatus.BAD_REQUEST),
    PIZZA_PRICE_NEGATIVE("Il prezzo della pizza deve essere positivo", HttpStatus.BAD_REQUEST),
    PIZZA_DESCRIPTION_TOO_LONG("La descrizione della pizza non può superare i 500 caratteri", HttpStatus.BAD_REQUEST),
    INVALID_PIZZA_ID("ID pizza non valido: %s", HttpStatus.BAD_REQUEST);

    private final String descrizione;
    private final HttpStatus httpStatus;
    
    PizzaErrorCode(String descrizione, HttpStatus httpStatus) {
        this.descrizione = descrizione;
        this.httpStatus = httpStatus;
    }
}
