# Expense & Budget Tracker - Backend

A professional-grade expense and budget tracking application built with Spring Boot using **Hexagonal Architecture (Ports & Adapters)** for maximum maintainability, testability, and flexibility.

## 🏗️ Architecture

This project follows **Hexagonal Architecture** principles:

```
┌─────────────────────────────────────────────────────────────┐
│                     Infrastructure Layer                      │
│  (REST Controllers, JPA Repositories, External Services)     │
└────────────────┬────────────────────────────┬────────────────┘
                 │                            │
                 ▼                            ▼
         ┌───────────────┐          ┌──────────────────┐
         │  Input Ports  │          │  Output Ports    │
         │  (Interfaces) │          │  (Interfaces)    │
         └───────┬───────┘          └────────┬─────────┘
                 │                            │
                 ▼                            ▼
         ┌───────────────────────────────────────────┐
         │          Application Layer                 │
         │     (Use Cases / Business Logic)          │
         └───────────────────────────────────────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │    Domain Layer      │
                 │  (Entities, Value    │
                 │   Objects, Rules)    │
                 └─────────────────────┘
```

## 📋 Features

### Core Functionality
- ✅ Multi-wallet/account management (Cash, Bank, Credit Card)
- ✅ Income & expense tracking with categorization
- ✅ Customizable income/expense categories
- ✅ Monthly/weekly/yearly analysis with visual charts
- ✅ PDF/Excel report generation with charts
- ✅ JWT-based authentication & authorization
- ✅ RESTful API with proper DTOs
- ✅ Email verification & password reset
- ✅ Transaction search and filtering

### Technical Highlights
- 🏛️ Hexagonal Architecture (Clean Architecture)
- 🔐 Spring Security with JWT (Access + Refresh Tokens)
- 📊 Data visualization endpoints for charts
- 📄 Document generation (PDF/Excel with JFreeChart)
- 🗄️ PostgresSQL with JPA/Hibernate
- ⚡ Redis caching for performance
- 🚦 Rate limiting with Bucket4j
- 🐳 Docker & Docker Compose ready
- 📝 Comprehensive API documentation (Swagger/OpenAPI)
- ✅ Unit & Integration tests with Testcontainers
- 🔍 Code coverage with JaCoCo (70% minimum)
- 📧 Email notifications (verification, password reset)

## 🗂️ Project Structure

```
src/main/java/com/yourname/expensetracker/
│
├── domain/                          # Domain Layer (Business Logic Core)
│   ├── model/                       # Domain Entities
│   │   ├── User.java
│   │   ├── Wallet.java
│   │   ├── Transaction.java
│   │   ├── Category.java
│   │   └── Budget.java
│   │
│   ├── valueobject/                 # Value Objects
│   │   ├── Money.java
│   │   ├── TransactionType.java
│   │   ├── Period.java
│   │   └── Currency.java
│   │
│   ├── exception/                   # Domain Exceptions
│   │   ├── WalletNotFoundException.java
│   │   ├── InsufficientFundsException.java
│   │   ├── InvalidTransactionException.java
│   │   └── CategoryNotFoundException.java
│   │
│   └── service/                     # Domain Services
│       ├── BalanceCalculator.java
│       └── TransactionValidator.java
│
├── application/                     # Application Layer (Use Cases)
│   ├── port/
│   │   ├── input/                   # Input Ports (Use Case Interfaces)
│   │   │   ├── CreateWalletUseCase.java
│   │   │   ├── RecordTransactionUseCase.java
│   │   │   ├── GetAnalyticsUseCase.java
│   │   │   ├── GenerateReportUseCase.java
│   │   │   ├── ManageCategoryUseCase.java
│   │   │   └── ManageUserUseCase.java
│   │   │
│   │   └── output/                  # Output Ports (Repository Interfaces)
│   │       ├── WalletRepository.java
│   │       ├── TransactionRepository.java
│   │       ├── CategoryRepository.java
│   │       ├── UserRepository.java
│   │       ├── RefreshTokenRepository.java
│   │       ├── ReportGenerator.java
│   │       ├── CacheRepository.java
│   │       └── EmailService.java
│   │
│   └── service/                     # Use Case Implementations
│       ├── WalletService.java
│       ├── TransactionService.java
│       ├── AnalyticsService.java
│       ├── ReportService.java
│       ├── CategoryService.java
│       └── AuthenticationService.java
│
└── infrastructure/                  # Infrastructure Layer (Adapters)
    ├── adapter/
    │   ├── input/                   # Input Adapters
    │   │   ├── rest/                # REST Controllers
    │   │   │   ├── AuthController.java
    │   │   │   ├── WalletController.java
    │   │   │   ├── TransactionController.java
    │   │   │   ├── CategoryController.java
    │   │   │   ├── AnalyticsController.java
    │   │   │   └── ReportController.java
    │   │   │
    │   │   └── dto/                 # Request/Response DTOs
    │   │       ├── request/
    │   │       │   ├── RegisterRequest.java
    │   │       │   ├── LoginRequest.java
    │   │       │   ├── CreateWalletRequest.java
    │   │       │   ├── TransactionRequest.java
    │   │       │   ├── CategoryRequest.java
    │   │       │   └── ReportRequest.java
    │   │       └── response/
    │   │           ├── AuthResponse.java
    │   │           ├── WalletResponse.java
    │   │           ├── TransactionResponse.java
    │   │           ├── AnalyticsResponse.java
    │   │           ├── CategoryResponse.java
    │   │           └── ReportResponse.java
    │   │
    │   └── output/                  # Output Adapters
    │       ├── persistence/         # Database Adapters
    │       │   ├── entity/          # JPA Entities
    │       │   │   ├── UserEntity.java
    │       │   │   ├── WalletEntity.java
    │       │   │   ├── TransactionEntity.java
    │       │   │   ├── CategoryEntity.java
    │       │   │   └── RefreshTokenEntity.java
    │       │   │
    │       │   ├── repository/      # JPA Repositories
    │       │   │   ├── JpaUserRepository.java
    │       │   │   ├── JpaWalletRepository.java
    │       │   │   ├── JpaTransactionRepository.java
    │       │   │   ├── JpaCategoryRepository.java
    │       │   │   └── JpaRefreshTokenRepository.java
    │       │   │
    │       │   └── adapter/         # Repository Adapters
    │       │       ├── UserRepositoryAdapter.java
    │       │       ├── WalletRepositoryAdapter.java
    │       │       ├── TransactionRepositoryAdapter.java
    │       │       ├── CategoryRepositoryAdapter.java
    │       │       └── RefreshTokenRepositoryAdapter.java
    │       │
    │       ├── cache/               # Redis Cache Adapter
    │       │   └── RedisCacheAdapter.java
    │       │
    │       ├── report/              # Report Generation Adapters
    │       │   ├── PdfReportGenerator.java
    │       │   └── ExcelReportGenerator.java
    │       │
    │       └── email/               # Email Service Adapter
    │           └── SmtpEmailService.java
    │
    ├── config/                      # Configuration Classes
    │   ├── SecurityConfig.java
    │   ├── JwtConfig.java
    │   ├── RedisConfig.java
    │   ├── SwaggerConfig.java
    │   ├── MapperConfig.java
    │   ├── CorsConfig.java
    │   └── RateLimitConfig.java
    │
    ├── security/                    # Security Components
    │   ├── JwtTokenProvider.java
    │   ├── JwtAuthenticationFilter.java
    │   ├── JwtAuthenticationEntryPoint.java
    │   ├── JwtAccessDeniedHandler.java
    │   ├── UserDetailsServiceImpl.java
    │   ├── PasswordValidator.java
    │   └── RateLimitingFilter.java
    │
    ├── exception/                   # Global Exception Handling
    │   ├── GlobalExceptionHandler.java
    │   ├── ErrorResponse.java
    │   └── ApiException.java
    │
    └── mapper/                      # MapStruct Mappers
        ├── UserMapper.java
        ├── WalletMapper.java
        ├── TransactionMapper.java
        └── CategoryMapper.java
```

## 🎯 Modules Breakdown

### Module 1: User Management & Authentication
**Duration: Week 1**

**Features:**
- User registration with email validation
- Login with JWT token generation (access + refresh)
- Token refresh mechanism
- User profile management (view/update)
- Password reset functionality
- Email verification workflow
- Account lockout after failed attempts

**Components:**
- `AuthController` - Authentication endpoints
- `AuthenticationService` - User authentication business logic
- `JwtTokenProvider` - Token generation/validation
- `SecurityConfig` - Spring Security configuration
- `RateLimitingFilter` - Rate limiting on login endpoint

**Endpoints:**
```
POST   /api/v1/auth/register          - Register new user
POST   /api/v1/auth/login             - Login and get tokens
POST   /api/v1/auth/refresh           - Refresh access token
POST   /api/v1/auth/logout            - Logout (revoke refresh token)
POST   /api/v1/auth/verify-email      - Verify email with token
POST   /api/v1/auth/forgot-password   - Request password reset
POST   /api/v1/auth/reset-password    - Reset password with token
GET    /api/v1/users/profile          - Get user profile
PUT    /api/v1/users/profile          - Update user profile
PUT    /api/v1/users/change-password  - Change password
```

**Security:**
- BCrypt password hashing (strength: 12)
- JWT access token: 15-30 minutes
- JWT refresh token: 7 days (stored in DB)
- Rate limiting: 5 login attempts per 15 minutes per IP
- Account lockout: 30 minutes after 5 failed attempts
- Email verification required before full access

---

### Module 2: Wallet Management
**Duration: Week 1-2**

**Features:**
- Create multiple wallets with different types (Cash, Bank Account, Credit Card, Investment, Savings)
- Edit wallet details (name, currency, color, icon)
- Delete wallets (with transaction cleanup confirmation)
- Set default wallet for quick transactions
- View wallet balance, transaction count, and statistics
- Transfer money between wallets
- Multi-currency support

**Components:**
- `WalletController` - Wallet REST endpoints
- `CreateWalletUseCase` - Business logic for wallet creation
- `WalletService` - Use case implementation
- `WalletRepositoryAdapter` - Database operations
- `BalanceCalculator` - Domain service for balance calculations

**Endpoints:**
```
POST   /api/v1/wallets                     - Create new wallet
GET    /api/v1/wallets                     - Get all user wallets
GET    /api/v1/wallets/{id}                - Get wallet details
PUT    /api/v1/wallets/{id}                - Update wallet
DELETE /api/v1/wallets/{id}                - Delete wallet
PUT    /api/v1/wallets/{id}/set-default    - Set as default wallet
GET    /api/v1/wallets/{id}/balance        - Get wallet balance
GET    /api/v1/wallets/{id}/transactions   - Get wallet transactions
POST   /api/v1/wallets/transfer            - Transfer between wallets
```

**Business Rules:**
- Wallet name must be unique per user
- Cannot delete wallet with transactions (must archive or transfer first)
- Default wallet automatically selected for quick transactions
- Balance calculated from all transactions
- Support for negative balance (credit cards, loans)

---

### Module 3: Category Management
**Duration: Week 2**

**Features:**
- Pre-defined income categories (Salary, Business, Investment, Gift, Refund, Other)
- Pre-defined expense categories (Food & Dining, Transportation, Shopping, Bills & Utilities, Entertainment, Healthcare, Education, Travel, Personal Care, Other)
- Create custom categories
- Edit user-created categories (name, icon, color, type)
- Delete user-created categories (system categories cannot be deleted)
- Category icons and color coding
- Category statistics (total spent/earned, transaction count)

**Components:**
- `CategoryController` - Category endpoints
- `ManageCategoryUseCase` - Category business logic
- `CategoryService` - Use case implementation
- `CategoryRepositoryAdapter` - Database operations

**Endpoints:**
```
GET    /api/v1/categories                  - Get all categories
GET    /api/v1/categories/income           - Get income categories
GET    /api/v1/categories/expense          - Get expense categories
GET    /api/v1/categories/{id}             - Get category details
POST   /api/v1/categories                  - Create custom category
PUT    /api/v1/categories/{id}             - Update category
DELETE /api/v1/categories/{id}             - Delete category
GET    /api/v1/categories/{id}/stats       - Get category statistics
```

**Business Rules:**
- System categories are read-only
- Custom categories unique per user
- Category type (INCOME/EXPENSE) cannot be changed after creation
- Cannot delete category with transactions (must reassign first)
- Maximum 50 custom categories per user

---

### Module 4: Transaction Management
**Duration: Week 2-3**

**Features:**
- Record income transactions
- Record expense transactions
- Edit transaction details (amount, category, wallet, date, note)
- Delete transactions (with balance recalculation)
- Filter transactions by date range, category, wallet, type
- Search transactions by description/note
- Pagination support for large transaction lists
- Bulk import from CSV
- Transaction attachments (receipts, bills)
- Recurring transactions (future enhancement)

**Components:**
- `TransactionController` - Transaction endpoints
- `RecordTransactionUseCase` - Transaction business logic
- `TransactionService` - Use case implementation
- `TransactionRepositoryAdapter` - Database operations
- `BalanceCalculator` - Domain service for balance calculations
- `TransactionValidator` - Domain service for validation

**Endpoints:**
```
POST   /api/v1/transactions                          - Create transaction
GET    /api/v1/transactions                          - Get all transactions (paginated)
GET    /api/v1/transactions/{id}                     - Get transaction details
PUT    /api/v1/transactions/{id}                     - Update transaction
DELETE /api/v1/transactions/{id}                     - Delete transaction
GET    /api/v1/transactions/search?q=coffee          - Search transactions
GET    /api/v1/transactions/filter?startDate=...     - Filter transactions
POST   /api/v1/transactions/bulk-import              - Import from CSV
GET    /api/v1/transactions/recent?limit=10          - Get recent transactions
GET    /api/v1/transactions/summary?period=month     - Get transaction summary
```

**Query Parameters for Filtering:**
```
?startDate=2024-01-01
&endDate=2024-01-31
&categoryId=123
&walletId=456
&type=EXPENSE
&minAmount=100
&maxAmount=1000
&page=0
&size=20
&sort=date,desc
```

**Business Rules:**
- Amount must be positive
- Transaction date cannot be in future (configurable)
- Category type must match transaction type
- Wallet balance updated automatically
- Cannot delete transaction if wallet is locked
- Audit trail maintained for all changes

---

### Module 5: Analytics & Insights
**Duration: Week 3-4**

**Features:**
- Monthly income vs expense summary
- Weekly spending trends
- Category-wise breakdown (pie chart data)
- Top spending categories (last 30 days, 90 days, year)
- Income sources analysis
- Cash flow analysis (inflow vs outflow)
- Spending patterns by day of week
- Month-over-month comparison
- Year-over-year comparison
- Budget vs actual spending
- Savings rate calculation
- Net worth tracking

**Components:**
- `AnalyticsController` - Analytics endpoints
- `GetAnalyticsUseCase` - Analytics business logic
- `AnalyticsService` - Complex calculations and aggregations
- Custom repository queries for efficient data aggregation
- Redis caching for expensive calculations

**Endpoints:**
```
GET    /api/v1/analytics/summary?period=monthly              - Income/Expense summary
GET    /api/v1/analytics/trends?period=weekly                - Spending trends over time
GET    /api/v1/analytics/category-breakdown?type=expense     - Category-wise breakdown
GET    /api/v1/analytics/top-categories?limit=5              - Top spending categories
GET    /api/v1/analytics/cash-flow?startDate=...&endDate=... - Cash flow analysis
GET    /api/v1/analytics/income-sources?period=monthly       - Income sources breakdown
GET    /api/v1/analytics/spending-patterns                   - Spending by day of week
GET    /api/v1/analytics/comparison?type=month-over-month    - Period comparisons
GET    /api/v1/analytics/savings-rate?period=monthly         - Savings rate calculation
GET    /api/v1/analytics/net-worth                           - Total net worth
```

**Response Data Structures:**

**Summary Response:**
```json
{
  "period": "2024-01",
  "totalIncome": 5000.00,
  "totalExpense": 3500.00,
  "netSavings": 1500.00,
  "savingsRate": 30.0,
  "transactionCount": 45,
  "averageTransactionAmount": 166.67
}
```

**Category Breakdown Response:**
```json
{
  "categories": [
    {
      "categoryId": "123",
      "categoryName": "Food & Dining",
      "amount": 800.00,
      "percentage": 22.86,
      "transactionCount": 15,
      "color": "#FF6B6B"
    }
  ]
}
```

**Trends Response (for charts):**
```json
{
  "labels": ["Week 1", "Week 2", "Week 3", "Week 4"],
  "datasets": [
    {
      "label": "Income",
      "data": [1250, 1250, 1250, 1250]
    },
    {
      "label": "Expense",
      "data": [875, 920, 850, 855]
    }
  ]
}
```

**Performance Optimizations:**
- Aggregate queries at database level
- Redis caching for frequently accessed analytics (5 min TTL)
- Async calculation for complex reports
- Pre-calculated summaries for current month

---

### Module 6: Report Generation
**Duration: Week 4**

**Features:**
- Generate PDF reports with custom date ranges
- Generate Excel reports with detailed transactions
- Include charts and visualizations in PDF
- Customizable report templates
- Email report delivery
- Download report directly
- Scheduled monthly reports (future enhancement)
- Report history and regeneration

**Components:**
- `ReportController` - Report endpoints
- `GenerateReportUseCase` - Report business logic
- `ReportService` - Report generation orchestration
- `PdfReportGenerator` - PDF generation with iText
- `ExcelReportGenerator` - Excel generation with Apache POI
- `SmtpEmailService` - Email delivery

**Endpoints:**
```
POST   /api/v1/reports/generate                 - Generate new report
GET    /api/v1/reports                          - Get report history
GET    /api/v1/reports/{id}                     - Get report details
GET    /api/v1/reports/{id}/download            - Download report file
DELETE /api/v1/reports/{id}                     - Delete report
POST   /api/v1/reports/{id}/email               - Email report to user
GET    /api/v1/reports/pdf?startDate=...        - Generate & download PDF
GET    /api/v1/reports/excel?startDate=...      - Generate & download Excel
```

**Report Request:**
```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "format": "PDF",
  "includeCharts": true,
  "includeTransactions": true,
  "groupBy": "CATEGORY",
  "walletIds": [123, 456],
  "categoryIds": []
}
```

**PDF Report Contents:**
- Cover page with date range and summary
- Income vs Expense chart (bar chart)
- Category breakdown (pie chart)
- Top 10 transactions table
- Monthly trends (line chart)
- Wallet-wise summary table
- Optional: All transactions table

**Excel Report Contents:**
- Summary sheet with key metrics
- Transactions sheet (all transactions with filters)
- Category Analysis sheet with pivot table
- Monthly Trends sheet with charts
- Wallet Details sheet

**Business Rules:**
- Reports generated asynchronously for large date ranges
- Maximum 1 year date range per report
- Generated reports stored for 30 days
- PDF max size: 10 MB
- Excel max rows: 65,000 transactions

---

## 🔐 Security Implementation

This project implements **enterprise-grade security** following industry best practices:

### Authentication & Authorization

**JWT-based Authentication**
- **Access Tokens**: Short-lived (15-30 minutes) for API access
- **Refresh Tokens**: Long-lived (7 days) stored in database with revocation support
- **Token Rotation**: Automatic refresh token rotation on each use
- **Stateless**: No server-side session storage required

**Security Features:**
- ✅ BCrypt password hashing (strength: 12)
- ✅ Strong password policy validation with Passay
- ✅ Email verification before account activation
- ✅ Account lockout after 5 failed login attempts (30 min)
- ✅ Rate limiting on all endpoints (Bucket4j + Redis)
- ✅ Refresh token revocation & blacklisting
- ✅ JWT token validation with signature verification
- ✅ Role-based access control (RBAC)
- ✅ OAuth2 social login support (Google, GitHub) - Optional

### Rate Limiting Strategy

**Login Endpoint:**
- 5 attempts per 15 minutes per IP address
- Progressive delays after failed attempts
- Automatic unlocking after timeout

**Password Reset:**
- 3 attempts per hour per email address
- Token expiration: 1 hour

**API Endpoints:**
- 100 requests per minute per authenticated user
- 20 requests per minute per IP for public endpoints
- X-Rate-Limit-Remaining header in responses

**Implementation:**
```java
// Uses Bucket4j with Redis for distributed rate limiting
// Automatic 429 (Too Many Requests) response
// Configurable limits per endpoint
```

### Security Headers

All responses include security headers:
- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `X-XSS-Protection: 1; mode=block`
- `Content-Security-Policy: default-src 'self'`
- `Strict-Transport-Security: max-age=31536000`

### Password Requirements

- Minimum 8 characters, maximum 128
- At least 1 uppercase letter (A-Z)
- At least 1 lowercase letter (a-z)
- At least 1 digit (0-9)
- At least 1 special character (!@#$%^&*)
- No whitespace allowed
- Cannot contain username or email

### Audit Logging

All security-critical operations are logged:
- User registration and email verification
- Login/logout events with IP and user agent
- Failed authentication attempts
- Password changes and resets
- Profile modifications
- Token refresh and revocation
- Sensitive data access (transactions, reports)
- Account lockout events

### Security Configuration

**CORS Policy:**
- Configurable allowed origins via environment variables
- Credentials support enabled for cookie-based auth
- Preflight request caching (1 hour)
- Allowed methods: GET, POST, PUT, DELETE, PATCH

**Session Management:**
- Stateless session creation policy
- No server-side sessions
- JWT-only authentication
- CSRF protection disabled (stateless API)

**Protected Endpoints:**
```
Public Endpoints:
  - /api/v1/auth/register
  - /api/v1/auth/login
  - /api/v1/auth/verify-email
  - /api/v1/auth/forgot-password
  - /api/v1/auth/reset-password
  - /swagger-ui/**
  - /v3/api-docs/**
  - /actuator/health

Private Endpoints (Require JWT):
  - All other /api/v1/** endpoints
```

### Security Dependencies

```xml
<!-- JWT Authentication -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>

<!-- Password Validation -->
<dependency>
    <groupId>org.passay</groupId>
    <artifactId>passay</artifactId>
    <version>1.6.4</version>
</dependency>

<!-- Rate Limiting -->
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.7.0</version>
</dependency>
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-redis</artifactId>
    <version>8.7.0</version>
</dependency>

<!-- OAuth2 (Optional) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

### Environment Variables

Required security-related environment variables:

```bash
# JWT Configuration
JWT_SECRET=your-256-bit-secret-key-base64-encoded-min-32-chars
JWT_ACCESS_TOKEN_EXPIRATION=900000      # 15 minutes in milliseconds
JWT_REFRESH_TOKEN_EXPIRATION=604800000  # 7 days in milliseconds

# BCrypt
BCRYPT_STRENGTH=12

# Rate Limiting
RATE_LIMIT_LOGIN_ATTEMPTS=5
RATE_LIMIT_LOGIN_WINDOW=900           # 15 minutes in seconds
RATE_LIMIT_API_REQUESTS=100
RATE_LIMIT_API_WINDOW=60              # 1 minute in seconds

# Account Lockout
ACCOUNT_LOCKOUT_DURATION=1800         # 30 minutes in seconds
MAX_FAILED_ATTEMPTS=5

# CORS
ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200

# OAuth2 (Optional)
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
GITHUB_CLIENT_ID=your-github-client-id
GITHUB_CLIENT_SECRET=your-github-client-secret
```

### Security Best Practices Implemented

- [x] Never store passwords in plain text
- [x] Use environment variables for secrets (never commit to Git)
- [x] Implement HTTPS in production (nginx config provided)
- [x] Enable CORS only for trusted origins
- [x] Validate all user inputs with Bean Validation
- [x] Log security events (login, failed attempts, changes)
- [x] Set appropriate token expiration times
- [x] Implement refresh token rotation
- [x] Add rate limiting on sensitive endpoints
- [x] Use secure password requirements
- [x] Implement account lockout mechanism
- [x] Add email verification workflow
- [x] Enable all security headers
- [x] Regular dependency updates via Dependabot
- [x] SQL injection prevention (Prepared Statements via JPA)
- [x] XSS prevention (Content Security Policy)
- [x] CSRF protection disabled (stateless JWT API)
- [x] Secure password reset with time-limited tokens
- [x] Audit logging for compliance

### Security Testing

Run security-specific tests:
```bash
# All security tests
mvn test -Dtest=*SecurityTest

# Authentication tests
mvn test -Dtest=JwtAuthenticationTest

# Rate limiting tests
mvn test -Dtest=RateLimitingTest

# Password validation tests
mvn test -Dtest=PasswordValidatorTest

# Authorization tests
mvn test -Dtest=AuthorizationTest
```

---

## 🛠️ Tech Stack

### Core
- **Java 17+**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
- **Spring Cache**

### Database
- **PostgreSQL 14+** - Primary database
- **Redis 7+** - Caching layer & rate limiting

### Security Libraries
- **Spring Security** - Authentication & Authorization framework
- **JJWT 0.12.3** - JWT token generation and validation
- **Passay 1.6.4** - Password strength validation
- **BCrypt** - Password hashing (via Spring Security)
- **Bucket4j 8.7.0** - Rate limiting and throttling
- **Spring OAuth2 Client** - Social login support (optional)

### Utility Libraries
- **MapStruct 1.5.5** - DTO mapping
- **Lombok 1.18.30** - Reduce boilerplate
- **Apache Commons Lang3** - String utilities
- **Google Guava 32.1.3** - Collections, utilities

### Report Generation
- **iText 8.0.2** - PDF generation
- **Apache POI 5.2.5** - Excel generation
- **JFreeChart 1.5.4** - Charts for reports

### Documentation & Monitoring
- **SpringDoc OpenAPI 2.3.0** - API documentation
- **Flyway** - Database migrations
- **Micrometer** - Metrics for Prometheus
- **Logstash Encoder 7.4** - Structured logging

### Testing
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework
- **Spring Boot Test** - Integration testing
- **