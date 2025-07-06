package com.awesomepizza.ordersystem.config;

import com.awesomepizza.ordersystem.model.Pizza;
import com.awesomepizza.ordersystem.service.PizzaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Inizializza alcuni dati di esempio nel database
 * Ora lo schema viene creato da schema.sql PRIMA di Hibernate
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final PizzaService pizzaService;
    
    @Override
    public void run(String... args) throws Exception {
        try {
            log.info("Inizializzazione dati in corso...");
            
            // Verifica se ci sono già pizze nel database
            if (pizzaService.countAvailablePizzas() == 0) {
                initializeSamplePizzas();
                log.info("Inizializzazione dati completata con successo");
            } else {
                log.info("Dati già presenti nel database, inizializzazione saltata");
            }
        } catch (Exception e) {
            log.error("Errore durante l'inizializzazione dei dati: {}", e.getMessage());
            log.warn("L'applicazione continuerà senza i dati di esempio");
        }
    }
    
    private void initializeSamplePizzas() {
        log.info("Inizializzazione delle pizze di esempio...");
        
        Pizza margherita = new Pizza();
        margherita.setName("Margherita");
        margherita.setDescription("Pomodoro, mozzarella, basilico");
        margherita.setPrice(new BigDecimal("8.50"));
        pizzaService.savePizza(margherita);
        
        Pizza marinara = new Pizza();
        marinara.setName("Marinara");
        marinara.setDescription("Pomodoro, aglio, origano, olio d'oliva");
        marinara.setPrice(new BigDecimal("7.00"));
        pizzaService.savePizza(marinara);
        
        Pizza diavola = new Pizza();
        diavola.setName("Diavola");
        diavola.setDescription("Pomodoro, mozzarella, salame piccante");
        diavola.setPrice(new BigDecimal("10.00"));
        pizzaService.savePizza(diavola);
        
        Pizza quattroStagioni = new Pizza();
        quattroStagioni.setName("Quattro Stagioni");
        quattroStagioni.setDescription("Pomodoro, mozzarella, prosciutto, funghi, carciofi, olive");
        quattroStagioni.setPrice(new BigDecimal("12.00"));
        pizzaService.savePizza(quattroStagioni);
        
        Pizza capricciosa = new Pizza();
        capricciosa.setName("Capricciosa");
        capricciosa.setDescription("Pomodoro, mozzarella, prosciutto, funghi, olive");
        capricciosa.setPrice(new BigDecimal("13.00"));
        pizzaService.savePizza(capricciosa);
        
        Pizza quattroFormaggi = new Pizza();
        quattroFormaggi.setName("Quattro Formaggi");
        quattroFormaggi.setDescription("Mozzarella, gorgonzola, parmigiano, fontina");
        quattroFormaggi.setPrice(new BigDecimal("11.50"));
        pizzaService.savePizza(quattroFormaggi);
        
        log.info("Pizze di esempio inizializzate con successo nel database!");
    }
}
