
# FinMid Backend Interview - Documentation

## Table of Contents
- [Build and Run Instructions](#build-and-run-instructions)
    - [Prerequisites](#prerequisites)
    - [Standalone start](#standalone-start)
    - [Backup app start](#backup-app-start)
- [Technology Stack](#technology-stack)
    - [Why Spring WebFlux](#why-spring-webflux)
    - [Why R2DBC (and Not Plain JDBC)](#why-r2dbc-and-not-plain-jdbc)
    - [Why JOOQ](#why-jooq)
    - [Why Kotlin](#why-kotlin)
- [Project Structure Organization](#project-structure-organization)
    - [Source Sets](#source-sets)
    - [Main Package Organization](#main-package-organization)
    - [Module Structure](#module-structure)
- [Data Model Choices](#data-model-choices)
    - [Account Model](#1-account-model)
    - [Consideration of Event-Based Approach for Locking](#consideration-of-event-based-approach-for-locking)
    - [Transaction Model](#2-transaction-model)
- [API Endpoint Documentation](#api-endpoint-documentation)
  - [Swagger](#swagger)
  - [Postman collection](#postman-collection)
  - [1. API Endpoint: Create Account](#1-api-endpoint-create-account)
  - [2. API Endpoint: Retrieve account by id](#2-api-endpoint-retrieve-account-by-id)
  - [3. API Endpoint: Create Transaction](#3-api-endpoint-create-transaction)
  - [4. API Endpoint: Retrieve transaction by id](#4-api-endpoint-retrieve-transaction-by-id)
- [Testing Strategy](#testing-strategy)
    - [Unit Tests](#unit-tests)
    - [Integration Tests](#integration-tests)
    - [Running All Tests](#running-all-tests)
---

## Build and Run Instructions

### Prerequisites
To run this project, ensure that the following dependencies are installed:

- **Docker**: Required for managing containers used in the project.
- **Java 21 (latest LTS)**: The application requires Java 21, which is the latest Long-Term Support (LTS) version of Java.

## Standalone start
1. Clone the repository
2. Navigate to the project directory
3. Run gradle `prepareAndRun` command which will build project, start postgres docker container and start bootRun task:
   ```bash
   ./gradlew prepareAndRun --stacktrace
   ```

## Backup app start
In case initial standalone app start is not working because of [Gradle Docker not found error](https://github.com/avast/gradle-docker-compose-plugin/issues/435)
1. Clone the repository
2. Navigate to the project directory:
3. Build the project:
   ```bash
   ./gradlew build
   ```
4. Start Postgres database with docker-compose:
   ```bash
   docker-compose -f docker/postgres/docker-compose-postgres.yml  up -d
   ```
5. Run the application:
   ```bash
   ./gradlew bootRun
   ```

---

## Technology Stack

### Why Spring WebFlux
Spring WebFlux is chosen for its support of reactive programming, making it suitable for building scalable, non-blocking applications. It leverages Project Reactor to handle asynchronous data streams, providing high throughput and efficiency for applications requiring concurrency.

### Why R2DBC (and Not Plain JDBC)
R2DBC (Reactive Relational Database Connectivity) is crucial for building a fully reactive application stack. Unlike plain JDBC, which is blocking and operates in a synchronous manner, R2DBC is designed for non-blocking, asynchronous database access. This is particularly important in a reactive application for the following reasons:

- **Non-Blocking I/O**: JDBC uses blocking I/O, meaning that each database query can block a thread until a response is received. In a reactive application, this would create bottlenecks and reduce scalability, defeating the purpose of using a reactive approach. On the other hand, R2DBC allows for non-blocking interactions with the database, ensuring the application remains responsive and can handle high-throughput operations efficiently.
- **Scalability**: With R2DBC, you can achieve true non-blocking database access, which enables the application to handle a larger number of concurrent requests without being constrained by thread availability. This makes it well-suited for highly scalable and responsive applications.
- **Integration with Reactive Streams**: R2DBC is designed to integrate seamlessly with reactive streams and libraries like Project Reactor. This ensures a consistent reactive programming model throughout the entire stack, from the web layer (using Spring WebFlux) to the database.

By using R2DBC instead of plain JDBC, we ensure that the reactive nature of the application is preserved across all layers, maximizing the benefits of a fully reactive architecture.

### Why JOOQ
JOOQ offers a fluent API for building type-safe SQL queries in a way that integrates seamlessly with the database schema. This allows complex SQL operations and query building with compile-time safety and flexibility, which is especially useful for projects that need fine-grained control over database interactions.

### Why Kotlin
Kotlin is chosen for its concise syntax, null safety features, and strong interoperability with Java. It significantly improves developer productivity and integrates well with modern frameworks like Spring WebFlux.

Kotlin Coroutines provide a simple way to write asynchronous, non-blocking code. Unlike traditional callback-based asynchronous programming, coroutines offer a clean, readable, and maintainable way to express complex asynchronous workflows, making them a perfect fit for reactive applications.

---

### Project Structure Organization

#### Source Sets
The project is organized into the following source sets to ensure a clear separation of concerns and facilitate clean code organization:

- **generated**: Contains code generated by JOOQ, such as database models and related classes. Keeping generated code in its own source set helps maintain a clear distinction between generated and manually written code.
- **main**: The main source set for application code. 
- **test**: Contains unit tests. Unit Tests are kept separate from integration tests in their own test source set. These tests run quickly, focusing on small, isolated units of functionality.
- **integration-test**: Contains integration tests that verify the system's behavior when multiple components interact. Integration Tests are in their own integration-test source set and are executed separately, typically during CI builds, to verify the integration between components and external dependencies.

#### Main Package Organization
The `com.finmid.backendinterview` package serves as the main package for the project. It is organized as follows:

#### Module Structure
Each module (`account` and `transaction`) follows a consistent internal structure for organizing related components. This ensures modularity, scalability, and maintainability:

- **controller.v1**: Handles incoming HTTP requests and exposes RESTful APIs. Organized by version (e.g., `v1`) for API versioning and future extensibility.
- **domain**: Contains core domain models and business logic related to the module.
- **dto**:
   - `request`: DTOs (Data Transfer Objects) for incoming requests.
   - `response`: DTOs for outgoing responses.
- **mapper**: Responsible for mapping between domain objects and DTOs, often implemented as extension functions for easy transformation.
- **repository**: Interfaces and implementations for data access, typically interacting with the database or other persistence layers.
- **service**: Contains service interfaces and their implementations, encapsulating business logic and coordinating between different components.
- **validation**: Validation logic specific to the module, ensuring data integrity and business rule enforcement.

---



## Data Model Choices

### 1. Account Model
```kotlin
data class Account(
    val id: String,
    val balance: BigDecimal,
    val version: Long
)
```

```postgresql
CREATE TABLE IF NOT EXISTS accounts (
    id VARCHAR(40) PRIMARY KEY,
    balance NUMERIC(15, 2) NOT NULL CHECK (balance >= 0),
    version BIGINT NOT NULL,
    CHECK (id ~ '^[a-z0-9_-]+$')
);
```

#### 1. `id VARCHAR(40) PRIMARY KEY`
- **Reasoning**:
    - **Data Type**: The `id` column is defined as `VARCHAR(40)` to provide flexibility in supporting a wide range of unique identifiers. This includes alphanumeric strings, UUIDs, or other formats that might be used for account identification.
    - **Primary Key**: Setting `id` as the `PRIMARY KEY` enforces uniqueness and non-nullability, ensuring each account record has a unique identifier.
    - **Alternative Consideration**: A `UUID` data type could have been used if a globally unique identifier was required. However, using `VARCHAR` allows for different ID formats and does not restrict the type to strictly UUIDs.
      - for account id we could use something like IBAN

#### 2. `CHECK (id ~ '^[a-z0-9_-]+$')`
- **Reasoning**:
    - This `CHECK` constraint uses a regular expression to ensure that the `id` column contains only lowercase alphanumeric characters, hyphens (`-`), or underscores (`_`). This enforces a specific format for `id` values, promoting consistency and potentially reducing input errors or security vulnerabilities.

#### 3.  **`balance NUMERIC(15, 2) NOT NULL CHECK (balance >= 0)` [kotlin`balance: BigDecimal`]**:
  - **Reasoning**:
    - **Data Type**: `NUMERIC(15, 2)` is used for the `balance` column to represent monetary values accurately, with a precision of 15 digits and a scale of 2 decimal places. This choice ensures precise calculations and prevents rounding errors, which are critical in financial applications.
    - **Constraint**: `NOT NULL` enforces that every account must have a defined balance. The `CHECK (balance >= 0)` constraint ensures that the balance cannot be negative, maintaining data integrity for accounts. Negative balances may be undesirable in this context (e.g., overdrafts not allowed).
    - **Alternative Consideration**: Using `BIGINT` to store amounts in cents (e.g., `1234` representing `$12.34`) was considered to avoid precision issues and improve performance with integer operations. However, this would reduce human readability and require conversions for display and input.

#### 4. `version BIGINT NOT NULL`
- **Reasoning**:
    - The `version` column is used to implement optimistic locking, helping manage concurrent updates to account records. It ensures that updates only succeed if the `version` matches the expected value, preventing lost updates and maintaining data consistency in multi-threaded or distributed environments.
    - **Data Type**: `BIGINT` was chosen to accommodate large numbers of updates over time without risking overflow.

### Consideration of Event-Based Approach for Locking

In addition to using optimistic locking with the `version` field, another approach considered for handling concurrent updates is an event-based locking mechanism. This approach relies on a distributed event system to manage state changes and ensure consistency across services.

#### How Event-Based Locking Works
- **Event Publishing**: When a change to an account or transaction is initiated, an event is published to a message broker (e.g., Kafka, RabbitMQ).
- **Event Handling**: Interested services or components subscribe to these events, processing them in a controlled, ordered manner.
- **Concurrency Control**: By managing state changes through event handling, you can ensure that conflicting operations are handled gracefully, preventing race conditions and maintaining data consistency.

#### Benefits of Event-Based Locking
- **Scalability**: Event-based systems are inherently more scalable as they decouple producers and consumers of events.
- **Asynchronous Processing**: This approach allows for asynchronous handling of operations, which can lead to improved performance for non-critical operations.
- **Distributed Systems Compatibility**: Event-based locking can be highly effective in distributed systems where multiple nodes might attempt to modify the same resource.

#### Potential Trade-Offs
- **Complexity**: Introducing an event-driven architecture adds complexity in terms of managing event order, retries, and failures.
- **Latency**: Depending on the system's requirements, there may be additional latency due to asynchronous processing.

#### Conclusion
While an event-based approach to locking can provide a robust alternative to optimistic locking, it introduces additional architectural considerations and complexity. This approach may be best suited for systems that require high scalability and are already leveraging event-driven designs.

---
    
## 2. Transaction Model
```kotlin
data class Transaction(
    val id: UUID,
    val amount: BigDecimal,
    val fromAcc: String,
    val toAcc: String,
    val createdAt: LocalDateTime
)
```

```postgresql
CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY,
    amount NUMERIC(15,2) NOT NULL CHECK (amount > 0),
    from_acc VARCHAR(40) REFERENCES accounts(id) ON DELETE RESTRICT NOT NULL,
    to_acc VARCHAR(40) REFERENCES accounts(id) ON DELETE RESTRICT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
```

#### 1. **`id UUID PRIMARY KEY`**
- **Reasoning**:
    - **Data Type**: The `id` column is defined as a `UUID` to guarantee global uniqueness for each transaction record.
    - **Primary Key**: Defining `id` as the `PRIMARY KEY` ensures that each transaction has a unique identifier and cannot be null.

#### 2. **`amount NUMERIC(15, 2) NOT NULL CHECK (amount > 0)`**
- **Reasoning**:
    - **Data Type**: The `NUMERIC(15, 2)` data type ensures precise representation of monetary values, with up to 15 digits of precision and 2 decimal places. This prevents rounding errors and maintains accuracy for financial transactions.
    - **Constraint**: The `NOT NULL` constraint ensures that every transaction must have a specified amount, and the `CHECK (amount > 0)` constraint enforces that only positive amounts are allowed. This reflects the typical nature of transactions, where zero or negative values are usually invalid.
    - **Alternative Consideration**: Representing the amount in cents as an integer (`BIGINT`) could be considered for performance optimization. This approach would avoid precision issues but would require converting values for display purposes.

#### 3. **`from_acc VARCHAR(40) REFERENCES accounts(id) ON DELETE RESTRICT NOT NULL`**
- **Reasoning**:
    - **Data Type**: `from_acc` is defined as `VARCHAR(40)` to ensure consistency with the `id` field in the `accounts` table, which is also defined as `VARCHAR(40)`. This allows the `from_acc` field to store a valid account identifier that matches the format used in the `accounts` table.
    - **Foreign Key Constraint**: `REFERENCES accounts(id)` establishes a foreign key relationship between the `from_acc` column in the `transactions` table and the `id` column in the `accounts` table. This ensures referential integrity, meaning that every value in `from_acc` must correspond to an existing `id` in the `accounts` table. This constraint guarantees that a transaction cannot reference an account that does not exist.
    - **`NOT NULL` Constraint**: This constraint ensures that every transaction must have a valid `from_acc` value. This is necessary to guarantee that each transaction has a source account, reinforcing data consistency and preventing incomplete transaction records.

#### 4. **`to_acc VARCHAR(40) REFERENCES accounts(id) ON DELETE RESTRICT NOT NULL`**
- **Reasoning**:
    - **Data Type**: The `to_acc` column is defined as `VARCHAR(40)`, consistent with the `id` column in the `accounts` table. This ensures that `to_acc` can store valid account identifiers that match the format used in the `accounts` table.
    - **Foreign Key Constraint**: `REFERENCES accounts(id)` establishes a foreign key relationship between the `to_acc` column in the `transactions` table and the `id` column in the `accounts` table. This guarantees referential integrity by ensuring that every value in `to_acc` corresponds to an existing account in the `accounts` table.
    - **`NOT NULL` Constraint**: The `NOT NULL` constraint ensures that every transaction has a valid `to_acc` value. This guarantees that every transaction specifies a destination account, preventing incomplete or invalid transaction records.

#### 5. **`created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL`**
- **Reasoning**:
    - **Data Type**: The `TIMESTAMP` type captures the exact date and time when the transaction is created.
    - **Default Value**: `DEFAULT CURRENT_TIMESTAMP` automatically sets the value to the current timestamp when a transaction is inserted, providing accurate tracking of when transactions occur.
    - **NOT NULL Constraint**: Ensures every transaction has a timestamp.

---
## API Endpoint Documentation
#### Swagger
Swagger generated documentation is accessible on [Swagger Documentation](http://localhost:8080/webjars/swagger-ui/index.html#/) url.

#### Postman collection
The Postman collection for this project is located in the [Postman Collection](postman/finmid-interview.postman_collection.json) folder. This collection contains pre-configured API requests for testing and interacting with the application endpoints.


### 1. API Endpoint: Create Account

#### Description
- `POST /api/v1/accounts`
This endpoint is used to create a new account.

#### Response Codes and Behaviors
- **`HTTP 201 Created`**: Returned when an account is successfully created. The response body will contain the payload of the newly created account.
- **`HTTP 400 Bad Request`**: Returned if there are validation issues with the request. Validation rules include:
    - **Account ID must not already exist**: The `id` of the account being created must be unique.
    - **Balance must be greater than 0**: The initial balance provided for the account must be a positive number.
    - **Account ID format validation**: The `id` must match the regular expression `^[a-z0-9_-]+$`. This ensures that only lowercase alphanumeric characters, hyphens (`-`), and underscores (`_`) are allowed.
    - **Account ID length validation**: The `id` must not exceed 40 characters.
- **`HTTP 500 Internal Server Error`**: Returned if an unexpected error occurs during the processing of the request.

### 2. API Endpoint: Retrieve account by id

#### Description
- `GET /api/v1/accounts/{account-id}`
This endpoint retrieves the details of an account specified by the `{account-id}` path parameter.

#### Response Codes and Behaviors
- **`200 OK`**: Returned when the specified account exists. The response body will contain the details of the requested account.
- **`404 Not Found`**: Returned if the specified `account-id` does not exist.
- **`500 Internal Server Error`**: Returned if an unexpected error occurs during the processing of the request.
- 
### 3. API Endpoint: Create Transaction

#### Description
- `POST /api/v1/transactions`
This endpoint is used to create and execute a new transaction between two accounts.

#### Response Codes and Behaviors
- **`HTTP 201 Created`**: Returned when a transaction is successfully created and executed. The response body will contain the details of the created transaction.
- **`HTTP 400 Bad Request`**: Returned if there are validation issues with the request. Validation rules include:
    - **`from_acc` must match the regex `^[a-z0-9_-]+$`**: Ensures that the source account ID contains only lowercase alphanumeric characters, hyphens (`-`), or underscores (`_`).
    - **`to_acc` must match the regex `^[a-z0-9_-]+$`**: Ensures that the destination account ID contains only lowercase alphanumeric characters, hyphens (`-`), or underscores (`_`).
    - **`from_acc` must exist**: The specified source account ID must refer to an existing account.
    - **`to_acc` must exist**: The specified destination account ID must refer to an existing account.
    - **source and destination accounts must not be the same**: The `from_acc` and `to_acc` account IDs must be different.
    - **amount must be positive**: The `amount` specified for the transaction must be greater than 0.
    - **amount max decimal places**: The `amount` specified for the transaction has more than 2 decimal places.
    - **`from_acc` balance must be greater than or equal to the amount**: The balance of the source account must be sufficient to cover the transaction amount.
- **`HTTP 409 Conflict`**: Returned if a race condition occurs while creating the transaction. This can happen when multiple concurrent operations attempt to modify the same account balance, leading to a conflict that must be resolved.
- **`HTTP 500 Internal Server Error`**: Returned if an unexpected error occurs during the processing of the request.

### 4. API Endpoint: Retrieve transaction by id

#### Description
`GET /api/v1/accounts/{transaction-id}`
This endpoint retrieves the details of a transaction specified by the `{transaction-id}` path parameter. 

#### Response Codes and Behaviors
- **`200 OK`**: Returned when the specified transaction exists. The response body will contain the details of the requested transaction.
- **`404 Not Found`**: Returned if the specified `transaction-id` does not exist.
- **`400 Bad Request`**: Returned if the provided `transaction-id` is not a valid UUID, as it is directly mapped to a UUID type.
- **`500 Internal Server Error`**: Returned if an unexpected error occurs during the processing of the request.
---

## Testing Strategy

### Unit Tests
- **Description**: Unit tests are located in the `test` source set and are designed to test individual components or functions in isolation. These tests are fast and focus on verifying the behavior of specific code units without involving external dependencies like databases or web services.
- **How to Run**: You can run the unit tests using the following command:
  ```bash
  ./gradlew test
  ```

### Integration Tests
- **Description**: Integration tests are located in the `integration-test` source set and verify the interaction between multiple components, as well as the behavior of the system when working with external dependencies, such as databases or APIs. These tests help ensure that the different parts of the application work together correctly.
- **How to Run**: You can run the integration tests using the following command:
  ```bash
  ./gradlew integrationTest
  ```

### Running All Tests
- **Description**: To run both unit and integration tests together, use the `check` task. This task runs all tests and performs any additional checks that are configured in the build.
- **How to Run**: Use the following command to run both unit and integration tests:
  ```bash
  ./gradlew check
  ```

---