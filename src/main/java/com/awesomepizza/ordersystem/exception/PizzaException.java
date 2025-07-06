package com.awesomepizza.ordersystem.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * Eccezione personalizzata per errori specifici delle pizze
 */
@Getter
@Setter
public class PizzaException extends RuntimeException {

    private final PizzaErrorCode pizzaErrorCode;
    private final String message;

    public PizzaException(PizzaErrorCode pizzaErrorCode) {
        this.pizzaErrorCode = pizzaErrorCode;
        this.message = pizzaErrorCode.getDescrizione();
    }

    public PizzaException(PizzaErrorCode pizzaErrorCode, String... strings) {
        this.pizzaErrorCode = pizzaErrorCode;
        this.message = String.format(pizzaErrorCode.getDescrizione(), strings);
    }

    public PizzaException(PizzaErrorCode pizzaErrorCode, Exception cause, String... strings) {
        this.pizzaErrorCode = pizzaErrorCode;
        this.message = String.format(pizzaErrorCode.getDescrizione(), strings);
        initCause(cause);
    }
}
