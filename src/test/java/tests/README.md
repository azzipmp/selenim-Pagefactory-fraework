# Rest Assured API Tests

This folder contains TestNG-based API tests implemented with Rest Assured.

## Main API Test Class

- `RestAssuredApiTest.java`

## Coverage

The API test class currently includes:

- GET request validation
- GET response deserialization to POJO
- POST request validation
- POST response deserialization to POJO
- JSON schema contract validation
- Mocked GET validation using WireMock

## Supporting Files

- POJO model: `model/ApiPost.java`
- JSON schema: `../resources/schemas/post-schema.json`

## Test Groups

- `smoke`
- `regression`
- `api`
- `contract`
- `mock`

## Commands

Run only the API test class:

```bash
mvn test "-Dtest=tests.RestAssuredApiTest"
```

Run smoke API tests:

```bash
mvn test "-Dtest=tests.RestAssuredApiTest" "-Dgroups=smoke"
```

Run regression API tests:

```bash
mvn test "-Dtest=tests.RestAssuredApiTest" "-Dgroups=regression"
```

Run mock API tests:

```bash
mvn test "-Dtest=tests.RestAssuredApiTest" "-Dgroups=mock"
```

## Notes

- Contract validation uses `matchesJsonSchemaInClasspath("schemas/post-schema.json")`.
- Mock API testing uses a dynamic WireMock server port at runtime.
- Extent reporting is enabled through `ExtentTestListener`.
