# Order Service

Spring Boot 3.3 microservice responsible for managing customer orders within the SpecSync e-commerce platform.

## Overview

The Order Service handles the full order lifecycle, from creation through delivery. When creating an order it calls the **User Service** to validate the customer and the **Product Service** to validate product availability and pricing.

## Technology Stack

| Technology | Version |
|---|---|
| Java | 21 |
| Spring Boot | 3.3.4 |
| Spring Data JPA | 3.3.4 |
| Spring WebFlux (WebClient) | 3.3.4 |
| H2 (in-memory DB) | runtime |
| springdoc-openapi | 2.6.0 |
| Lombok | latest |

## Configuration

| Property | Value |
|---|---|
| Server port | **8083** |
| H2 Console | http://localhost:8083/h2-console |
| Swagger UI | http://localhost:8083/swagger-ui.html |
| OpenAPI JSON | http://localhost:8083/v3/api-docs |
| JDBC URL | `jdbc:h2:mem:orderdb` |

## API Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/api/orders` | List all orders |
| GET | `/api/orders/{id}` | Get order by ID |
| GET | `/api/orders/user/{userId}` | Get orders by user ID |
| GET | `/api/orders/status/{status}` | Get orders by status |
| POST | `/api/orders` | Create a new order |
| PATCH | `/api/orders/{id}/status` | Update order status |
| PATCH | `/api/orders/{id}/cancel` | Cancel an order |
| DELETE | `/api/orders/{id}` | Delete an order |

## Order Statuses

`PENDING` → `CONFIRMED` → `PROCESSING` → `SHIPPED` → `DELIVERED`

Cancellation is allowed only from `PENDING` or `CONFIRMED`. Refund is a terminal state set manually.

## Request / Response Examples

### Create Order — POST /api/orders

```json
{
  "userId": 1,
  "shippingAddress": "123 Main St, Springfield, IL 62701",
  "notes": "Please leave at front door",
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 3, "quantity": 1 }
  ]
}
```

### Update Status — PATCH /api/orders/{id}/status

```json
{ "status": "CONFIRMED" }
```

## Dependencies on Other Services

| Service | Base URL | Used For |
|---|---|---|
| user-service | http://localhost:8081 | Validate user exists before creating order |
| product-service | http://localhost:8082 | Validate product exists, get price, check stock |

The service uses **Spring WebFlux WebClient** for outbound HTTP calls. If a dependent service is unreachable, `createOrder` returns HTTP 503. Missing user/product returns HTTP 422.

## How to Run

### Prerequisites

- Java 21+
- Maven 3.9+
- user-service running on port 8081 (optional — returns 503 if unavailable)
- product-service running on port 8082 (optional — returns 503 if unavailable)

### Build and Run

```bash
cd order-service
mvn clean package -DskipTests
java -jar target/order-service-1.0.0.jar
```

Or with Maven directly:

```bash
mvn spring-boot:run
```

### Run All Services (from project root)

```bash
# Terminal 1
cd user-service && mvn spring-boot:run

# Terminal 2
cd product-service && mvn spring-boot:run

# Terminal 3
cd order-service && mvn spring-boot:run
```

### Verify the Service

```bash
# Health check
curl http://localhost:8083/actuator/health 2>/dev/null || curl http://localhost:8083/api/orders

# List all orders
curl http://localhost:8083/api/orders

# Get order by ID
curl http://localhost:8083/api/orders/1

# Create a new order
curl -X POST http://localhost:8083/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"shippingAddress":"123 Test St","items":[{"productId":1,"quantity":1}]}'
```

## Sample Data

On startup, `data.sql` inserts 5 sample orders in various statuses:

| Order ID | User ID | Status | Total |
|---|---|---|---|
| 1 | 1 | DELIVERED | $149.97 |
| 2 | 2 | SHIPPED | $299.98 |
| 3 | 1 | CONFIRMED | $89.99 |
| 4 | 3 | PENDING | $199.95 |
| 5 | 2 | CANCELLED | $49.99 |
