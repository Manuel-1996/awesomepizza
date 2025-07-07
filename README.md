# Awesome Pizza - Sistema di Gestione Ordini

## Panoramica

Sistema di gestione ordini per la pizzeria "Awesome Pizza" sviluppato con Spring Boot e Java 17. Il sistema permette ai clienti di ordinare pizze senza registrazione e ai pizzaioli di gestire la coda degli ordini in tempo reale.

## Indice

- [Funzionalità](#funzionalità)
- [Architettura](#architettura)
- [Progettazione Database](#progettazione-database)
- [Variabili d'Ambiente](#variabili-dambiente)
- [Panoramica API](#panoramica-api)
- [Docker & Deployment](#docker--deployment)
- [Sviluppo Locale](#sviluppo-locale)
- [Test](#test)
- [Struttura del Progetto](#struttura-del-progetto)

## Funzionalità

- Gestione menu pizze con disponibilità dinamica
- Creazione ordini senza registrazione utente
- Sistema di tracking ordini con codici univoci
- Gestione stati dell'ordine (PENDING → IN_PROGRESS → READY → COMPLETED)
- Coda ordini per pizzaioli con priorità FIFO
- API RESTful complete con documentazione Swagger
- Containerizzato per la produzione

## Architettura

- **Framework:** Spring Boot 3.5.3 (Java 17)
- **ORM:** Hibernate/JPA
- **Database:** PostgreSQL 15
- **Build Tool:** Maven 3.9
- **Testing:** JUnit & Mockito
- **Containerizzazione:** Docker, Docker Compose
- **Configurazione:** Properties e variabili d'ambiente (.env)

## Progettazione Database

Il sistema utilizza PostgreSQL con schema dedicato `pizzeria`:

- **Pizza**: Menu delle pizze con prezzo e disponibilità
- **Order**: Ordini con codice univoco e stato
- **OrderItem**: Dettagli degli elementi ordinati per ogni ordine

### Schema Database
```
Database: awesomepizza
Schema: pizzeria
Tables: pizzas, orders, order_items
```

## Inizializzazione Database e Specifica Java

Il progetto utilizza PostgreSQL come database con uno schema dedicato `pizzeria`.
Lo schema viene creato automaticamente tramite il file `schema.sql` durante l'avvio dell'applicazione.

Le tabelle vengono create automaticamente tramite Hibernate/JPA usando l'opzione:
`spring.jpa.hibernate.ddl-auto=update`

Questo significa che non è necessario eseguire script di setup manuali per inizializzare
le tabelle del database. Ogni volta che viene avviata l'applicazione, le modifiche alle Entity vengono automaticamente applicate
alle tabelle corrispondenti nel database.

Oltre alla sincronizzazione automatica delle tabelle, è stato introdotto un sistema di inizializzazione dati tramite il file `DataInitializer` che popola il database con dati di esempio (pizze del menu) al primo avvio.


## Stati dell'Ordine e Flusso di Lavoro

### Stati Disponibili
1. **PENDING** - In attesa di presa in carico
2. **IN_PROGRESS** - In preparazione  
3. **READY** - Pronto per il ritiro
4. **COMPLETED** - Completato

### Flusso Cliente
1. Visualizza menu pizze (`GET /api/v1/pizzas`)
2. Crea nuovo ordine (`POST /api/v1/orders`)
3. Riceve codice ordine (es. `ORD-12345678`)
4. Monitora stato ordine (`GET /api/v1/orders/{orderCode}`)

### Flusso Pizzaiolo  
1. Visualizza coda ordini (`GET /api/v1/orders/queue`)
2. Prende in carico ordine (`PUT /api/v1/orders/{orderCode}/take`)
3. Segna ordine pronto (`PUT /api/v1/orders/{orderCode}/ready`)
4. Completa ordine alla consegna (`PUT /api/v1/orders/{orderCode}/complete`)


## Panoramica API

L'applicazione espone API RESTful complete per la gestione di pizze e ordini. Una volta avviata, l'applicazione sarà disponibile su `http://localhost:8080` con documentazione Swagger su `http://localhost:8080/swagger-ui.html`.

### Pizze
- **GET** `/api/v1/pizzas` - Lista tutte le pizze disponibili nel menu
- **GET** `/api/v1/pizzas/{id}` - Ottiene una pizza specifica per ID
- **POST** `/api/v1/pizzas` - Crea una nuova pizza (amministratori)
- **PUT** `/api/v1/pizzas/{id}/availability` - Aggiorna disponibilità pizza

### Ordini
- **POST** `/api/v1/orders` - Crea un nuovo ordine
- **GET** `/api/v1/orders/{orderCode}` - Ottiene dettagli ordine tramite codice
- **GET** `/api/v1/orders/queue` - Lista ordini in attesa (pizzaioli)
- **PUT** `/api/v1/orders/{orderCode}/take` - Prende in carico un ordine
- **PUT** `/api/v1/orders/{orderCode}/ready` - Segna ordine come pronto
- **PUT** `/api/v1/orders/{orderCode}/complete` - Completa un ordine

### Esempi Pratici

#### Creazione Ordine
```json
POST /api/v1/orders
{
  "customerName": "Mario Rossi",
  "customerPhone": "123456789",
  "items": [
    {
      "pizzaId": 1,
      "quantity": 2,
      "notes": "Extra basilico"
    }
  ]
}
```

#### Risposta Creazione Ordine
```json
{
  "id": 1,
  "orderCode": "ORD-12345678",
  "customerName": "Mario Rossi",
  "customerPhone": "123456789",
  "status": "PENDING",
  "createdAt": "2025-07-05T10:30:00",
  "items": [...]
}
```



# 1. Avvio con Docker

Il progetto è completamente containerizzato con Docker e Docker Compose per un deployment semplice e consistente.

### 🚀 Avvio Rapido

**IMPORTANTE:** Quando avvii il sistema con Docker Compose, verranno automaticamente:
1. **Avviata l'applicazione** (i test saranno eseguiti in un container separato)
2. **Inizializzato il database** PostgreSQL con schema e dati di esempio

```bash
# Avvia l'intero stack (database + app)
docker-compose up --build

# Avvia in background
docker-compose up -d --build

# Ferma tutti i servizi
docker-compose down

# Visualizza i logs in tempo reale
docker-compose logs -f app
```
### Variabili d'Ambiente in ambiente containerizzato (.env)
```env
# Database Configuration
DB_HOST=postgres
DB_PORT=5432
DB_DATABASE=awesomepizza
DB_SCHEMA=pizzeria
DB_USERNAME=postgres
DB_PASSWORD=postgres

# Application Configuration
PORT=8080
SPRING_PROFILES_ACTIVE=docker

# JVM Configuration
JAVA_OPTS=-Xmx512m -Xms256m

# HikariCP Configuration
HIKARI_MINIMUM_IDLE=5
HIKARI_MAXIMUM_POOL_SIZE=20
HIKARI_CONNECTION_TIMEOUT=30000
HIKARI_IDLE_TIMEOUT=600000
HIKARI_MAX_LIFETIME=1800000
```

### Esecuzione Test Separati

Se vuoi eseguire solo i test senza avviare l'applicazione:

```bash
# Esegui test in ambiente Docker isolato
docker-compose --profile test up test

# Oppure con rebuild forzato
docker-compose --profile test up --build test
```

⚠️ IMPORTANTE: Prima di avviare i test, assicurarsi che schema `pizzeria` sia presente nel database PostgreSQL.

# 2. Avvio in locale

Le configurazioni per l'avvio in locale sono presenti nell'application.properties.

⚠️ **IMPORTANTE**: Prima di avviare l'applicazione in locale, è necessario creare lo schema `pizzeria` nel database PostgreSQL.

### Setup
```bash
# 1. Clone del repository
git clone https://github.com/Manuel-1996/awesomepizza.git
cd awesomepizza

# 2. Avvia solo PostgreSQL con Docker
docker-compose up -d postgres

# 3. Crea lo schema pizzeria (OBBLIGATORIO)
# Esegui il file schema.sql nel database PostgreSQL
docker-compose up -d schema-init

# 4. Configura application.properties per connessione locale
# (modifica src/main/resources/application.properties se necessario)

# 5. Compila e installa dipendenze
mvn clean install (aggiungere -DskipTests se si vogliono saltare i test)

# 6. Avvia l'applicazione
mvn spring-boot:run
```

### Note per lo sviluppo locale
- Lo schema `pizzeria` deve essere creato manualmente prima del primo avvio
- Hibernate si occuperà automaticamente di creare e aggiornare le tabelle
- Le configurazioni di connessione al database sono in `application.properties`

## Test

Il progetto include una suite completa di test unitari e di integrazione.
Assicurarsi che lo schema `pizzeria` sia stato prima inizializzato.

### Esecuzione Test

```bash
# Test con Maven
mvn test
# Test specifici, esempi:
mvn test -Dtest=OrderServiceTest
mvn test -Dtest=OrderControllerTest
```

### Copertura Test
- **Controller Tests:** Test di integrazione per i due Controller
- **Service Tests:** Test unitari per la logica di business

### Report Test
I report dei test vengono generati in:
- `target/surefire-reports/` - Report dettagliati dei test
- Console output con statistiche complete

# Configurazione Avanzata

### Personalizzazione Configurazione
Per modificare le configurazioni:
1. **Locale**: Modifica `src/main/resources/application.properties`
2. **Docker**: Modifica file `.env` e `application-docker.properties`