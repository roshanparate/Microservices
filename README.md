# Microservices

A Java/Spring Boot sample banking application split into three independently
runnable REST services:

- **Accounts** manages customer and bank-account records.
- **Loans** creates and manages loan records associated with a mobile number.
- **Cards** creates and manages card records associated with a mobile number.

Each service exposes JSON CRUD endpoints, validates incoming data, uses Spring
Data JPA with its own in-memory H2 database, and runs on a separate port. The
services are Maven modules in the root aggregator project; they do not require
an API gateway to run locally.

| Service | Port | Base URL |
| --- | ---: | --- |
| Accounts | 8787 | `http://localhost:8787` |
| Loans | 8090 | `http://localhost:8090` |
| Cards | 9000 | `http://localhost:9000` |

## Requirements

- JDK 26 for the full Maven reactor (the Accounts and Loans modules target
  Java 26; Cards targets Java 25).
- Internet access on the first build so Maven can download dependencies.
- Maven installed, or use the Maven Wrapper included in each service directory.

## Installation and build

Clone the repository, then from the project root build all modules:

```shell
git clone https://github.com/roshanparate/Microservices.git
cd Microservices
mvn clean install
```

On Windows, if Maven is not installed, run a module's wrapper from that
module's directory, for example:

```powershell
cd accounts
.\mvnw.cmd clean package
```

The `mvnw` wrapper script is also available for macOS/Linux.

## Run the application

Start each service in a separate terminal from the project root:

```shell
cd accounts
.\mvnw.cmd spring-boot:run
```

```shell
cd loans
.\mvnw.cmd spring-boot:run
```

```shell
cd cards
.\mvnw.cmd spring-boot:run
```

Alternatively, build the JAR in a service directory and run it with
`java -jar target/<service>-0.0.1-SNAPSHOT.jar`.

The development configuration uses in-memory H2 databases, so data is
temporary and is cleared when its service restarts. There is also a Docker
Compose file at `accounts/docker-compose.yml` for starting the configured
container images, if those images are available to your Docker environment.

## API documentation

All endpoints use the `/api` path prefix and JSON responses. No authentication
is configured for these sample endpoints. Every service includes generated API
documentation at `/swagger-ui/index.html` and `/v3/api-docs` on its own base
URL.

### Accounts

| Method | Path | Purpose |
| --- | --- | --- |
| `POST` | `/api/create` | Create a customer and account; JSON body is a `CustomerDto`. |
| `GET` | `/api/fetch?mobileNumber={10 digits}` | Fetch customer and account details. |
| `PUT` | `/api/update` | Update customer and account details; JSON body is a `CustomerDto`. |
| `DELETE` | `/api/delete?mobileNumber={10 digits}` | Delete customer and account details. |

Example create request:

```shell
curl.exe -i -X POST "http://localhost:8787/api/create" -H "Content-Type: application/json" --data-raw '{"name":"Alex Morgan","email":"alex@example.com","mobileNumber":"9345432123","accountsDto":{"accountNumber":3454433243,"accountType":"Savings","branchAddress":"123 Main Street, New York"}}'
```

Example fetch request:

```shell
curl.exe -i "http://localhost:8787/api/fetch?mobileNumber=9345432123"
```

The customer name must be 5–30 characters, the email must be valid, and the
mobile number must contain 10 digits. The account object requires an account
number, account type, and branch address.

### Loans

| Method | Path | Purpose |
| --- | --- | --- |
| `POST` | `/api/create?mobileNumber={10 digits}` | Create a loan for the mobile number. |
| `GET` | `/api/fetch?mobileNumber={10 digits}` | Fetch loan details. |
| `PUT` | `/api/update` | Update loan details; JSON body is a `LoansDto`. |
| `DELETE` | `/api/delete?mobileNumber={10 digits}` | Delete loan details. |

Example create and fetch requests:

```shell
curl.exe -i -X POST "http://localhost:8090/api/create?mobileNumber=9345432123"
curl.exe -i "http://localhost:8090/api/fetch?mobileNumber=9345432123"
```

Example update request:

```shell
curl.exe -i -X PUT "http://localhost:8090/api/update" -H "Content-Type: application/json" --data-raw '{"mobileNumber":"9345432123","loanNumber":"548732457654","loanType":"Home Loan","totalLoan":100000,"amountPaid":1000,"outstandingAmount":99000}'
```

### Cards

| Method | Path | Purpose |
| --- | --- | --- |
| `POST` | `/api/create?mobileNumber={10 digits}` | Create a card for the mobile number. |
| `GET` | `/api/fetch?mobileNumber={10 digits}` | Fetch card details. |
| `PUT` | `/api/update` | Update card details; JSON body is a `CardsDto`. |
| `DELETE` | `/api/delete?mobileNumber={10 digits}` | Delete card details. |

Example create and fetch requests:

```shell
curl.exe -i -X POST "http://localhost:9000/api/create?mobileNumber=9345432123"
curl.exe -i "http://localhost:9000/api/fetch?mobileNumber=9345432123"
```

Example update request:

```shell
curl.exe -i -X PUT "http://localhost:9000/api/update" -H "Content-Type: application/json" --data-raw '{"mobileNumber":"9345432123","cardNumber":"100646930341","cardType":"Credit Card","totalLimit":100000,"amountUsed":1000,"availableAmount":99000}'
```

Loan and card mobile numbers must contain 10 digits; loan/card numbers must
contain 12 digits. Loan/card types must not be empty, total amounts or limits
must be positive, and paid/used/outstanding/available amounts must be
non-negative.

### Responses

Create operations return HTTP `201`; successful fetch, update, and delete
operations return HTTP `200`. Create and mutation responses use this shape:

```json
{
  "statusCode": "201",
  "statusMsg": "Account created successfully"
}
```

The status message depends on the service and operation. An update or delete
that cannot be completed returns HTTP `417` with a response message explaining
the operation failed. Invalid request data is rejected by validation.

## Troubleshooting

- **The build reports an unsupported Java release:** Check `java -version` and
  `JAVA_HOME`; the Accounts and Loans modules require JDK 26. Then rerun the
  build from the project root.
- **A service fails to start because its port is occupied:** Stop the other
  process using that port, or start the service with another port, for example
  `.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=8788"`.
- **A request returns a validation error:** Check that query-string mobile
  numbers contain exactly 10 digits, loan/card numbers contain 12 digits, JSON
  field names match the examples, and required numeric values meet the
  constraints above.
- **An update or delete returns `417`:** The service could not complete the
  operation for the supplied record. Confirm the record exists and that the
  identifying fields in the request are correct.
- **Data disappears after restart:** This is expected with the configured
  in-memory H2 databases. Use a persistent database configuration if records
  need to survive restarts.
- **The API docs page is unavailable:** Confirm that the service is running,
  use the matching service port, and open
  `http://localhost:<port>/swagger-ui/index.html`.
