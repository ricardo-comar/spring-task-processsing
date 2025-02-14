# POC Spring Task

This project is a Proof of Concept (POC) for demonstrating the use of Spring Boot to handle tasks with a task pool. It includes a REST API for managing event data and processing events.

## Purpose

The purpose of this project is to showcase how to:
- Create a RESTful API using Spring Boot.
- Handle HTTP POST and GET requests.
- Process events asynchronously.

## Prerequisites

- Java 11 or higher
- Maven
- An IDE (e.g., VS Code, Eclipse)


## Running the Project Locally

1. **Clone the repository:**
    ```sh
    git clone https://github.com/your-username/spring-task-processsing.git
    cd spring-task-processsing
    ```
1. **Start a local CouchBase Docker Instance**
    ```
    docker-compose run -d
    ```

1. **Build the project using Maven:**
    ```sh
    mvn clean install
    ```

1. **Run the Spring Boot application:**
    ```sh
    mvn spring-boot:run
    ```
    - The application will be running at `http://localhost:8080`.
    - You can access the Swagger UI for API documentation and testing at `http://localhost:8080/swagger-ui.html`.
    - You can also use tools like Postman or curl to interact with the API.

1. **Sending Events**
    - With the POST endpoint, you can send a new event to be processed. It will be replied back with a state NEW.
    - If you query it within 5 seconds, it will be (I hope so) replied with state IN_PROGRESS.
    - After 5 seconds it will be changed to COMPLETED.
    - If you send one event with the same *evendId*, it will be processed again using the new payload, but the previous payload in couchbase will be replaced by the new one. And, if you do it within 5 seconds, the semaphore will block the second execution until the first one is finished.

## Async Configuration

The project uses Spring's `@EnableAsync` annotation to enable asynchronous processing. The configuration is defined in the `AsyncConfig` class, which sets up the task executor with the following parameters. You can find the configuration details in the [`AsyncConfig`](src/main/java/com/example/poc/AsyncConfig.java) file.

- **Core Pool Size:** The number of threads to keep in the pool, even if they are idle.
- **Max Pool Size:** The maximum number of threads to allow in the pool.
- **Queue Capacity:** The size of the queue to hold tasks before they are executed.
- **Thread Name Prefix:** The prefix to use for the names of newly created threads.

These parameters can be adjusted to optimize the performance based on the application's requirements.

## Semaphore in EventDocProcessor

The `EventDocProcessor` class uses a semaphore to control concurrent access to event processing. The semaphore ensures that only one thread can process an event with a specific `eventId` at a time, preventing race conditions and ensuring data consistency.

### Purpose of the Semaphore

- **Concurrency Control:** Limits the number of threads that can access the event processing logic simultaneously.
- **Data Consistency:** Ensures that an event with the same `eventId` is not processed by multiple threads concurrently, avoiding conflicts and data corruption.
- **Resource Management:** Helps manage system resources by controlling the number of concurrent event processing tasks.

The semaphore is configured to allow a single permit for each unique `eventId`, effectively serializing the processing of events with the same ID.

You can find the semaphore implementation details in the [`EventDocProcessor`](src/main/java/com/example/poc/EventDocProcessor.java) file.

## API Endpoints

- **POST /api/data**
    - Description: Save event data and process the event.
    - Request Body: `EventNotification` JSON object.
    - Response: `EventDoc` JSON object with HTTP status 201 (Created) or 400 (Bad Request).

- **GET /api/data/user/{id}**
    - Description: Retrieve event data by user ID.
    - Path Variable: `id` (String)
    - Response: `EventDoc` JSON object with HTTP status 200 (OK) or 404 (Not Found).

## License

This project is licensed under the MIT License.