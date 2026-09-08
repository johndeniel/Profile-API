# License Certificate API

**Base URL:** `/v1/license-certificate`

The **License Certificate API** manages professional licenses and certifications — including title, issuer, issued date, credential details, and blob storage references — through a full-featured REST interface. It supports complete **CRUD operations**, **paginated listing**, **multi-field filtering**, **text search**, and **sortable results**.

---

## Endpoint Summary

| Method | Endpoint | Operation |
|--------|----------|-----------|
| **`POST`** | [`/v1/license-certificate`](#create-license-certificate) | Create License Certificate |
| **`GET`** | [`/v1/license-certificate`](#get-license-certificates-paginated) | Get License Certificates (Paginated) |
| **`PUT`** | [`/v1/license-certificate/{id}`](#update-license-certificate) | Update License Certificate |
| **`DELETE`** | [`/v1/license-certificate/{id}`](#delete-license-certificate) | Delete License Certificate |

---

## Endpoints

### Create License Certificate

**`POST`** `/v1/license-certificate`

#### Request Body

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| **`uploaderId`** | UUID | **Yes** | Uploader's unique identifier |
| **`title`** | String | **Yes** | Max 255 characters |
| **`issuer`** | String | **Yes** | Max 255 characters |
| **`issued`** | DateTime | **Yes** | Must not be in the future |
| **`level`** | String | **Yes** | Must be `MAIN` or `SUB` |
| `credentialId` | String | No | Max 255 characters |
| `credentialUrl` | String | No | Valid URL, max 2048 characters |
| `description` | String | No | Max 5000 characters |
| `blobUrl` | String | No | Valid URL, max 2048 characters |
| `blobId` | UUID | No | Blob storage identifier |

**Request Example:**

```json
{
  "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
  "title": "AWS Solutions Architect",
  "issuer": "Amazon",
  "issued": "2025-01-01T00:00:00",
  "level": "MAIN",
  "credentialId": "AWS-123-456",
  "credentialUrl": "https://aws.amazon.com/verification/123",
  "description": "AWS Certified Solutions Architect - Professional",
  "blobUrl": "https://example.com/blob/cert.png",
  "blobId": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response:** **`201 Created`**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
  "title": "AWS Solutions Architect",
  "issuer": "Amazon",
  "issued": "2025-01-01T00:00:00",
  "level": "MAIN",
  "credentialId": "AWS-123-456",
  "credentialUrl": "https://aws.amazon.com/verification/123",
  "description": "AWS Certified Solutions Architect - Professional",
  "blobUrl": "https://example.com/blob/cert.png",
  "blobId": "550e8400-e29b-41d4-a716-446655440000",
  "createdAt": "2026-09-08T09:00:00",
  "updatedAt": "2026-09-08T09:00:00"
}
```

---

### Get License Certificates (Paginated)

**`GET`** `/v1/license-certificate`

#### Query Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | `0` | Page number (0-indexed) |
| `size` | int | `10` | Page size (max 100) |
| `sortBy` | String | `createdAt` | Field to sort by. Allowed: `uploaderId`, `title`, `issuer`, `issued`, `level`, `credentialId`, `createdAt`, `updatedAt` |
| `sortDirection` | String | `desc` | Sort direction (`asc` or `desc`) |
| `id` | UUID | - | Filter by exact ID |
| `uploaderId` | UUID | - | Filter by uploader ID |
| `search` | String | - | Search across `title`, `issuer`, `credentialId` |
| `title` | String | - | Filter by title (LIKE, case-insensitive) |
| `issuer` | String | - | Filter by issuer (LIKE, case-insensitive) |
| `level` | String | - | Filter by level (equals, case-insensitive: `MAIN` or `SUB`) |

**Response:** **`200 OK`**

```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
      "title": "AWS Solutions Architect",
      "issuer": "Amazon",
      "issued": "2025-01-01T00:00:00",
      "level": "MAIN",
      "credentialId": "AWS-123-456",
      "credentialUrl": "https://aws.amazon.com/verification/123",
      "description": "AWS Certified Solutions Architect - Professional",
      "blobUrl": "https://example.com/blob/cert.png",
      "blobId": "550e8400-e29b-41d4-a716-446655440000",
      "createdAt": "2026-09-08T09:00:00",
      "updatedAt": "2026-09-08T09:00:00"
    }
  ],
  "totalElements": 50,
  "totalPages": 5,
  "currentPage": 0,
  "size": 10
}
```

---

### Update License Certificate

**`PUT`** `/v1/license-certificate/{id}`

**Path Parameter:** `id` (UUID) - License certificate ID

#### Request Body

_All fields optional for partial update._

| Field | Type | Constraints |
|-------|------|-------------|
| `uploaderId` | UUID | Uploader's unique identifier |
| `title` | String | Must not be blank, max 255 characters |
| `issuer` | String | Must not be blank, max 255 characters |
| `issued` | DateTime | Must not be in the future |
| `level` | String | Must be `MAIN` or `SUB` |
| `credentialId` | String | Max 255 characters |
| `credentialUrl` | String | Must not be blank, valid URL, max 2048 characters |
| `description` | String | Max 5000 characters |
| `blobUrl` | String | Must not be blank, valid URL, max 2048 characters |
| `blobId` | UUID | Blob storage identifier |

**Request Example:**

```json
{
  "title": "AWS Solutions Architect - Professional",
  "level": "SUB"
}
```

**Response:** **`200 OK`**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "AWS Solutions Architect - Professional",
  "issuer": "Amazon",
  "issued": "2025-01-01T00:00:00",
  "level": "SUB",
  "credentialId": "AWS-123-456",
  "credentialUrl": "https://aws.amazon.com/verification/123",
  "description": "AWS Certified Solutions Architect - Professional",
  "blobUrl": "https://example.com/blob/cert.png",
  "blobId": "550e8400-e29b-41d4-a716-446655440000",
  "createdAt": "2026-09-08T09:00:00",
  "updatedAt": "2026-09-08T10:00:00"
}
```

---

### Delete License Certificate

**`DELETE`** `/v1/license-certificate/{id}`

**Path Parameter:** `id` (UUID) - License certificate ID

**Response:** **`204 No Content`**

_No response body._
