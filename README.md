# Mobility Sharing - Backend

Backend for the Mobility Sharing app built with Java 21 and Spring Boot 3. Provides REST APIs for user, trip, subscription, and rating management with JWT authentication.  

**Tech Stack:**
- Java 21, Spring Boot 3.3.4, MySQl 8.0.33
- Maven for build management
- Hibernate / JPA for ORM
- MySQL for production
- Flyway for database migrations
- JUnit & Mockito for unit testing
- JWT for authentication and role-based access

**Run:**
1. Clone repo: `git clone <repo-url>`
2. Configure database in `application.yml` and set `.env` file
3. Migrate: `mvn flyway:migrate`
4. Start: `mvn spring-boot:run`
