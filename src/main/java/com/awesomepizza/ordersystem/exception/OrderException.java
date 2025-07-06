package com.awesomepizza.ordersystem.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * Eccezione personalizzata per errori specifici degli ordini
 */
@Getter
@Setter
public class OrderException extends RuntimeException {

    private final OrderErrorCode orderErrorCode;
    private final String message;

    public OrderException(OrderErrorCode orderErrorCode) {
        this.orderErrorCode = orderErrorCode;
        this.message = orderErrorCode.getDescrizione();
    }

    public OrderException(OrderErrorCode orderErrorCode, String... strings) {
        this.orderErrorCode = orderErrorCode;
        this.message = String.format(orderErrorCode.getDescrizione(), strings);
    }

}
