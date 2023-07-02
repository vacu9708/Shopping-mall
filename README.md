# (Microservices architecture) shopping-mall
#### [개발 일기](https://github.com/vacu9708/Shopping-mall/tree/main/%EA%B0%9C%EB%B0%9C%20%EC%9D%BC%EA%B8%B0)

# The shopping mall consists of these microservices
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
Handles payment processing (The refund feature will be added later)

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
![image](https://github.com/vacu9708/Shopping-mall/assets/67142421/14b3373a-61b3-4296-88e3-5f08463945c3)<br>
### MySQL query
~~~sql
CREATE DATABASE user_management;
USE user_management;
CREATE TABLE users (
	`user_id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	`username` VARCHAR(20) NOT NULL,
	`password` VARCHAR(100) NOT NULL,
	`email` VARCHAR(40) NOT NULL,
    INDEX username(username)
);

CREATE DATABASE inventory;
USE inventory;
CREATE TABLE inventory (
	`product_id` BINARY(16) DEFAULT(UUID_TO_BIN(UUID())) PRIMARY KEY,
	`name` VARCHAR(20),
	`description` VARCHAR(100),
	`price` INT UNSIGNED,
	`stock` INT UNSIGNED DEFAULT(0)
);

CREATE DATABASE order_management;
USE order_management;
CREATE TABLE orders ( -- many to many 
	`order_id` BINARY(16) DEFAULT(UUID_TO_BIN(UUID())) PRIMARY KEY,
	`order_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	`user_id` BIGINT UNSIGNED NOT NULL, -- REFERENCES users(user_id)
	`product_id` BINARY(16) NOT NULL, -- REFERENCES products(product_id)
	`quantity` INT NOT NULL
);
~~~

## UML diagram


# Tech
## Basic frontend
Next.js

## Backend
Spring boot, JWT

## Database
MySQL

## Redis
- For storing data good to cache such as refresh tokens, wishlist, shopping cart
- Rate limit

## Spring cloud gateway
Spring cloud gateway acts as a router and forwards incoming requests to the appropriate downstream microservices based on the defined routes.

## Kafka
Kafka can handle high volumes of real-time data streams.<br>
Kafka enables communication between microservices for scenarios such as order notifications, inventory updates, etc.

## Docker
For containerizing individual microservices

## Cloud
AWS EC2(for deployment), AWS S3(for storing images)

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

## Kubernetes
- `Scalibility`: Kubernetes allows services to be easily scaled horizontally across a cluster of machines by adding or removing instances based on demand.
- `Service discovery`: Kubernetes provides a service discovery mechanism, allowing services to find and communicate with each other using DNS or environment variables.
- `load balancing`: Kubernetes also includes a load balancer that distributes traffic across multiple instances of a service.

## Netflix Hystrix
***Resilience***<br>
Hystrix introduces the concept of a circuit breaker pattern, where it monitors the calls to external services. If a particular service or endpoint starts failing or responding slowly, Hystrix can trip the circuit breaker, preventing further calls to that service

---
