# The shopping mall consists of these microservices
### API gateway
Acts as a single entry point for clients, routing requests to the appropriate microservices 

### Order management
Handles order processing

### User management
Handles user-related features such as user registration, auth, etc.

### Product management
1. Manages the inventory
2. Manages the catalog of products available in the shopping mall

### Notification
Sends notifications to users regarding orders, promotions, verification email

---
# Architecture
## Branching strategy
- Topics: Development is performed and completed work is merged into the Development branch
- Development: Changes are tested and merged into the Relase branch
- Release: Pushes to this branch trigger the Jenkins for deployment
- Main: Documentation, etc

## ER diagram ([Schema](https://github.com/vacu9708/Shopping-mall/blob/main/DB/schema.sql))
![image](https://github.com/vacu9708/Shopping-mall/assets/67142421/51fd3bb9-5adb-4986-a3fb-a7f2529e3126)<br>
This schema is normalized but denormalizing "productㄴ" table and "ordered_items" table seems good for better distributed JOINs.

## Class relationship
![image](https://github.com/vacu9708/Shopping-mall/assets/67142421/b9ae82d9-d33d-44f2-82f5-01f0434f5e58)

## CI/CD diagram
![image](https://github.com/vacu9708/Shopping-mall/assets/67142421/86c8824c-7680-458f-8e43-0ab68f6d4651)

## AWS S3 diagram
![image](https://github.com/vacu9708/Shopping-mall/assets/67142421/61eb5213-2f03-4012-958e-04a462c07658)

## Login flow
![image](https://github.com/vacu9708/Shopping-mall/assets/67142421/9db51f76-12eb-460b-a431-062f00bc773f)

## Making order flow (Distributed transaction)
![image](https://github.com/vacu9708/Shopping-mall/assets/67142421/6634f67a-84a7-4adb-9e08-2adf6640c65d)

---
### [API documentation link](https://youngsiks-organization.gitbook.io/shopping_mall/)
### [Future plan link](https://github.com/vacu9708/Shopping-mall/blob/main/Future%20plan.md)

---
# What was used
## Backend
Spring boot

## Database
MySQL

## JWT
For decoupled authentication, authrization facilitating easier load balancing

## Webflux, CompletableFuture
For non-blocking communication across services

## Redis
- For storing data good to cache such as refresh tokens, (wishlist, shopping cart)
- For request rate limit

## Spring cloud gateway
Spring cloud gateway acts as a router and forwards incoming requests to the appropriate downstream microservices based on the defined routes.

## Spring AOP
Used to permit only authenticated requests to specific APIs

## Kafka
For decoupled communication between microservices for scenarios such as order notifications.

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
2. Create "credentials" file in \product_management. [The file's format](https://github.com/vacu9708/Tools-etc/blob/main/AWS/AWS%20credentials.md)
3. Go to "build_and_run" directory
4. Execute the commands that are in build_all.txt to build the Spring apps
5. Execute the commands that are in docker-compose_prerequisites.txt
6. Execute the schema generation queries that are in schema.sql
7. Execute the commands that are in docker-compose_services.txt
8. Expose ports on which the containers run
