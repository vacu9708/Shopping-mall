# The shopping mall consists of these microservices
### API gateway
Acts as a single entry point for clients, routing requests to the appropriate microservices.
It also controls the incoming reequests with Request rate limiting and Circuit breaker.

### Order management
Handles order processing

### User management
Handles user-related features such as user registration, auth, etc.

### Product management
1. Manages the inventory
2. Manages the catalog of products available in the shopping mall

### Notification
Sends emails to users regarding orders, sign-in verification, etc

---
# Architecture
## Branching strategy
- Topics: Development is performed and completed work is merged into the Development branch
- Development: Changes are tested and merged into the Relase branch
- Release: Pushes to this branch trigger the Jenkins for deployment
- Main: Documentation, etc

## ER diagram ([Schema](https://github.com/vacu9708/Shopping-mall/blob/main/DB/schema.sql))
![image](https://github.com/vacu9708/Shopping-mall/assets/67142421/51fd3bb9-5adb-4986-a3fb-a7f2529e3126)<br>
This schema is normalized but denormalizing "products" table and "ordered_items" table seems good for better distributed JOINs.

## Sequence diagram of makeOrder() (Distributed transaction)
![image](https://github.com/vacu9708/Shopping-mall/assets/67142421/0485da5a-82a5-4670-8525-374ded1bfdb1)

## Load balancing to handle sending a large number of emails
![image](https://github.com/vacu9708/Shopping-mall/assets/67142421/831269c0-87eb-423f-83b0-9f6c035e1e17)

## CI/CD diagram
![image](https://github.com/vacu9708/Shopping-mall/assets/67142421/09d0cb75-9a06-4f66-95a1-b6b5301aa8a1)

## AWS S3 diagram
![image](https://github.com/vacu9708/Shopping-mall/assets/67142421/61eb5213-2f03-4012-958e-04a462c07658)

## Login flow
![image](https://github.com/vacu9708/Shopping-mall/assets/67142421/2990c13e-c6a0-4d0c-8e9d-ef1198e5906e)

## Class relationship
![image](https://github.com/vacu9708/Shopping-mall/assets/67142421/f96a837a-85c6-431b-bd2c-9185e4104c1e)

---
### [API documentation](https://youngsiks-organization.gitbook.io/shopping_mall/)
#### [Incomplete features](https://github.com/vacu9708/Shopping-mall/blob/main/Future%20plan.md)

---
# What was used
## Base
Spring boot, (junit)

## Database
MySQL

## JWT
For decoupled authentication, authorization facilitating easier load balancing

## Webflux, CompletableFuture
For non-blocking communication across services

## Redis
- For storing data good to cache such as token revocation info
- For limiting the rate of requests coming into the gateway using the Request rate limiter

## Spring cloud gateway
- Spring cloud gateway acts as a router that forwards incoming requests to the appropriate downstream microservices based on the defined routes.
- The circuit breaker is applied.

## Spring AOP
Used to permit only authenticated requests to specific APIs

## Spring mail
Used to send emails to users. Load balancing is applied for sending emails using Kafka

## Kafka
For decoupled communication between microservices for scenarios such as order notifications, sign-in verification.

## Docker
For containerizing individual microservices so that they can work without the environment setting

## Cloud
- AWS EC2(for deployment)
- AWS S3(for storing images)

## Jenkins
Jenkins in combination with github webhook, AWS EC2

---

# How to execute the application
The current docker-compose only works on Linux
1. Install Docker
2. Create "credentials" file in \product_management for your AWS S3 access. [The file's format](https://github.com/vacu9708/Tools-etc/blob/main/AWS/AWS%20credentials.md)
3. Go to "build_and_run" directory
4. Execute the commands that are in build_all.txt to build the Spring apps
5. Execute the commands that are in docker-compose_prerequisites.txt
6. Enter the MySQL and execute the schema generation queries that are in schema.sql
7. Execute the commands that are in docker-compose_services.txt
8. Expose ports on which the containers run
