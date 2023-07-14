# The shopping mall consists of these microservices
### API gateway (port: 8080)
Acts as a single entry point for clients, routing requests to the appropriate microservices, including rate limiter and circuit breaker 

### Order management (port: 8081)
Handles order processing, payment processing (The refund feature will be added later)

### User management (port: 8082) [API documentation](https://documenter.getpostman.com/view/26215585/2s946eBu67)
Handles user registration, auth, etc.

### Product management (port: 8083)
1. Manages the inventory, including tracking stock levels, and triggering alerts for low stock.
2. Manages the catalog of products available in the shopping mall, including product information, availability, and search functionality.

### Notification (port: 8084)
Sends notifications to users regarding order updates, promotions, etc.

### To be implemented in the future:
- **Shopping cart and wishlist in the Product management service**
- **Reviews and Ratings Service**: Manages customer reviews and ratings for products, allowing users to provide feedback and make informed decisions.
- **Recommendations Service**: Provides personalized product recommendations to users based on their browsing and purchase history.
- **Analytics Service**: Collects and analyzes data on user behavior, sales trends, and other metrics to gain insights and make data-driven decisions.

# Branching strategy
- Main: Documentation, other files
- Release: Pushes to this sbranch trigger the CI/CD process
- Development: Merged into the main branch
- Topics: Merged into the development branch
 
# Architecture
## ER diagram
![image](https://github.com/vacu9708/Shopping-mall/assets/67142421/f423c72a-6429-41a9-9a2f-3abfdc68d7ca)

## Class relationship diagram
![image](https://github.com/vacu9708/Shopping-mall/assets/67142421/17349e56-2384-4227-894a-69b048861ab7)

## CI/CD diagram
![image](https://github.com/vacu9708/Shopping-mall/assets/67142421/86c8824c-7680-458f-8e43-0ab68f6d4651)

## AWS S3 diagram
![image](https://github.com/vacu9708/Shopping-mall/assets/67142421/e7cb8201-b7ce-413e-a84d-6723a0bb6097)

## Distributed transactions
The term "saga" in saga pattern refers to a sequence of distributed transactions or steps.<br>

1. `Start`: OrderService.makeOrder() triggers the saga
2. `Local transactions`: The saga includes payment, setting the stock, placing the order 
3. `Compensation`: If a transaction failed, the saga pattern invokes a compensation action that undoes the completed steps, ensuring data consistency.
4. `Completion`: The saga concludes when all the steps are either successfully completed or compensated.

---

# Used things
## Basic frontend
Next.js, (redux?)

## Backend
Spring boot

## Spring cloud gateway
Spring cloud gateway acts as a router and forwards incoming requests to the appropriate downstream microservices based on the defined routes.<br>
It is combined with resilience4j, which monitors the calls to external services. If a particular service fails or responds slowly, it trips the circuit breaker, preventing further calls to that service.

## Database
MySQL

## Redis
- For storing data good to cache such as refresh tokens, wishlist, shopping cart
- Rate limit

## JWT
For decoupled auth service facilitating easier load balanacing

## Webflux, CompletableFuture
For non-blocking communication across microservices

## Kafka
For decoupled communication between microservices for scenarios such as order notifications.

## Testing
Junit

## Docker
For containerizing individual microservices

## Cloud
AWS EC2(for deployment), AWS S3(for storing images)

## CI/CD
Jenkins (in combination with github webhook, AWS EC2)<br>
CI/CD pipelines are used to automate build, testing and deployment process.

## Postman
For API testing and documentation

## Kubernetes (to be implemented later)
- `Scalibility`: Kubernetes allows services to be easily scaled horizontally across a cluster of machines by adding or removing instances based on demand.
- `Service discovery`: Kubernetes provides a service discovery mechanism, allowing services to find and communicate with each other using DNS or environment variables.
- `Load balancing`: Kubernetes also includes a load balancer that distributes traffic across multiple instances of a service.

## Monitoring and Logging (to be implemented later)
### Logback with Logstash encoder:
Logback as the logging framework and a Logstash encoder to format log messages in a way that can be easily consumed by Logstash.

### ELK stack
- `Elasticsearch`: For log Aggregation, Analysis, and Alerting<br>
Log data from microservices is sent to Elasticsearch which is a distributed search and analytics.<br>
Elasticsearch can also track the health and performance of the microservices for alerting.

- `Logstash`: Centralized log collection pipeline<br>
Logstash can parse, filter, and transform log data before sending it to Elasticsearch for storage and analysis.

- `Kibana`: Visualization and Dashboards
---
