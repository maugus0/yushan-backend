# Yushan Backend

> 📚 **Backend API for Yushan** - A gamified web novel reading platform that transforms reading into an engaging, social experience.

## 🚀 Tech Stack

- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **Database**: PostgreSQL (Production) | H2 (Development)
- **Build Tool**: Maven
- **Security**: Spring Security with JWT
- **Testing**: JUnit 5, Testcontainers

## ✨ Key Features

### 📖 Core Platform
- Novel management and chapter organization
- User authentication and authorization
- Full-text search across novels and content
- Reading progress tracking
- Bookmarks and favorites system

### 🎮 Gamification
- XP and leveling system
- Achievement and badge system
- Reading streaks and milestones
- Leaderboards and competitions
- Social features (following, reviews)

### 🔧 Technical Features
- RESTful API design
- JWT-based authentication
- Database migration support
- Comprehensive error handling
- API documentation with OpenAPI
- Caching for performance optimization

## 🏗️ Project Structure

```
com.yushan.backend/
├── controller/          # REST API endpoints
├── service/            # Business logic layer
├── repository/         # Data access layer
├── entity/            # JPA entities (database models)
├── dto/               # Data Transfer Objects
├── config/            # Application configuration
└── exception/         # Custom exception handling
```

## 🚦 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL (for production)

### Quick Setup
```bash
# Clone the repository
git clone https://github.com/your-username/yushan-backend.git
cd yushan-backend

# Run with H2 database (development)
./mvnw spring-boot:run

# Run tests
./mvnw test
```

### Database Configuration
```properties
# Development (H2 - auto-configured)
spring.profiles.active=dev

# Production (PostgreSQL)
spring.profiles.active=prod
spring.datasource.url=jdbc:postgresql://localhost:5432/yushan
```

## 📡 API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Token refresh

### Novels
- `GET /api/novels` - List novels
- `POST /api/novels` - Create novel
- `GET /api/novels/{id}` - Get novel details
- `GET /api/novels/{id}/chapters` - Get chapters

### User & Gamification
- `GET /api/users/profile` - User profile
- `GET /api/users/progress` - Reading progress
- `GET /api/leaderboard` - User rankings

## 🧪 Development

### Running Tests
```bash
# Unit tests
./mvnw test

# Integration tests with Testcontainers
./mvnw verify
```

### Database Migration
```bash
# Generate migration scripts
./mvnw flyway:migrate
```

## 🛠️ Built With

- [Spring Boot](https://spring.io/projects/spring-boot) - Application framework
- [Spring Security](https://spring.io/projects/spring-security) - Authentication & authorization
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa) - Data persistence
- [PostgreSQL](https://www.postgresql.org/) - Primary database
- [H2 Database](https://www.h2database.com/) - Development database
- [Maven](https://maven.apache.org/) - Dependency management

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🌟 Roadmap

- [ ] Advanced search with filters
- [ ] Real-time notifications
- [ ] Social features (comments, discussions)
- [ ] Mobile app API support
- [ ] Advanced analytics dashboard
- [ ] Multi-language support

---

**Yushan Backend** - Powering the future of gamified reading experiences 🚀
