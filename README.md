# (Microservices architecture) shopping-mall
#### [개발 일기](https://github.com/vacu9708/Shopping-mall/tree/main/%EA%B0%9C%EB%B0%9C%20%EC%9D%BC%EA%B8%B0)

# The shopping mall consists of these microservices (Only essential services for the moment)
### API Gateway
Acts as a single entry point for clients, routing requests to the appropriate microservices, and enforcing security and access controls.
### User Management
Handles user registration, auth, etc.
### Inventory
Manages the inventory of products in the shopping mall, tracking stock levels, and triggering alerts for low inventory.
### Product display
Manages the catalog of products available in the shopping mall, including product information, availability, and search functionality.
### Order Management
Handles order processing, including order placement and fulfillment.
### Payment
Handles payment processing, integrating with payment gateways to securely manage transactions.

### To be implemented in the future:
- **Notifications**: Sends notifications to users regarding order updates, promotions, etc.
- **Shopping cart and wishlist**
- **Reviews and Ratings Service**: Manages customer reviews and ratings for products, allowing users to provide feedback and make informed decisions.
- **Recommendations Service**: Provides personalized product recommendations to users based on their browsing and purchase history.
- **Analytics Service**: Collects and analyzes data on user behavior, sales trends, and other metrics to gain insights and make data-driven decisions.

# Development methdologies
- Architecture design with ER diagram, UML diagram
- MVC pattern and Domain Driven Development for high cohesion
- Test Driven Development using JUnit including e2e tests and stress tests to be confident after code modifications
- CI/CD to automate building, testing, deployment
- Version control with github

# Branching strategy
- Topic/branches (Commits to this branch trigger the CI process)
- Main (Commits to this branch trigger the CD process)
- Development (For automatic deployment to the development server but not used in this project)
 
# Architecture
Saga pattern for distributed transactions

## ER diagram

## UML diagram


# Tech stack
## Basic frontend
Next.js

## Backend
Spring boot, JWT

## Database
MySQL, (graphQL)

## Redis
For storing data good to cache such as refresh tokens, wishlist, shopping cart

## Swagger(springdoc 2.1)
For generating API documentation automatically

## Spring cloud gateway
Spring cloud gateway acts as a router and forwards incoming requests to the appropriate downstream microservices based on the defined routes.

## Kafka
Kafka can handle high volumes of real-time data streams.<br>
Kafka enables communication between microservices for scenarios such as order notifications, inventory updates, etc.

## Docker, Kubernetes
For containerizing individual microservices and orchestrating them using Kubernetes

## Cloud
AWS EC2 for deployment, AWS S3(for storing images)

## CI/CD
Jenkins (in combination with github webhook, AWS EC2)<br>
CI/CD pipelines are used to automate build, testing and deployment process.

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
***service registration and discovery within a distributed system***<br>
Eureka makes it easier for microservices to discover and communicate with them. By integrating Eureka with Spring Cloud Gateway, the gateway can dynamically discover and route requests to available services without hardcoding their addresses.

## Netflix Ribbon
***Load balancing***<br>
Ribbon helps manage the load balancing of requests across multiple instances of a particular service. It dynamically distributes the requests to available instances, considering factors like server health, latency, and other configurable rules. Ribbon integrates with service discovery mechanisms (such as Netflix Eureka) to obtain an up-to-date list of available service instances.

## Netflix Hystrix
***Resilience***<br>
Hystrix introduces the concept of a circuit breaker pattern, where it monitors the calls to external services. If a particular service or endpoint starts failing or responding slowly, Hystrix can trip the circuit breaker, preventing further calls to that service

---
