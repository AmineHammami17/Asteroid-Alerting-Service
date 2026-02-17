# Asteroid Alerting Service

A Spring Boot microservice that monitors near-Earth objects (NEOs) using NASA's NeoWs API and sends alerts for potentially hazardous asteroids.

## 🚀 Project Status

**Current Phase**: 🏗️ **Early Development**

The project has a solid architectural foundation but requires implementation of core features. See [PROJECT_ANALYSIS.md](PROJECT_ANALYSIS.md) for a comprehensive analysis.

## 📋 Overview

This service retrieves asteroid data from NASA's Near Earth Object Web Service (NeoWs) API, analyzes the data to identify potentially hazardous asteroids, and publishes alerts through REST API and optionally via Apache Kafka.

## ✨ Features

### Implemented
- ✅ Spring Boot 3.5 application structure
- ✅ REST API endpoint for triggering alerts
- ✅ Maven build configuration
- ✅ Basic service layer architecture

### In Progress / Planned
- ⏳ NASA NeoWs API integration
- ⏳ Asteroid data model implementation
- ⏳ Alert filtering logic (hazardous asteroids)
- ⏳ Apache Kafka integration for event streaming
- ⏳ Comprehensive test coverage
- ⏳ Configuration management
- ⏳ Error handling and resilience

## 🛠️ Technology Stack

- **Java**: 17
- **Framework**: Spring Boot 3.5.0
- **Build Tool**: Maven 3.9+
- **Dependencies**:
  - Spring Web (REST APIs)
  - Spring Kafka (Message streaming)
  - Lombok (Code generation)
  - JUnit 5 (Testing)

## 📁 Project Structure

```
src/main/java/com/project/asteroidalert/
├── AsteroidalertApplication.java    # Application entry point
├── Controllers/                      # REST API endpoints
│   └── AsteroidAlertingController.java
├── Services/                         # Business logic
│   └── AsteroidAlertingService.java
├── Client/                          # External API clients
│   └── NasaClient.java
└── Entities/                        # Data models
    ├── Asteroid.java
    ├── CloseApproachData.java
    ├── EstimatedDiameter.java
    ├── DiameterRange.java
    └── MissDistance.java
```

## 🚦 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.9+ (or use the included wrapper)
- NASA API key ([Get one here](https://api.nasa.gov/))

### Installation

1. Clone the repository:
```bash
git clone https://github.com/AmineHammami17/Asteroid-Alerting-Service.git
cd Asteroid-Alerting-Service
```

2. Build the project:
```bash
./mvnw clean install
```

3. Configure your NASA API key (when implemented):
```bash
export NASA_API_KEY=your_api_key_here
```

4. Run the application:
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## 🔌 API Endpoints

### Trigger Asteroid Alert

```http
POST /api/v1/asteroid-alerting/alert
```

**Response**: `202 Accepted`

**Description**: Triggers the alert service to fetch asteroid data from NASA API for the next 7 days and process potentially hazardous asteroids.

**Example**:
```bash
curl -X POST http://localhost:8080/api/v1/asteroid-alerting/alert
```

## 🧪 Testing

Run all tests:
```bash
./mvnw test
```

**Note**: Tests currently fail due to incomplete implementation. See [PROJECT_ANALYSIS.md](PROJECT_ANALYSIS.md) for details.

## 📚 Documentation

- [PROJECT_ANALYSIS.md](PROJECT_ANALYSIS.md) - Comprehensive project analysis
- [ARCHITECTURE.md](ARCHITECTURE.md) - Technical architecture documentation

## 🗺️ Roadmap

### Phase 1: Core Functionality
- [ ] Implement NASA API client
- [ ] Complete entity models with JSON mapping
- [ ] Finish alert service logic
- [ ] Add unit and integration tests
- [ ] Configuration management

### Phase 2: Production Ready
- [ ] Error handling and resilience
- [ ] Logging and monitoring
- [ ] API documentation (Swagger)
- [ ] CI/CD pipeline

### Phase 3: Advanced Features
- [ ] Kafka integration for alert publishing
- [ ] Scheduled alert checks
- [ ] Data persistence layer
- [ ] Notification services (email/SMS)

## 🔒 Security

⚠️ **Important**: Never commit API keys or sensitive credentials to the repository.

Use environment variables for all sensitive configuration:
```bash
export NASA_API_KEY=your_key_here
```

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📝 License

This project is open source and available under the [MIT License](LICENSE).

## 📧 Contact

Project maintained by [AmineHammami17](https://github.com/AmineHammami17)

## 🙏 Acknowledgments

- NASA NeoWs API for providing asteroid data
- Spring Boot team for the excellent framework