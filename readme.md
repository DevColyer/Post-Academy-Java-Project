# NorseMythologyWiki API

Welcome to the NorseMythologyWiki API! This project provides a backend API for a wiki on Norse mythology. Follow the instructions below to set up the application and contribute to its development.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Initial Setup](#initial-setup)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## Prerequisites

Before setting up the application, ensure you have the following installed:

- [Java JDK 11+](https://adoptopenjdk.net/)
- [MySQL Database](https://dev.mysql.com/downloads/)
- [Maven](https://maven.apache.org/download.cgi) (for building the project)

## Initial Setup

1. **Set up the MySQL Database:**

   - Create a new database in MySQL.
   - Run the provided `DatabaseSetup.sql` to set up the necessary tables and schema. You can execute this script using a MySQL client or command-line tool:

     ```sh
     mysql -u <username> -p <database_name> < DatabaseSetup.sql
     ```

2. **Configure the Database Connection:**

   Create an `application.properties` file in the `src/main/resources` directory of your project. Use the following template, replacing placeholders with your actual database details and JWT secret:

   ```properties
   spring.application.name=NorseMythologyWiki
   spring.datasource.url=jdbc:mysql://localhost:3306/your-db-here
   spring.datasource.username=username
   spring.datasource.password=password

   jwt.cookie.name=jwt
   # 10hr in ms
   jwt.token.validity=36000000
   jwt.secret=your-secret-here

   spring.banner.location=classpath:banner.txt
   spring.banner.charset=UTF-8

   springdoc.api-docs.enabled=true
   springdoc.swagger-ui.enabled=true
```

3. **Setup Bean Initialization:**

   The first run of the application requires the setup bean to be initialized. Ensure that you configure this bean in your main application file (`NorseMythologyWikiApplication.java`). For example:

   ```java
    @Bean
    CommandLineRunner setup(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Set<String> adminRoles = new HashSet<>();
            adminRoles.add("ROLE_ADMIN");
            userRepository.save(new User("admin", passwordEncoder.encode("password"), adminRoles));

            Set<String> userRoles = new HashSet<>();
            userRoles.add("ROLE_USER");
            userRepository.save(new User("user", passwordEncoder.encode("password"), userRoles));
        };
    }
```

## Running the Application

1. **Build the Project:**

   Navigate to the root directory of the project and use Maven to build the project:

   ```sh
   mvn clean install
```
2. **Run the Application:**

   Start the application using the following command:

   ```sh
   mvn spring-boot:run
```
Alternatively, if you have a packaged JAR file, you can run it with:

   ```sh
   java -jar target/norse-mythology-wiki-api.jar
```
By default, the application will start on port 8080. You can change the port by specifying it in the application.properties file:
   ```properties
   server.port=8081
```
3. **Access the API Documentation:**

   Once the application is running, you can access the API documentation through Swagger UI:

   - **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

   Swagger UI provides an interactive interface to explore and test the API endpoints. It will allow you to see the available endpoints, their required parameters, and the responses they return.

   Additionally, you can access the OpenAPI JSON documentation:

   - **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

   This URL provides a structured format of the API documentation in JSON, which can be useful for integration with other tools or for generating client libraries.

4. **Stopping the Application:**

   To stop the application, you can terminate the running process in your terminal or command prompt. If you are running the application using Maven, you can stop it by pressing `Ctrl+C` in the terminal where it is running.

   If you are running the application as a JAR file, use `Ctrl+C` in the terminal to stop the process.

## Testing

To ensure that the application functions correctly, run the unit tests and integration tests using Maven:

```sh
mvn test
```
Running tests is crucial to verify that your changes do not introduce new issues. Ensure all tests pass before submitting any changes to maintain the application's stability and reliability.

## Contributing

We welcome contributions from the community! To contribute to the NorseMythologyWiki API, please follow these guidelines:

1. **Fork the Repository:**

   Create a fork of the repository on GitHub to your own account. This allows you to make changes without affecting the main repository.

2. **Clone Your Fork:**

   Clone your fork to your local machine:

   ```sh
   git clone https://github.com/your-username/norse-mythology-wiki-api.git
```
4. **Create a New Branch:**

   Create a new branch for your changes:

   ```sh
   git checkout -b feature/your-feature
```
Certainly! Here’s the continuation from the “Create a New Branch” section in Markdown format:

markdown

4. **Create a New Branch:**

   Create a new branch for your changes:

   ```sh
   git checkout -b feature/your-feature

Choose a descriptive name for the branch based on the feature or fix you are working on. For example, use names like feature/user-authentication or bugfix/login-error.
5. **Make Your Changes:**

   Implement your changes or new features in the codebase. Make sure to follow the project's coding standards and conventions. Consider the following while making changes:

   - **Code Quality:** Ensure your code is clean, efficient, and easy to understand. Avoid unnecessary complexity and adhere to best practices.
   - **Documentation:** Update the documentation if your changes affect the API or functionality. This includes modifying the `README.md` file and any relevant inline comments to clearly describe the new features or bug fixes.
   - **Testing:** Write or update unit tests to cover the changes you made. Ensure that your modifications do not break existing functionality.

   After making your changes, thoroughly test the application to verify that your changes work as expected and do not introduce new issues.

6. **Commit Your Changes:**

   Once you've verified that your changes are working correctly, commit them to your branch:

   ```sh
   git add .
   git commit -m "Add feature: description of the feature"
```
Ensure that your commit messages are clear and descriptive. Follow the format Add feature: description of the feature or Fix bug: description of the bug. This helps maintain a clear project history and makes it easier for reviewers to understand the purpose of the changes.
7. **Push Your Changes:**

   Push your changes to your fork on GitHub:

   ```sh
   git push origin feature/your-feature
```
This command uploads your local branch and commits to your GitHub repository, making them available for review.
8. **Create a Pull Request:**

   Navigate to the original repository on GitHub to create a pull request (PR) from your forked repository. Follow these steps:

   - **Go to the “Pull Requests” Tab:** Access the “Pull Requests” section of the original repository on GitHub.
   - **Click the “New Pull Request” Button:** This will open a new page where you can create a pull request.
   - **Select Your Branch:** From the “compare” dropdown menu, select your feature branch from your fork.
   - **Provide a Title and Description:** Write a clear and descriptive title for your pull request. In the description field, explain the changes you’ve made, why they are necessary, and any additional context that might be helpful. Include any related issue numbers if applicable.
   - **Assign Reviewers:** If appropriate, assign reviewers who can provide feedback on your changes.
   - **Apply Labels:** Add any relevant labels (e.g., `enhancement`, `bug`, `documentation`) if the project uses them. This helps categorize and prioritize the pull request.

   After filling out the required information, click the “Create Pull Request” button to submit your PR.

### Contributor Guidelines

- **Code Style:** Follow the project's existing coding style to maintain consistency across the codebase. Adhere to any style guides or conventions provided within the project.

- **Documentation:** Update the documentation if your changes affect the API or application functionality. This includes updating the `README.md` file and any inline comments to accurately reflect the changes made.

- **Testing:** Ensure that your changes include adequate tests and that all tests pass. Thorough testing is crucial to prevent bugs and ensure the stability of the application.

- **Commit Messages:** Use clear, concise commit messages that describe the changes made. Follow the format `Add feature: description` or `Fix bug: description` to make the commit history informative and easy to understand.

- **Code Reviews:** Participate in reviewing other contributors' pull requests if you have the expertise and time. Provide constructive feedback to help improve the quality of contributions and maintain high standards for the project.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

Thank you for contributing to the NorseMythologyWiki API! If you have any questions or need further assistance, feel free to reach out or open an issue in the repository. We appreciate your contributions and support in improving this project!

