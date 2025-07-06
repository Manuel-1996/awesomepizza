package com.awesomepizza.ordersystem.model;

/**
 * Stati possibili di un ordine nella pizzeria
 */
public enum OrderStatus {
    PENDING("In attesa di presa in carico"),
    IN_PROGRESS("In preparazione"), 
    READY("Pronto per il ritiro"),
    COMPLETED("Completato");
    
    private final String description;
    
    private OrderStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
