# (Microservices architecture) shopping-mall

# What microservices the shopping mall consists of
### API Gateway
Acts as a single entry point for clients, routing requests to the appropriate microservices, and enforcing security and access controls.
### User Management
Handles user registration, auth, etc.
### Inventory
Manages the inventory of products in the shopping mall, tracking stock levels, and triggering alerts for low inventory.
### Catalog
Manages the catalog of products available in the shopping mall, including wishlist, product information, availability, and search functionality.
### Order Management
Handles order processing, including order placement and fulfillment.
### Payment
Handles payment processing, integrating with payment gateways to securely manage transactions.

### To be implemented in the future:
- **Notifications**: Sends notifications to users regarding order updates, promotions, and other relevant information.
- **Cart Service**: Manages the shopping carts for users, allowing them to add and remove items, calculate totals, and process orders.
- **Reviews and Ratings Service**: Manages customer reviews and ratings for products, allowing users to provide feedback and make informed decisions.
- **Recommendations Service**: Provides personalized product recommendations to users based on their browsing and purchase history.
- **Analytics Service**: Collects and analyzes data on user behavior, sales trends, and other metrics to gain insights and make data-driven decisions.

# Development methdologies
- MVC pattern
- Domain Driven Development for high cohesion
- Architecture design with ER diagram, UML diagram
- Collaboration with github pull requests
- CI/CD
- Test Driven Development using JUnit

# Tech stack
## Frontend
Next.js, Redux

## Backend
Spring boot, JWT

## Database
MySQL, (graphQL)

## Redis cache
For caching temporary data(shopping cart), data that requires a fast response speed(wishlist), result of complex queries

## Swagger(springdoc 2.1)
For generating API documentation automatically

## Spring cloud gateway
Spring cloud gateway acts as a router and forwards incoming requests to the appropriate downstream microservices based on the defined routes.

## Kafka
Kafka can handle high volumes of real-time data streams.<br>
Kafka enables communication between microservices for scenarios such as order notifications, inventory updates, etc.

## Monitoring and Logging
### Logback with Logstash encoder:
Logback as the logging framework and a Logstash encoder to format log messages in a way that can be easily consumed by Logstash.

### ELK stack
#### `Elasticsearch`
For log Aggregation, Analysis, and Alerting<br>
Log data from microservices is sent to Elasticsearch which is a distributed search and analytics.<br>
Elasticsearch can also track the health and performance of the microservices for alerting.

#### `Logstash`
Centralized log collection pipeline<br>
Logstash can parse, filter, and transform log data before sending it to Elasticsearch for storage and analysis.

#### `Kibana`
Visualization and Dashboards

## Netflix Eureka
Eureka enables ***service registration and discovery*** within a distributed system, making it easier for microservices to discover and communicate with them. By integrating Eureka with Spring Cloud Gateway, the gateway can dynamically discover and route requests to available services without hardcoding their addresses.

## Netflix Ribbon
***Load balancing***<br>
Ribbon helps manage the load balancing of requests across multiple instances of a particular service. It dynamically distributes the requests to available instances, considering factors like server health, latency, and other configurable rules. Ribbon integrates with service discovery mechanisms (such as Netflix Eureka) to obtain an up-to-date list of available service instances.

## Netflix Hystrix
***Resilience***<br>
Hystrix introduces the concept of a circuit breaker pattern, where it monitors the calls to external services. If a particular service or endpoint starts failing or responding slowly, Hystrix can trip the circuit breaker, preventing further calls to that service

## Docker, Kubernetes
For containerizing individual microservices and orchestrating them using Kubernetes

## CI/CD
Jenkins, AWS EC2<br>
CI/CD pipelines are used to automate build, testing and deployment process.

---

# Architecture
Saga pattern for distributed transactions
## ER diagram

## UML diagram
