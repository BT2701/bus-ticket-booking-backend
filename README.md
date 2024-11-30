# Bus-Ticket-Booking-Backend

## Link to Front-end: [Click here](https://github.com/BT2701/bus-ticket-booking-frontend)

## Introduction
This is a web-based bus ticket booking system with three types of users: admin, customer, and staff. The customer category is further divided into logged-in and non-logged-in customers.

## Project Members
- **3121410546**: [Duong Thanh Truong](https://github.com/BT2701)
- **3121410128**: [Pham Van Du](https://github.com/vandu178)
- **3121410149**: [Pham Tan Dat](https://github.com/phamtandat655)
- **3121410542**: [Nguyen Nhat Truong](https://github.com/nhattruong16062003)

## Features
- User authentication and authorization using JWT with Spring Security
- Login via Google, Facebook, and GitHub
- Payment integration with VNPay
- Automatic notifications to customers
- Role-based access control

## Architecture
The system follows a Service-Oriented Architecture (SOA) to ensure scalability and maintainability.

## Technologies Used

The project leverages a variety of modern technologies to ensure robust performance and scalability:

- **Spring Boot**: Simplifies the development of production-ready applications.
- **Spring Security**: Provides comprehensive security services for Java applications.
- **Spring Data JPA**: Facilitates the implementation of JPA-based repositories.
- **REST API**: Enables seamless communication between client and server.
- **Maven**: Manages project build and dependencies.
- **MySQL**: Serves as the relational database management system.
- **Mail**: Integrates email services for notifications and communication.


## Installation
1. Clone the repository
    ```bash
    git clone https://github.com/BT2701/bus-ticket-booking-backend.git
    ```
2. Navigate to the project directory
    ```bash
    cd bus-ticket-booking-backend
    ```
3. Build the project using Maven
    ```bash
    mvn clean install
    ```
4. Run the application
    ```bash
    mvn spring-boot:run
    ```

## Usage
- Access the API at `http://localhost:8080`
- Use the provided endpoints to interact with the system

## Contributing
1. Fork the repository
2. Create a new branch (`git checkout -b feature-branch`)
3. Commit your changes (`git commit -m 'Add some feature'`)
4. Push to the branch (`git push origin feature-branch`)
5. Open a Pull Request

## License
This project is licensed under [Apache License Version 2.0](LICENSE).