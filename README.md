# (Microservices architecture) shopping-mall

# What microservices the shopping mall consists of
- **API Gateway**: Acts as a single entry point for clients, routing requests to the appropriate microservices, and enforcing security and access controls.
- **User Management**: Handles user authentication, registration, and profile management.
- **Inventory**: Manages the inventory of products in the shopping mall, tracking stock levels, and triggering alerts for low inventory.
- **Catalog**: Manages the catalog of products available in the shopping mall, including product information, pricing, availability, and search functionality.
- **Order Management**: Handles order processing, including order placement, tracking, and fulfillment.
- **Payment**: Handles payment processing, integrating with payment gateways to securely manage transactions.
- **Notifications**: Sends notifications to users regarding order updates, promotions, and other relevant information.

### Services I want to implement in the future:
- Cart Service: Manages the shopping carts for users, allowing them to add and remove items, calculate totals, and process orders.
- Reviews and Ratings Service: Manages customer reviews and ratings for products, allowing users to provide feedback and make informed decisions.
- Recommendations Service: Provides personalized product recommendations to users based on their browsing and purchase history.
- Analytics Service: Collects and analyzes data on user behavior, sales trends, and other metrics to gain insights and make data-driven decisions.

# Tech stack
## Frontend
Next.js
## Backend
Spring boot

## Database
MySQL, (graphQL)

## Test Driven Development
JUnit

## Springfox Swagger
For generating API documentation automatically

## Kafka
Kafka can handle high volumes of real-time data streams.<br>
Kafka enables communication between microservices for scenarios such as order notifications, inventory updates, etc.

## Redis
Redis is used to store frequently accessed data such as product information, user profiles, refresh tokens, or search results.<br>
This helps reduce the load on the DB and speeds up response times.

## Monitoring and Logging
### Logback with Logstash encoder:
Logback as the logging framework and a Logstash encoder to format log messages in a way that can be easily consumed by Logstash.

### ELK stack
#### Elasticsearch:
For log Aggregation, Analysis, and Alerting<br>
Log data from microservices is sent to Elasticsearch which is a distributed search and analytics.<br>
Elasticsearch can also track the health and performance of the microservices for alerting.

#### Logstash:
Centralized log collection pipeline<br>
Logstash can parse, filter, and transform log data before sending it to Elasticsearch for storage and analysis.

#### Kibana:
Visualization and Dashboards

## Netflix Ribbon, Netflix Hystrix
For Load Balancing and Resilience<br>
Employ load balancing mechanisms for distributing requests across multiple instances of microservices. Use Hystrix for fault tolerance and implementing circuit breaker patterns to handle failures gracefully.

## Docker, Kubernetes
For containerizing individual microservices and orchestrating them using Kubernetes

## CI/CD
Github, Jenkins, AWS EC2<br>
CI/CD pipelines are used to automate build, testing and deployment process.
