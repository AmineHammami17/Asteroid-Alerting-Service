# Quick Start Guide - Asteroid Alerting Service

This guide helps developers quickly understand and start working on the Asteroid Alerting Service.

## 🎯 What Does This Project Do?

Monitors near-Earth asteroids using NASA's API and alerts about potentially hazardous objects approaching Earth.

## 📊 Current Status

| Component | Status | Priority |
|-----------|--------|----------|
| Entity Models | ❌ Empty | P0 - Critical |
| NASA API Client | ❌ Empty | P0 - Critical |
| Service Logic | 🟡 Partial | P0 - Critical |
| REST Controller | ✅ Done | Complete |
| Tests | ❌ Failing | P0 - Critical |
| Configuration | ❌ Missing | P1 - High |
| Kafka Integration | ❌ Not Started | P2 - Medium |

## 🏃 Quick Commands

```bash
# Build project
./mvnw clean compile

# Run tests
./mvnw test

# Run application
./mvnw spring-boot:run

# Package application
./mvnw clean package

# Run packaged JAR
java -jar target/asteroidalert-0.0.1-SNAPSHOT.jar
```

## 🐛 Known Issues

### 1. Test Failure
**Issue**: `AsteroidalertApplicationTests.contextLoads()` fails  
**Cause**: `NasaClient` missing Spring annotation  
**Fix**: Add `@Component` to `NasaClient.java`

```java
@Component
public class NasaClient {
    // implementation
}
```

### 2. Empty Implementations
**Issue**: All entity classes and NASA client are empty  
**Impact**: Core functionality not working  
**Priority**: P0 - Must fix before MVP

## 🔨 What Needs to Be Built?

### 1. Entity Models (2-4 hours)

Add to `Asteroid.java`:
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asteroid {
    private String id;
    private String name;
    
    @JsonProperty("absolute_magnitude_h")
    private Double absoluteMagnitudeH;
    
    @JsonProperty("is_potentially_hazardous_asteroid")
    private Boolean isPotentiallyHazardous;
    
    @JsonProperty("estimated_diameter")
    private EstimatedDiameter estimatedDiameter;
    
    @JsonProperty("close_approach_data")
    private List<CloseApproachData> closeApproachData;
}
```

Similar implementations needed for:
- `EstimatedDiameter.java`
- `DiameterRange.java`
- `CloseApproachData.java`
- `MissDistance.java`

### 2. NASA API Client (4-6 hours)

Add to `NasaClient.java`:
```java
@Component
@Slf4j
public class NasaClient {
    
    @Value("${nasa.api.url}")
    private String apiUrl;
    
    @Value("${nasa.api.key}")
    private String apiKey;
    
    private final RestTemplate restTemplate;
    
    public NasaClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }
    
    public Map<LocalDate, List<Asteroid>> getAsteroids(LocalDate startDate, LocalDate endDate) {
        String url = String.format("%s/feed?start_date=%s&end_date=%s&api_key=%s",
            apiUrl, startDate, endDate, apiKey);
        // Implementation here
    }
}
```

### 3. Configuration (2-3 hours)

Add to `application.properties`:
```properties
# NASA API
nasa.api.url=https://api.nasa.gov/neo/rest/v1
nasa.api.key=${NASA_API_KEY:DEMO_KEY}

# Alert thresholds
asteroid.alert.diameter.min.km=0.5
asteroid.alert.distance.max.km=10000000

# Logging
logging.level.com.project.asteroidalert=DEBUG
```

### 4. Service Logic (3-4 hours)

Complete `AsteroidAlertingService.java`:
```java
public void alert() {
    log.info("Alerting service called");
    
    final LocalDate fromDate = LocalDate.now();
    final LocalDate toDate = LocalDate.now().plusDays(7);
    
    log.info("Getting asteroid list for dates {} to {}", fromDate, toDate);
    
    // 1. Fetch data from NASA
    Map<LocalDate, List<Asteroid>> asteroidsByDate = nasaClient.getAsteroids(fromDate, toDate);
    
    // 2. Filter hazardous asteroids
    List<Asteroid> hazardousAsteroids = asteroidsByDate.values().stream()
        .flatMap(Collection::stream)
        .filter(Asteroid::getIsPotentiallyHazardous)
        .collect(Collectors.toList());
    
    // 3. Log/publish alerts
    log.info("Found {} potentially hazardous asteroids", hazardousAsteroids.size());
    hazardousAsteroids.forEach(asteroid -> 
        log.warn("ALERT: Hazardous asteroid {} approaching on {}", 
            asteroid.getName(), 
            asteroid.getCloseApproachData().get(0).getCloseApproachDate())
    );
}
```

## 📝 Development Workflow

### 1. Pick a Task
Start with P0 tasks from the roadmap

### 2. Create Branch
```bash
git checkout -b feature/implement-entities
```

### 3. Make Changes
Implement one component at a time

### 4. Test Locally
```bash
./mvnw clean test
```

### 5. Commit & Push
```bash
git add .
git commit -m "Implement Asteroid entity with JSON mapping"
git push origin feature/implement-entities
```

### 6. Create PR
Submit PR for review

## 🧪 Testing Strategy

### Unit Tests
Test each component in isolation:
```java
@Test
void testFilterHazardousAsteroids() {
    // Test service filtering logic
}
```

### Integration Tests
Test NASA API integration:
```java
@SpringBootTest
@Test
void testNasaClientIntegration() {
    // Test real API call (with test key)
}
```

### Mock Tests
Mock external dependencies:
```java
@MockBean
private NasaClient nasaClient;
```

## 🌐 API Resources

### NASA NeoWs API
- **Documentation**: https://api.nasa.gov/
- **Endpoint**: `https://api.nasa.gov/neo/rest/v1/feed`
- **Get API Key**: https://api.nasa.gov/ (free, instant)
- **Rate Limit**: 1000 requests/hour (standard key)

### Example API Call
```bash
curl "https://api.nasa.gov/neo/rest/v1/feed?start_date=2026-02-17&end_date=2026-02-24&api_key=DEMO_KEY"
```

### Example Response
```json
{
  "element_count": 25,
  "near_earth_objects": {
    "2026-02-17": [
      {
        "id": "2021277",
        "name": "(2021277) 1996 TO5",
        "absolute_magnitude_h": 16.73,
        "is_potentially_hazardous_asteroid": true,
        "estimated_diameter": {
          "kilometers": {
            "estimated_diameter_min": 1.6284579489,
            "estimated_diameter_max": 3.6411658781
          }
        },
        "close_approach_data": [...]
      }
    ]
  }
}
```

## 🔧 Debugging Tips

### Check Spring Context
```bash
./mvnw spring-boot:run -Ddebug
```

### View Actuator Endpoints (when added)
```bash
curl http://localhost:8080/actuator/health
```

### Enable SQL Logging (when DB added)
```properties
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG
```

### Test REST Endpoint
```bash
# Using curl
curl -X POST http://localhost:8080/api/v1/asteroid-alerting/alert -v

# Using HTTPie
http POST :8080/api/v1/asteroid-alerting/alert
```

## 📦 Dependencies to Know

### Spring Web
- REST controllers
- HTTP client (RestTemplate, WebClient)
- JSON serialization (Jackson)

### Lombok
- `@Data` - Generates getters, setters, toString, equals, hashCode
- `@Slf4j` - Generates logger
- `@NoArgsConstructor` / `@AllArgsConstructor` - Constructors
- `@Builder` - Builder pattern

### Spring Kafka (Future)
- Kafka producer/consumer
- JSON serialization
- Topic management

## 🎓 Learning Resources

### Spring Boot
- [Official Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Building REST Services](https://spring.io/guides/tutorials/rest/)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)

### NASA API
- [API Portal](https://api.nasa.gov/)
- [NeoWs Documentation](https://github.com/SpaceRocks/NeoWs)

### Kafka (for future reference)
- [Spring Kafka](https://docs.spring.io/spring-kafka/reference/)
- [Kafka Quickstart](https://kafka.apache.org/quickstart)

## ❓ FAQ

**Q: Why are tests failing?**  
A: NasaClient needs `@Component` annotation. It's not registered as a Spring bean.

**Q: Where do I get a NASA API key?**  
A: Visit https://api.nasa.gov/ - it's free and instant. Use `DEMO_KEY` for testing (rate limited).

**Q: What's the MVP scope?**  
A: REST endpoint that fetches asteroids from NASA, filters hazardous ones, and logs them.

**Q: When will Kafka be integrated?**  
A: Phase 3, after core functionality is complete. Not required for MVP.

**Q: Can I run this in Docker?**  
A: Not yet. Dockerfile will be added in Phase 2.

**Q: How do I contribute?**  
A: Fork the repo, create a feature branch, make changes, submit PR.

## 🚨 Before You Commit

- [ ] Code compiles (`./mvnw compile`)
- [ ] Tests pass (`./mvnw test`)
- [ ] Code formatted properly
- [ ] No sensitive data (API keys, passwords)
- [ ] Updated relevant documentation
- [ ] Commit message is clear and descriptive

## 📞 Getting Help

1. Check [PROJECT_ANALYSIS.md](PROJECT_ANALYSIS.md) for detailed analysis
2. Check [ARCHITECTURE.md](ARCHITECTURE.md) for architecture details
3. Review NASA API documentation
4. Open an issue on GitHub
5. Contact maintainer

---

**Last Updated**: February 17, 2026  
**Quick Start Version**: 1.0  

Happy coding! 🚀
