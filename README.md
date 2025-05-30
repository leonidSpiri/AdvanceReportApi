# Advance Report API

A comprehensive system for managing advance reports, expense tracking, and financial transactions for business trips.

## Project Overview

The Advance Report API is a Spring Boot application written in Kotlin that provides a complete solution for managing advance reports for business trips. The system allows employees to create advance reports, add expense items, attach receipts, and submit them for approval. Supervisors can review, approve, or reject reports, and accounting staff can process financial transactions and archive approved reports.

## Project Structure

The application follows a standard layered architecture:

- **Controllers**: Handle HTTP requests and responses
  - `AdvanceReportController`: Manages advance reports
  - `FinancialController`: Handles financial transactions
  - `UserController`: Manages user accounts

- **Services**: Implement business logic
  - `AdvanceReportService`: Business logic for advance reports
  - `FinancialService`: Business logic for financial transactions
  - `UserService`: Business logic for user management
  - `FileUploadService`: Handles file uploads for receipts and attachments

- **Repositories**: Data access layer
  - `AdvanceReportRepository`: Data access for advance reports
  - `ExpenseItemRepository`: Data access for expense items
  - `FinancialTransactionRepository`: Data access for financial transactions
  - `UserRepository`: Data access for users

- **Models**: Domain entities
  - `AdvanceReport`: Represents an advance report
  - `ExpenseItem`: Represents an expense item in a report
  - `Receipt`: Represents a receipt for an expense
  - `ReceiptItem`: Represents an item in a receipt
  - `Attachment`: Represents a file attachment
  - `FinancialTransaction`: Represents a financial transaction
  - `User`: Represents a user in the system

- **Enums**: Define constants and types
  - `ReportStatus`: Status of an advance report (DRAFT, SUBMITTED, UNDER_REVIEW, REJECTED, APPROVED, ARCHIVED)
  - `TransactionType`: Type of financial transaction
  - `UserRole`: User roles (EMPLOYEE, SUPERVISOR, ACCOUNTING, ADMIN)

- **Security**: Authentication and authorization
  - JWT-based authentication
  - Role-based access control

## Data Relationships

- An advance report (`AdvanceReport`) has multiple expense items (`ExpenseItem`)
- Each expense item has one receipt (`Receipt`)
- Each receipt has multiple receipt items (`ReceiptItem`)
- Each receipt has multiple attachments (`Attachment`) - photos of the receipt
- Each expense item has multiple attachments (`Attachment`) - photos of confirmation or contract

## Setup Instructions

### Prerequisites

- JDK 17 or higher
- PostgreSQL 12 or higher
- Gradle 7.6 or higher

### Configuration

The application can be configured using environment variables or by modifying the `application.properties` file:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${POSTGRES_DB:advance_report_db}
spring.datasource.username=${POSTGRES_USER:postgres}
spring.datasource.password=${POSTGRES_PASSWORD:postgres}

# JPA Configuration
spring.jpa.hibernate.ddl-auto=${JPA_DDL_AUTO:update}

# JWT Configuration
jwt.secret=${JWT_SECRET:your_secret_key}
jwt.access-token-validity=${JWT_ACCESS_TOKEN_VALIDITY:900000}
jwt.refresh-token-validity=${JWT_REFRESH_TOKEN_VALIDITY:604800000}

# File Storage Configuration
file.upload-dir=${FILE_UPLOAD_DIR:./uploads}

# Server Configuration
server.port=${SERVER_PORT:8080}
```

### Building and Running

1. Clone the repository
2. Configure the application as needed
3. Build the application:
   ```bash
   ./gradlew build
   ```
4. Run the application:
   ```bash
   ./gradlew bootRun
   ```

## API Documentation

The API documentation is available via Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

### Main API Endpoints

#### Advance Reports

- `GET /api/v1/reports`: Get all reports (with optional filtering)
- `GET /api/v1/reports/{id}`: Get a specific report
- `POST /api/v1/reports`: Create a new report
- `PUT /api/v1/reports/{id}`: Update a report
- `DELETE /api/v1/reports/{id}`: Delete a report
- `POST /api/v1/reports/{id}/submit`: Submit a report for review
- `POST /api/v1/reports/{id}/approve`: Approve a report
- `POST /api/v1/reports/{id}/reject`: Reject a report
- `POST /api/v1/reports/{id}/archive`: Archive a report

#### Financial Transactions

- `GET /api/v1/finance/users/balance`: Get current user's balance
- `GET /api/v1/finance/transactions`: Get all transactions (with optional filtering)
- `POST /api/v1/finance/transactions`: Create a new transaction

#### Users

- `GET /api/v1/users`: Get all users
- `GET /api/v1/users/{id}`: Get a specific user
- `POST /api/v1/users`: Create a new user
- `PUT /api/v1/users/{id}`: Update a user
- `DELETE /api/v1/users/{id}`: Delete a user

## User Roles and Permissions

The system has four user roles with different permissions:

1. **EMPLOYEE**:
   - Create, view, update, and delete their own reports
   - Submit reports for approval
   - View their own financial transactions and balance

2. **SUPERVISOR**:
   - View reports submitted to them
   - Approve or reject reports
   - Add comments to reports

3. **ACCOUNTING**:
   - View all reports
   - Create financial transactions
   - Archive approved reports
   - Add accounting reference numbers and comments

4. **ADMIN**:
   - Full access to all features
   - Manage users and their roles

## Workflow

1. An employee creates an advance report for a business trip
2. The employee adds expense items, receipts, and attachments to the report
3. The employee submits the report to a supervisor
4. The supervisor reviews the report and either approves or rejects it
5. If rejected, the employee can update the report and resubmit it
6. If approved, the accounting staff can process the report and archive it
7. Financial transactions are created to track advances and reimbursements

## Security

The application uses JWT (JSON Web Token) for authentication. To access protected endpoints, clients must include a valid JWT token in the Authorization header:

```
Authorization: Bearer <token>
```

## File Storage

The application supports file uploads for receipts and attachments. Files are stored in the directory specified by the `file.upload-dir` property.
