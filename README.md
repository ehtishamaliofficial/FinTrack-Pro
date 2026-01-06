# FinTrack Pro

<div align="center">

**A Professional-Grade Personal Finance Management System**

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>

---

## 📖 About The Project

**FinTrack Pro** is a modern, enterprise-grade expense and budget tracking application designed to help users manage their personal finances effectively. Built with Spring Boot and following **Hexagonal Architecture (Ports & Adapters)** principles, FinTrack Pro offers a clean separation of concerns, making it highly maintainable, testable, and extensible.

### Why FinTrack Pro?

- **🏗️ Clean Architecture**: Hexagonal architecture ensures business logic independence from frameworks
- **🔐 Enterprise Security**: JWT-based authentication with OAuth2 support
- **💰 Multi-Wallet Support**: Manage multiple accounts (Cash, Bank, Credit Card, Investment)
- **📊 Advanced Analytics**: Detailed insights into spending patterns and financial health
- **🚀 Production Ready**: Docker support, comprehensive testing, and monitoring
- **📱 API First**: RESTful API with full OpenAPI/Swagger documentation

---

## ✨ Key Features

### 💳 Wallet Management

- Create and manage multiple wallets (Cash, Bank Account, Credit Card, Investment, Digital Wallet)
- Real-time balance synchronization with transactions
- Multi-currency support
- Set default wallets for quick transactions
- Transfer funds between wallets

### 📝 Transaction Management

- Record income and expense transactions
- Automatic wallet balance updates
- Category-based transaction classification
- Advanced filtering and search capabilities
- Bulk import from CSV
- Transaction attachments (receipts, bills)

### 🏷️ Category System

- Pre-defined income and expense categories
- Create custom categories with icons and colors
- Category-wise spending analysis
- Protected system categories

### 📊 Analytics & Insights

- Monthly income vs expense summaries
- Category-wise spending breakdown
- Spending trends and patterns
- Cash flow analysis
- Savings rate calculation
- Net worth tracking
- Month-over-month comparisons

### 📄 Report Generation

- Generate PDF reports with charts and visualizations
- Export detailed Excel spreadsheets
- Customizable date ranges and filters
- Email delivery of reports

### 🔐 Security

- JWT-based authentication (Access + Refresh tokens)
- OAuth2 social login (Google)
- Email verification workflow
- Password strength validation
- Rate limiting and account lockout
- Comprehensive audit logging

---

## 🏛️ Architecture

FinTrack Pro follows **Hexagonal Architecture** (also known as Ports and Adapters pattern) for a clean separation between business logic and external dependencies.

```
┌─────────────────────────────────────────────────────────────┐
│                     Infrastructure Layer                      │
│  (REST Controllers, JPA Repositories, External Services)     │
└────────────────┬────────────────────────────┬────────────────┘
                 │                            │
                 ▼                            ▼
         ┌───────────────┐          ┌──────────────────┐
         │  Input Ports  │          │  Output Ports    │
         │  (Use Cases)  │          │  (Repositories)  │
         └───────┬───────┘          └────────┬─────────┘
                 │                            │
                 ▼                            ▼
         ┌───────────────────────────────────────────┐
         │          Application Layer                 │
         │     (Business Logic Implementation)       │
         └───────────────────────────────────────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │    Domain Layer      │
                 │  (Entities, Value    │
                 │   Objects, Rules)    │
                 └─────────────────────┘
```

### Layer Responsibilities

- **Domain Layer**: Contains business entities, value objects, and core business rules
- **Application Layer**: Implements use cases and orchestrates business logic
- **Infrastructure Layer**: Handles external concerns (database, REST API, email, caching)

---

## 🗂️ Project Structure

```
src/main/java/com/fintrackpro/
│
├── domain/                          # Domain Layer (Core Business Logic)
│   ├── model/                       # Domain Entities (User, Wallet, Transaction, Category)
│   ├── valueobject/                 # Value Objects (TransactionType, WalletType, etc.)
│   ├── exception/                   # Domain-specific Exceptions
│   └── port/output/                 # Output Port Interfaces (Repository contracts)
│
├── application/                     # Application Layer (Use Cases)
│   ├── port/input/                  # Input Ports (Use Case Interfaces)
│   └── service/                     # Use Case Implementations (Business Logic)
│
└── infrastructure/                  # Infrastructure Layer (Adapters)
    ├── adapter/
    │   ├── input/rest/              # REST Controllers
    │   ├── input/dto/               # Request/Response DTOs
    │   ├── output/persistence/      # JPA Entities & Repositories
    │   └── output/mapper/           # Entity-Model Mappers
    ├── config/                      # Configuration (Security, Swagger, etc.)
    └── security/                    # JWT, OAuth2, Security Filters
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 17+** (OpenJDK or Oracle JDK)
- **Maven 3.8+**
- **PostgreSQL 14+**
- **Redis 7+** (Optional, for caching)
- **Docker & Docker Compose** (Optional, for containerized deployment)

### Installation

#### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/fintrack-pro.git
cd fintrack-pro
```

#### 2. Configure Database

Create a PostgreSQL database:

```sql
CREATE DATABASE fintrack_db;
CREATE USER fintrack_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE fintrack_db TO fintrack_user;
```

#### 3. Configure Application Properties

Create or update `src/main/resources/application.properties`:

```properties
# Database Configuration
${SPRING_DATASOURCE_URL}
${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=your_password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# JWT Configuration
jwt.secret=your-256-bit-secret-key-change-this-in-production
jwt.access-token-expiration=900000
jwt.refresh-token-expiration=604800000

# OAuth2 Configuration (Optional)
spring.security.oauth2.client.registration.google.client-id=your-google-client-id
spring.security.oauth2.client.registration.google.client-secret=your-google-client-secret

# Server Port
server.port=8080
```

> **⚠️ Security Note**: Never commit sensitive credentials to Git. Use environment variables or external configuration in production.

#### 4. Generate RSA Keys for JWT (Optional)

If using RSA-based JWT signing:

```bash
# Generate private key
openssl genrsa -out app.key 2048

# Generate public key
openssl rsa -in app.key -pubout -out app.pub

# Move to resources
mv app.key app.pub src/main/resources/
```

#### 5. Build the Project

```bash
mvn clean install
```

#### 6. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

---

## 🐳 Docker Deployment

### Using Docker Compose (Recommended)

```bash
# Start all services (App + PostgreSQL + Redis)
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Manual Docker Build

```bash
# Build the image
docker build -t fintrack-pro:latest .

# Run the container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/fintrack_db \
  -e SPRING_DATASOURCE_USERNAME=fintrack_user \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  fintrack-pro:latest
```

---

## 📚 API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Sample API Endpoints

#### Authentication

```
POST   /api/v1/auth/register          - Register new user
POST   /api/v1/auth/login             - Login and get JWT tokens
POST   /api/v1/auth/refresh           - Refresh access token
POST   /api/v1/auth/logout            - Logout
```

#### Wallets

```
GET    /api/v1/wallets                - Get all wallets
POST   /api/v1/wallets                - Create new wallet
GET    /api/v1/wallets/{id}           - Get wallet details
PUT    /api/v1/wallets/{id}           - Update wallet
DELETE /api/v1/wallets/{id}           - Delete wallet
```

#### Transactions

```
GET    /api/v1/transactions           - Get all transactions (paginated)
POST   /api/v1/transactions           - Create transaction
PUT    /api/v1/transactions/{id}      - Update transaction
DELETE /api/v1/transactions/{id}      - Delete transaction
GET    /api/v1/transactions/search    - Search transactions
```

#### Categories

```
GET    /api/v1/categories             - Get all categories
POST   /api/v1/categories             - Create custom category
PUT    /api/v1/categories/{id}        - Update category
DELETE /api/v1/categories/{id}        - Delete category
```

#### Analytics

```
GET    /api/v1/analytics/summary      - Get financial summary
GET    /api/v1/analytics/trends       - Get spending trends
GET    /api/v1/analytics/category-breakdown - Category-wise analysis
GET    /api/v1/analytics/net-worth    - Calculate net worth
```

---

## 🔧 Configuration

### Environment Variables

| Variable                       | Description                   | Default                                        |
| ------------------------------ | ----------------------------- | ---------------------------------------------- |
| `SPRING_DATASOURCE_URL`        | PostgreSQL connection URL     | `jdbc:postgresql://localhost:5432/fintrack_db` |
| `SPRING_DATASOURCE_USERNAME`   | Database username             | `fintrack_user`                                |
| `SPRING_DATASOURCE_PASSWORD`   | Database password             | -                                              |
| `JWT_SECRET`                   | JWT signing secret key        | -                                              |
| `JWT_ACCESS_TOKEN_EXPIRATION`  | Access token expiration (ms)  | `900000` (15 min)                              |
| `JWT_REFRESH_TOKEN_EXPIRATION` | Refresh token expiration (ms) | `604800000` (7 days)                           |
| `REDIS_HOST`                   | Redis server host             | `localhost`                                    |
| `REDIS_PORT`                   | Redis server port             | `6379`                                         |
| `ALLOWED_ORIGINS`              | CORS allowed origins          | `http://localhost:3000`                        |

---

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run Specific Test Suites

```bash
# Unit tests only
mvn test -Dtest=*Test

# Integration tests
mvn test -Dtest=*IT

# Security tests
mvn test -Dtest=*SecurityTest
```

### Code Coverage

```bash
# Generate coverage report with JaCoCo
mvn clean test jacoco:report

# View report at: target/site/jacoco/index.html
```

---

## 🛠️ Tech Stack

### Core Framework

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
- **Spring Cache**

### Database

- **PostgreSQL 14+** - Primary database
- **Redis 7+** - Caching & rate limiting

### Security

- **JJWT 0.12.3** - JWT authentication
- **BCrypt** - Password hashing
- **Passay** - Password validation
- **Bucket4j** - Rate limiting

### Utilities

- **MapStruct** - DTO mapping
- **Lombok** - Reduce boilerplate
- **Apache Commons** - Utilities

### Documentation & Monitoring

- **SpringDoc OpenAPI** - API documentation
- **Flyway** - Database migrations
- **Micrometer** - Metrics

### Testing

- **JUnit 5** - Unit testing
- **Mockito** - Mocking
- **Testcontainers** - Integration testing

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Style

- Follow Java Code Conventions
- Use meaningful variable and method names
- Write unit tests for new features
- Update documentation as needed

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 📧 Contact

**Project Maintainer**: Your Name

- Email: your.email@example.com
- GitHub: [@yourusername](https://github.com/yourusername)
- LinkedIn: [Your Profile](https://linkedin.com/in/yourprofile)

---

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- PostgreSQL community
- All contributors and users of FinTrack Pro

---

## 📈 Roadmap

- [x] Core wallet and transaction management
- [x] Analytics and insights
- [x] JWT authentication
- [x] OAuth2 social login
- [ ] Mobile app (React Native)
- [ ] Recurring transactions
- [ ] Budget planning and alerts
- [ ] Multi-user support (family accounts)
- [ ] Bill reminders and notifications
- [ ] Investment portfolio tracking
- [ ] Tax report generation

---

<div align="center">

**Made with ❤️ using Spring Boot**

⭐ Star this repo if you find it useful!

</div>
