# Future plan
## Integration test code
Write integration test code

## Load balancing
Use EC2 load balancer or Kubernetes
### Kubernetes
- `Scalibility`: Kubernetes allows services to be easily scaled horizontally across a cluster of machines by adding or removing instances based on demand.
- `Service discovery`: Kubernetes provides a service discovery mechanism, allowing services to find and communicate with each other using DNS or environment variables.
- `Load balancing`: Kubernetes also includes a load balancer that distributes traffic across multiple instances of a service.

### DB load balancing
Read replicas help handling large read-only traffic. (Use AWS RDS)

## Monitoring
### Logback with Logstash encoder:
Logback as the logging framework and a Logstash encoder to format log messages in a way that can be easily consumed by Logstash.

### ELK stack
- `Elasticsearch`: For log Aggregation, Analysis, and Alerting<br>
Log data from microservices is sent to Elasticsearch which is a distributed search and analytics.<br>
Elasticsearch can also track the health and performance of the microservices for alerting.

- `Logstash`: Centralized log collection pipeline<br>
Logstash can parse, filter, and transform log data before sending it to Elasticsearch for storage and analysis.

- `Kibana`: Visualization and Dashboards

## Compensation after distributed transactions failed because of system failures (like unexpected shutdowns)
- Keep logging and automate the compensation after system failures.
- When a compensation failed, retry multiple times before leaving a log.

## Circuit breaker
Combine Cloud gateway with resilience4j, which monitors the calls to external services. If a particular service fails or responds slowly, it trips the circuit breaker, preventing further calls to that service.

## Essential but incomplete features
- Payment
- cancelOrder()

## Frontend
Next.js, (redux?)

### Microservices to be implemented in the future
- **Shopping cart and wishlist in the Product management service**
- **Reviews and Ratings Service**: Manages customer reviews and ratings for products, allowing users to provide feedback and make informed decisions.
- **Recommendations Service**: Provides personalized product recommendations to users based on their browsing and purchase history.
- **Analytics Service**: Collects and analyzes data on user behavior, sales trends, and other metrics to gain insights and make data-driven decisions.
