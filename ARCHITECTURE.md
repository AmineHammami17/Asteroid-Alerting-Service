# Asteroid Alerting Service - Architecture Documentation

## System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         Client Application                       │
│                    (API Consumer/Trigger)                        │
└───────────────────────────────┬─────────────────────────────────┘
                                │
                                │ HTTP POST /api/v1/asteroid-alerting/alert
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Spring Boot Application                       │
│                   (Asteroid Alerting Service)                    │
│                                                                   │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              Controller Layer                             │  │
│  │  AsteroidAlertingController                               │  │
│  │  - POST /alert endpoint                                   │  │
│  └──────────────────┬───────────────────────────────────────┘  │
│                     │                                            │
│                     ▼                                            │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              Service Layer                                │  │
│  │  AsteroidAlertingService                                  │  │
│  │  - Business logic for alerts                              │  │
│  │  - Filter hazardous asteroids                             │  │
│  │  - Coordinate NASA API calls                              │  │
│  └──────────────────┬───────────────────────────────────────┘  │
│                     │                                            │
│                     ▼                                            │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              Client Layer                                 │  │
│  │  NasaClient                                               │  │
│  │  - HTTP client for NASA API                               │  │
│  │  - Request/response handling                              │  │
│  └──────────────────┬───────────────────────────────────────┘  │
│                     │                                            │
└─────────────────────┼────────────────────────────────────────────┘
                      │
                      │ HTTPS GET
                      ▼
┌─────────────────────────────────────────────────────────────────┐
│                      NASA NeoWs API                              │
│            https://api.nasa.gov/neo/rest/v1/feed                │
│  - Near Earth Object data                                        │
│  - 7-day lookback window                                         │
└─────────────────────────────────────────────────────────────────┘

Optional (Future):
                      │
                      │ Kafka Message
                      ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Apache Kafka                                │
│                   (Message Broker)                               │
│  Topic: asteroid-alerts                                          │
└─────────────────────────────────────────────────────────────────┘
```

## Component Details

### 1. Controller Layer

**File**: `AsteroidAlertingController.java`

**Responsibilities**:
- Expose REST API endpoints
- Handle HTTP requests/responses
- Delegate to service layer
- Return appropriate HTTP status codes

**Endpoints**:
```
POST /api/v1/asteroid-alerting/alert
Status: 202 Accepted
Body: None
```

**Current State**: ✅ Implemented

---

### 2. Service Layer

**File**: `AsteroidAlertingService.java`

**Responsibilities**:
- Core business logic
- Orchestrate NASA API calls
- Filter potentially hazardous asteroids
- Log/publish alert data
- Handle date range calculations

**Key Methods**:
```java
public void alert()
```

**Current State**: 🟡 Partially Implemented
- Calculates date range (today + 7 days)
- Missing: NASA API integration
- Missing: Alert filtering logic
- Missing: Kafka publishing

---

### 3. Client Layer

**File**: `NasaClient.java`

**Responsibilities**:
- HTTP client for NASA API
- Request construction
- Response parsing
- Error handling for API calls
- API key management

**Expected Methods**:
```java
public Map<LocalDate, List<Asteroid>> getAsteroids(LocalDate startDate, LocalDate endDate)
```

**Current State**: ❌ Not Implemented (empty class)

---

### 4. Entity Layer

**Files**: 
- `Asteroid.java`
- `CloseApproachData.java`
- `EstimatedDiameter.java`
- `DiameterRange.java`
- `MissDistance.java`

**Responsibilities**:
- Data models for NASA API response
- JSON deserialization mapping
- Domain object representation

**Current State**: ❌ Not Implemented (all empty classes)

---

## Data Model

### Entity Relationships

```
┌─────────────────────────────────────────┐
│            Asteroid                      │
├─────────────────────────────────────────┤
│ - id: String                             │
│ - name: String                           │
│ - absoluteMagnitudeH: Double             │
│ - isPotentiallyHazardous: Boolean        │
│ - estimatedDiameter: EstimatedDiameter   │◄─────┐
│ - closeApproachData: List<...>           │◄───┐ │
└─────────────────────────────────────────┘    │ │
                                                │ │
┌─────────────────────────────────────────┐    │ │
│       CloseApproachData                  │    │ │
├─────────────────────────────────────────┤    │ │
│ - closeApproachDate: LocalDate           │◄───┘ │
│ - relativeVelocity: RelativeVelocity     │      │
│ - missDistance: MissDistance             │◄───┐ │
│ - orbitingBody: String                   │    │ │
└─────────────────────────────────────────┘    │ │
                                                │ │
┌─────────────────────────────────────────┐    │ │
│        MissDistance                      │    │ │
├─────────────────────────────────────────┤    │ │
│ - astronomical: String                   │◄───┘ │
│ - lunar: String                          │      │
│ - kilometers: String                     │      │
│ - miles: String                          │      │
└─────────────────────────────────────────┘      │
                                                  │
┌─────────────────────────────────────────┐      │
│      EstimatedDiameter                   │      │
├─────────────────────────────────────────┤      │
│ - kilometers: DiameterRange              │◄─────┘
│ - meters: DiameterRange                  │
│ - miles: DiameterRange                   │
│ - feet: DiameterRange                    │
└─────────────────────────────────────────┘
         │
         │
         ▼
┌─────────────────────────────────────────┐
│        DiameterRange                     │
├─────────────────────────────────────────┤
│ - estimatedDiameterMin: Double           │
│ - estimatedDiameterMax: Double           │
└─────────────────────────────────────────┘
```

---

## Technology Stack Details

### Core Framework
- **Spring Boot**: 3.5.0
  - Spring Web (REST APIs)
  - Spring Kafka (Message streaming)
  
### Java
- **Version**: 17
- **Build Tool**: Maven 3.9.9
- **Wrapper**: Included (mvnw)

### Libraries
- **Lombok**: Annotation processing for boilerplate code
  - `@Data`, `@Slf4j`, `@AllArgsConstructor`, etc.
  
### Testing
- **JUnit**: 5 (Jupiter)
- **Spring Boot Test**: Integration testing
- **Spring Kafka Test**: Kafka integration testing

---

## Configuration Architecture

### Current Configuration
```properties
# application.properties
spring.application.name=asteroidalert
```

### Required Configuration (Missing)
```properties
# NASA API Configuration
nasa.api.url=https://api.nasa.gov/neo/rest/v1
nasa.api.key=${NASA_API_KEY:DEMO_KEY}
nasa.api.connect-timeout=5000
nasa.api.read-timeout=30000

# Alert Configuration
asteroid.alert.enabled=true
asteroid.alert.check-hazardous-only=true
asteroid.alert.diameter-threshold-km=0.5
asteroid.alert.distance-threshold-km=10000000

# Kafka Configuration (if enabled)
spring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
asteroid.alert.kafka.topic=asteroid-alerts
asteroid.alert.kafka.enabled=false

# Logging
logging.level.com.project.asteroidalert=DEBUG
logging.level.org.springframework.web=INFO
```

---

## API Flow Diagram

### Request Flow

```
1. Client Request
   ▼
POST /api/v1/asteroid-alerting/alert
   │
   ▼
2. Controller receives request
   AsteroidAlertingController.alert()
   │
   ▼
3. Service processes request
   AsteroidAlertingService.alert()
   │
   ├─ Calculate date range (today to +7 days)
   │
   ├─ Call NASA API
   │  ▼
   │  NasaClient.getAsteroids(startDate, endDate)
   │  │
   │  ├─ Build request URL with params
   │  ├─ Add API key to request
   │  ├─ Execute HTTP GET request
   │  ├─ Parse JSON response
   │  └─ Return List<Asteroid>
   │
   ├─ Filter hazardous asteroids
   │  (isPotentiallyHazardous == true)
   │
   ├─ Apply threshold filters
   │  (diameter, distance)
   │
   └─ Publish alerts
      │
      ├─ Log to console (current)
      │
      └─ Publish to Kafka (future)
   
4. Return Response
   HTTP 202 Accepted
```

---

## Deployment Architecture

### Local Development

```
┌─────────────────────────────────────────┐
│  Developer Machine                       │
│                                          │
│  ┌────────────────────────────────────┐ │
│  │  Spring Boot App                   │ │
│  │  Port: 8080                        │ │
│  └────────────────────────────────────┘ │
│                                          │
│  ┌────────────────────────────────────┐ │
│  │  Kafka (Optional)                  │ │
│  │  Port: 9092                        │ │
│  └────────────────────────────────────┘ │
└─────────────────────────────────────────┘
           │
           │ HTTPS
           ▼
┌─────────────────────────────────────────┐
│  NASA API (Internet)                     │
└─────────────────────────────────────────┘
```

### Production (Future)

```
┌─────────────────────────────────────────────────────────┐
│                    Load Balancer                         │
└───────────────┬──────────────────────┬──────────────────┘
                │                      │
     ┌──────────▼────────┐   ┌────────▼──────────┐
     │  App Instance 1   │   │  App Instance 2   │
     │  (Container)      │   │  (Container)      │
     └──────────┬────────┘   └────────┬──────────┘
                │                     │
                └──────────┬──────────┘
                           │
                ┌──────────▼───────────┐
                │   Kafka Cluster      │
                │   (3 brokers)        │
                └──────────────────────┘
```

---

## Security Architecture

### Current State
⚠️ **No security implementation**

### Recommended Security Layers

```
┌─────────────────────────────────────────────────────────┐
│                    Security Layers                       │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  1. API Gateway / Load Balancer                          │
│     - SSL/TLS termination                                │
│     - Rate limiting                                       │
│     - DDoS protection                                     │
│                                                           │
│  2. Spring Security                                       │
│     - API key authentication                              │
│     - OAuth2/JWT (if needed)                              │
│     - CORS configuration                                  │
│                                                           │
│  3. Application Security                                  │
│     - Input validation                                    │
│     - Data sanitization                                   │
│     - Secure headers                                      │
│                                                           │
│  4. External API Security                                 │
│     - NASA API key (environment variable)                 │
│     - Secrets management (AWS Secrets Manager, etc.)      │
│     - Key rotation                                        │
│                                                           │
│  5. Network Security                                      │
│     - VPC/Private subnets                                 │
│     - Security groups                                     │
│     - Firewall rules                                      │
│                                                           │
└─────────────────────────────────────────────────────────┘
```

---

## Monitoring & Observability

### Recommended Metrics

```
Application Metrics:
- Request count (total, success, failure)
- Response time (p50, p95, p99)
- NASA API calls (count, latency, errors)
- Alert count (total, hazardous only)

Infrastructure Metrics:
- CPU usage
- Memory usage
- JVM heap usage
- Thread count

Business Metrics:
- Asteroids processed per day
- Hazardous asteroids detected
- Alerts sent
- API quota usage
```

### Recommended Tools
- **Spring Boot Actuator**: Health checks, metrics
- **Micrometer**: Metrics collection
- **Prometheus**: Metrics storage
- **Grafana**: Metrics visualization
- **ELK Stack**: Log aggregation

---

## Error Handling Strategy

### Error Types and Handling

```
┌─────────────────────────────────────────────────────────┐
│                  Error Handling Flow                     │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  Client Error (4xx)                                       │
│  ├─ Invalid date range → 400 Bad Request                 │
│  ├─ Unauthorized → 401 Unauthorized                       │
│  └─ Rate limit exceeded → 429 Too Many Requests          │
│                                                           │
│  Server Error (5xx)                                       │
│  ├─ NASA API timeout → Retry with exponential backoff    │
│  ├─ NASA API down → Circuit breaker, return cached data  │
│  ├─ Kafka publish error → Log and alert admin            │
│  └─ Unexpected error → 500, log stack trace              │
│                                                           │
│  Resilience Patterns                                      │
│  ├─ Retry (with exponential backoff)                     │
│  ├─ Circuit Breaker (fail fast)                          │
│  ├─ Timeout (prevent hanging)                            │
│  └─ Fallback (cached data or degraded service)           │
│                                                           │
└─────────────────────────────────────────────────────────┘
```

---

## Performance Considerations

### Scalability
- **Horizontal Scaling**: Stateless service, can run multiple instances
- **Caching**: Cache NASA API responses (TTL: 1 hour)
- **Async Processing**: Use Kafka for non-blocking alert distribution
- **Rate Limiting**: Respect NASA API limits (1000 requests/hour for standard key)

### Optimization Opportunities
1. **Response Caching**: Cache asteroid data to reduce API calls
2. **Connection Pooling**: Reuse HTTP connections to NASA API
3. **Batch Processing**: Process multiple dates in parallel
4. **Database**: Add persistence for historical data and analytics

---

## Testing Strategy

### Test Pyramid

```
              ┌─────────────┐
             /  E2E Tests    \     (Few - 10%)
            /  Integration    \
           /──────────────────\
          /   Component Tests  \   (Some - 30%)
         /──────────────────────\
        /      Unit Tests        \  (Most - 60%)
       /──────────────────────────\
      └──────────────────────────┘
```

### Test Coverage Goals
- **Unit Tests**: >80% coverage
- **Integration Tests**: All API endpoints, NASA client
- **Contract Tests**: NASA API response schema validation
- **Performance Tests**: Load testing for concurrent requests

---

## Development Guidelines

### Code Standards
- Follow Spring Boot best practices
- Use Lombok for reducing boilerplate
- Implement proper exception handling
- Add JavaDoc for public methods
- Follow REST API conventions

### Git Workflow
- Feature branches from main
- PR reviews required
- CI/CD pipeline for automated testing
- Semantic versioning

### Documentation
- Keep README up to date
- Document API endpoints (Swagger/OpenAPI)
- Maintain architecture diagrams
- Update changelog for releases

---

**Document Version**: 1.0  
**Last Updated**: February 17, 2026  
**Maintained By**: Development Team  
