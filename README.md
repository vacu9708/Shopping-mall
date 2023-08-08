# The shopping mall consists of these microservices
### API gateway (port: 8080)
Acts as a single entry point for clients, routing requests to the appropriate microservices, including rate limiter and circuit breaker 

### Order management (port: 8081)
Handles order processing, payment processing (The refund feature will be added later)

### User management (port: 8082)
Handles user registration, auth, etc.

### Product management (port: 8083)
1. Manages the inventory, including tracking stock levels, and triggering alerts for low stock.
2. Manages the catalog of products available in the shopping mall, including product information, availability, and search functionality.

### Notification (port: 8084)
Sends notifications to users regarding order updates, promotions, etc.

# Branching strategy
- Main: Documentation, etc
- Release: Pushes to this branch trigger the Jenkins
- Development: Merged into the main branch
- Topics: Merged into the development branch

---
# Architecture
## ER diagram
![image](https://github.com/vacu9708/Shopping-mall/assets/67142421/f423c72a-6429-41a9-9a2f-3abfdc68d7ca)<br>
[Schema](https://github.com/vacu9708/Shopping-mall/blob/main/schema.sql)

## Class relationship diagram
![image](https://github.com/vacu9708/Shopping-mall/assets/67142421/ec5a40c2-72a8-4d37-a79a-c5d5ee5ebe44)

## CI/CD diagram
![image](https://github.com/vacu9708/Shopping-mall/assets/67142421/86c8824c-7680-458f-8e43-0ab68f6d4651)

## AWS S3 diagram
![image](https://github.com/vacu9708/Shopping-mall/assets/67142421/61eb5213-2f03-4012-958e-04a462c07658)

## Distributed transactions
The term "saga" in saga pattern refers to a sequence of distributed transactions or steps.<br>

1. `Start`: OrderService.makeOrder() triggers the saga
2. `Local transactions`: The saga includes payment, setting the stock, placing the order 
3. `Compensation`: If a transaction failed, the saga pattern invokes a compensation action that undoes the completed steps, ensuring data consistency.
4. `Completion`: The saga concludes when all the steps are either successfully completed or compensated.

---
## API documentation
https://youngsiks-organization.gitbook.io/shopping_mall/

---
# Used things
## Backend
Spring boot

## Spring cloud gateway
Spring cloud gateway acts as a router and forwards incoming requests to the appropriate downstream microservices based on the defined routes.<br>
It is combined with resilience4j, which monitors the calls to external services. If a particular service fails or responds slowly, it trips the circuit breaker, preventing further calls to that service.

## Database
MySQL

## Redis
- For storing data good to cache such as refresh tokens, wishlist, shopping cart
- (Rate limit)

## JWT
For decoupled auth service facilitating easier load balanacing

## Webflux, CompletableFuture
For non-blocking communication across microservices

## Kafka
For decoupled communication between microservices for scenarios such as order notifications.

## Docker
For containerizing individual microservices

## Cloud
AWS EC2(for deployment), AWS S3(for storing images)

## Jenkins
Jenkins in combination with github webhook, AWS EC2

---

# How to execute the application
1. Execute the docker-compose for Kafka and Redis
~~~
docker-compose -f kafka_redis_docker_compose.yml up -d
~~~
2. Execute Build all.bat
3. Execute docker-compose_start.bat
