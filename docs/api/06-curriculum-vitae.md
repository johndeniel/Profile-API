# Curriculum Vitae API

**Base URL:** `/v1/curriculum-vitae`

The **Curriculum Vitae API** manages CV documents — including blob storage references and issued date — through a REST interface. It supports **create**, **paginated listing**, and **delete** operations, all protected by **idempotency** on POST requests.

---

## Endpoint Summary

| Method | Endpoint | Operation |
|--------|----------|-----------|
| **`POST`** | [`/v1/curriculum-vitae`](#create-curriculum-vitae) | Create Curriculum Vitae |
| **`GET`** | [`/v1/curriculum-vitae`](#get-curriculum-vitaes-paginated) | Get Curriculum Vitaes (Paginated) |
| **`PUT`** | [`/v1/curriculum-vitae/{id}`](#update-curriculum-vitae) | Update Curriculum Vitae |
| **`DELETE`** | [`/v1/curriculum-vitae/{id}`](#delete-curriculum-vitae) | Delete Curriculum Vitae |

---

## Endpoints

### Create Curriculum Vitae

**`POST`** `/v1/curriculum-vitae`

#### Headers

| Header | Required | Description |
|--------|----------|-------------|
| **`Idempotency-Key`** | **Yes** | Unique key for idempotent request (max 64 chars) |

#### Request Body

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| **`uploaderId`** | UUID | **Yes** | Uploader's unique identifier |
| **`issued`** | DateTime | **Yes** | Must not be in the future |
| `blobUrl` | String | No | Valid URL, max 2048 characters |
| `blobId` | UUID | No | Blob storage identifier |

**Request Example:**

```json
{
  "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
  "issued": "2025-06-01T00:00:00",
  "blobUrl": "https://example.com/blob/cv.pdf",
  "blobId": "550e8400-e29b-41d4-a716-446655440001"
}
```

**Response:** **`201 Created`**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
  "blobUrl": "https://example.com/blob/cv.pdf",
  "blobId": "550e8400-e29b-41d4-a716-446655440001",
  "issued": "2025-06-01T00:00:00",
  "createdAt": "2026-09-09T09:00:00",
  "updatedAt": "2026-09-09T09:00:00"
}
```

---

### Get Curriculum Vitaes (Paginated)

**`GET`** `/v1/curriculum-vitae`

#### Query Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | `0` | Page number (0-indexed) |
| `size` | int | `10` | Page size (max 100) |
| `sortBy` | String | `createdAt` | Field to sort by. Allowed: `uploaderId`, `issued`, `createdAt`, `updatedAt` |
| `sortDirection` | String | `desc` | Sort direction (`asc` or `desc`) |
| `id` | UUID | - | Filter by exact ID |
| `uploaderId` | UUID | - | Filter by uploader ID |

**Response:** **`200 OK`**

```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440002",
      "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
      "blobUrl": "https://example.com/blob/cv.pdf",
      "blobId": "550e8400-e29b-41d4-a716-446655440001",
      "issued": "2025-06-01T00:00:00",
      "createdAt": "2026-09-09T09:00:00",
      "updatedAt": "2026-09-09T09:00:00"
    }
  ],
  "totalElements": 12,
  "totalPages": 2,
  "currentPage": 0,
  "size": 10
}
```

---

### Update Curriculum Vitae

**`PUT`** `/v1/curriculum-vitae/{id}`

**Path Parameter:** `id` (UUID) - Curriculum Vitae ID

#### Request Body

_All fields optional for partial update._

| Field | Type | Constraints |
|-------|------|-------------|
| `uploaderId` | UUID | Uploader's unique identifier |
| `blobUrl` | String | Must not be blank, valid URL, max 2048 characters |
| `blobId` | UUID | Blob storage identifier |
| `issued` | DateTime | Must not be in the future |

**Request Example:**

```json
{
  "blobUrl": "https://example.com/blob/cv-updated.pdf",
  "issued": "2025-09-01T00:00:00"
}
```

**Response:** **`200 OK`**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
  "blobUrl": "https://example.com/blob/cv-updated.pdf",
  "blobId": "550e8400-e29b-41d4-a716-446655440001",
  "issued": "2025-09-01T00:00:00",
  "createdAt": "2026-09-09T09:00:00",
  "updatedAt": "2026-09-09T10:00:00"
}
```

---

### Delete Curriculum Vitae

**`DELETE`** `/v1/curriculum-vitae/{id}`

**Path Parameter:** `id` (UUID) - Curriculum Vitae ID

**Response:** **`204 No Content`**

_No response body._
