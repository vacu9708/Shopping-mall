# Essential but incomplete features
- Payment
- cancelOrder()

# Load balancing
Use EC2 load balancer or Kubernetes
### Kubernetes
- `Scalibility`: Kubernetes allows services to be easily scaled horizontally across a cluster of machines by adding or removing instances based on demand.
- `Service discovery`: Kubernetes provides a service discovery mechanism, allowing services to find and communicate with each other using DNS or environment variables.
- `Load balancing`: Kubernetes also includes a load balancer that distributes traffic across multiple instances of a service.
# DB
- Read replicas help handling large read-only traffic. (Use AWS RDS)
- Cache queries using Redis
- DB backup using Spring batch

# Performance monitoring
### ELK stack
- `Elasticsearch`: For log Aggregation, Analysis, and Alerting<br>
Log data from microservices is sent to Elasticsearch which is a distributed search and analytics.<br>
Elasticsearch can also track the health and performance of the microservices for alerting.

- `Logstash`: Centralized log collection pipeline<br>
Logstash can parse, filter, and transform log data before sending it to Elasticsearch for storage and analysis.

- `Kibana`: Visualization and Dashboards

# Compensation after system failures (like unexpected shutdowns)
- Keep logging of committed local transactions and automate their compensation when their saga did not complete due to system failures.


### Frontend(?)
Next.js, (redux?)
