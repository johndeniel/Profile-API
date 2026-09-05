## A production-oriented REST API for my personal website, built with Spring Boot, Hibernate/JPA, PostgreSQL, and Vercel Blob.

### Environment Variables

All environment variables are loaded from the `.env` file in the project root.

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_URL` | PostgreSQL JDBC connection string | `jdbc:postgresql://host:5432/db?sslmode=require` |
| `DB_USERNAME` | Database username | `neondb_owner` |
| `DB_PASSWORD` | Database password | `npg_xxxxx` |
| `BLOB_READ_WRITE_TOKEN` | Vercel Blob storage token | `vercel_blob_rw_xxxxx` |

### Tech Stack `Java 21` `Maven 3.8.7` `Spring Boot 4.1.1` `PostgreSQL` `Hibernate / JPA` `Vercel Blob`

```bash
The project includes the Maven Wrapper, so Maven does not need to be installed globally.

./mvnw spring-boot:run         # Using Maven Wrapper (recommended)
mvn spring-boot:run            # Using a system-installed Maven
```



