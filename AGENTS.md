# AGENTS.md — Employee Management System

## 1. Project Overview

Employee Management System is a web application designed to help companies manage their employees, departments, and users.

The main goal is to build a professional full-stack application for a personal developer portfolio.

The application will provide two independent frontend interfaces connected to the same backend REST API.

## 2. Project Architecture

The project follows a monorepo architecture.

Project structure:

- `backend/`: Spring Boot REST API.
- `frontend-angular/`: Angular frontend application.
- `frontend-vue/`: Vue.js frontend application.
- `docs/`: Project documentation and screenshots.

Both frontend applications must communicate with the same Spring Boot REST API.

General architecture:

Angular / Vue.js → Spring Boot REST API → PostgreSQL

Do not create separate backends for Angular and Vue.

## 3. Technology Stack

### Backend
- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- Spring Security
- REST API
- PostgreSQL
- OpenAPI / Swagger

### Angular Frontend
- Angular
- TypeScript
- HTML
- CSS

### Vue Frontend
- Vue.js
- TypeScript
- HTML
- CSS

### Development and Quality Tools
- JUnit 5
- Mockito
- Frontend testing tools appropriate to each framework
- Docker
- Git and GitHub
- GitHub Actions

Before selecting framework versions, check compatibility with the installed Java version and the other dependencies.

Do not upgrade dependencies or change the technology stack without approval.

## 4. User Roles and Permissions

The application has exactly two roles in its first version:

### ADMIN

The administrator can:

- Access all application features.
- Create, view, update, and delete employees.
- Manage departments.
- Create, view, update, and delete user accounts.
- Assign roles to users.
- Access the dashboard.

### HR

The HR user can:

- Create, view, update, and delete employees.
- Manage departments.
- Search and filter employees.
- Access the dashboard.

HR users cannot manage user accounts or change user permissions.

There is no EMPLOYEE role in the first version.

All permissions must be enforced on the backend, not only in the frontend.

## 5. Main Features

### 5.1 Authentication

- User login.
- User logout.
- Role-based authorization.
- Protected API endpoints and frontend routes.

### 5.2 Employee Management

Each employee must have the following information:

- `id`: Unique identifier.
- `firstName`: First name.
- `lastName`: Last name.
- `email`: Email address.
- `phone`: Phone number.
- `hireDate`: Hiring date.
- `salary`: Salary.
- `position`: Job position.
- `status`: ACTIVE or INACTIVE.
- `department`: Associated department.

Required operations:

- Create an employee.
- View employee details.
- Update employee information.
- Delete an employee.
- Search and filter employees.

### 5.3 Department Management

Each department must contain:

- `id`: Unique identifier.
- `name`: Department name.
- `description`: Department description.

Required operations:

- Create a department.
- List departments.
- View department details.
- Update a department.
- Delete a department.

### 5.4 User Management

Only ADMIN users can:

- Create user accounts.
- List users.
- View user details.
- Update user accounts.
- Delete user accounts.
- Assign ADMIN or HR roles.

### 5.5 Dashboard

The dashboard should display:

- Total number of employees.
- Number of active employees.
- Number of inactive employees.
- Total number of departments.
- Employee distribution by department.

## 6. Data Model and Relationships

The initial data model contains three main entities:

### Department
- id
- name
- description

### Employee
- id
- firstName
- lastName
- email
- phone
- hireDate
- salary
- position
- status
- department

### User
- id
- username
- email
- password
- role

Relationships:

- One Department can have multiple Employees.
- One Employee belongs to one Department.
- Each User has one role: ADMIN or HR.

Use appropriate JPA relationships and constraints.

Do not add unnecessary fields or entities without discussing their purpose first.

## 7. Backend Architecture

Organize the backend using a layered architecture.

Suggested packages:

- `controller/`: Handles HTTP requests and responses.
- `service/`: Contains business logic.
- `repository/`: Handles database access.
- `entity/`: Contains JPA entities.
- `dto/`: Contains Data Transfer Objects.
- `mapper/`: Handles entity-to-DTO conversions.
- `exception/`: Handles application exceptions.
- `config/`: Contains application configuration.

Keep business logic out of controllers.

Use DTOs for API communication instead of exposing JPA entities directly.

## 8. Development Guidelines

- Write clean, readable, and maintainable code.
- Follow Java and TypeScript naming conventions.
- Avoid code duplication.
- Validate incoming data on the backend.
- Handle errors consistently.
- Use appropriate HTTP status codes.
- Protect sensitive information.
- Never expose passwords or authentication secrets.
- Never commit passwords, tokens, or secrets to Git.
- Use environment variables for sensitive configuration.
- Follow security best practices.

## 9. Testing Requirements

Every important feature must have appropriate tests.

### Backend
- Unit tests for services.
- Tests for controllers.
- Tests for repositories when appropriate.
- Tests for normal cases and error cases.

### Frontend
- Component tests.
- Tests for important user interactions and application behavior.

Do not consider a feature complete if its tests are failing.

## 10. Rules for Codex

When working on this project:

1. Inspect the existing code before making changes.
2. Understand the existing architecture before implementing a feature.
3. Do not modify unrelated files.
4. Do not delete existing features without approval.
5. Do not invent additional requirements or features.
6. Explain important technical decisions and changes.
7. Work progressively, one feature at a time.
8. Do not generate the entire application in one operation.
9. Run relevant tests after making changes.
10. Verify that the application builds successfully.
11. Ask for clarification before making significant architectural decisions.
12. Preserve existing conventions unless there is a good reason to change them.

## 11. Development Roadmap

Follow this development order:

1. Set up the Spring Boot backend and PostgreSQL database.
2. Implement department management.
3. Implement employee management.
4. Add search, filtering, and pagination.
5. Implement authentication and authorization.
6. Implement user management.
7. Implement the dashboard.
8. Develop the Angular frontend.
9. Develop the Vue.js frontend.
10. Add Docker configuration and CI/CD.
11. Complete tests and documentation.

Do not move to the next major stage until the current stage has been verified.

## 12. Communication

The developer prefers explanations in French.

When explaining implementation steps:

- Use clear and simple French.
- Explain what a change does and why it is necessary.
- Avoid unnecessary technical complexity.
- Provide commands one step at a time.
- Do not assume the developer understands every technical concept.
