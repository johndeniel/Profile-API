## A production-oriented REST API for my personal website, built with Spring Boot, Hibernate/JPA, PostgreSQL, and Vercel Blob.

### Tech Stack

- Java 21
- Spring Boot 4.1.1
- Spring Data JPA
- PostgreSQL (Neon)
- Vercel Blob Storage
- Springdoc OpenAPI (Swagger UI)
- Lombok

### Build and Run

The project includes the Maven Wrapper, so Maven does not need to be installed globally.

```bash
./mvnw spring-boot:run         # Using Maven Wrapper (recommended)
mvn spring-boot:run            # Using a system-installed Maven
```

### Environment Variables

All environment variables are loaded from the `.env` file in the project root.

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_URL` | PostgreSQL JDBC connection string | `jdbc:postgresql://host:5432/db?sslmode=require` |
| `DB_USERNAME` | Database username | `neondb_owner` |
| `DB_PASSWORD` | Database password | `npg_xxxxx` |
| `BLOB_READ_WRITE_TOKEN` | Vercel Blob storage token | `vercel_blob_rw_xxxxx` |

### Vercel Blob Integration

The API uses [Vercel Blob](https://vercel.com/docs/storage/vercel-blob) for file storage. The `VercelBlobService` provides:

```java
@Autowired
private VercelBlobService blobService;

// Upload with auto-generated unique name
String url = blobService.uploadWithRandomSuffix("profile/image.jpg", data, "image/jpeg");

// Upload with specific pathname
String url = blobService.upload("profile/user-123.jpg", data, "image/jpeg");

// Download
byte[] file = blobService.download("profile/user-123.jpg");

// Delete
blobService.delete("profile/user-123.jpg");
```

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/v1/personal-information` | Create personal information |
| GET | `/v1/personal-information` | Get all (with search, filter, sort, pagination) |
| PUT | `/v1/personal-information/{id}` | Update by ID |
| DELETE | `/v1/personal-information/{id}` | Delete by ID |

### Swagger UI

Once the application is running, access Swagger UI at:
```
http://localhost:8080/swagger-ui/index.html
```
