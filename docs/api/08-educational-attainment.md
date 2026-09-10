# Educational Attainment API

**Base URL:** `/v1/educational-attainment`

The **Educational Attainment API** manages education records — including institution, degree, field of study, award, and attendance dates — through a full-featured REST interface. It supports complete **CRUD operations**, **paginated listing**, **multi-field filtering**, **text search**, and **sortable results**.

---

## Endpoint Summary

| Method | Endpoint | Operation |
|--------|----------|-----------|
| **`POST`** | [`/v1/educational-attainment`](#create-educational-attainment) | Create Educational Attainment |
| **`GET`** | [`/v1/educational-attainment`](#get-educational-attainments-paginated) | Get Educational Attainments (Paginated) |
| **`PUT`** | [`/v1/educational-attainment/{id}`](#update-educational-attainment) | Update Educational Attainment |
| **`DELETE`** | [`/v1/educational-attainment/{id}`](#delete-educational-attainment) | Delete Educational Attainment |

---

## Endpoints

### Create Educational Attainment

**`POST`** `/v1/educational-attainment`

#### Headers

| Header | Required | Description |
|--------|----------|-------------|
| **`Idempotency-Key`** | **Yes** | Unique key for idempotent request (max 64 chars) |

#### Request Body

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| **`uploaderId`** | UUID | **Yes** | Uploader's unique identifier |
| **`institution`** | String | **Yes** | Max 255 characters |
| **`startDate`** | DateTime | **Yes** | Must not be in the future, must not be after `endDate` |
| `endDate` | DateTime | No | Must not be in the future, nullable, must not be before `startDate` |
| `award` | String | No | Max 255 characters |
| `degree` | String | No | Max 255 characters |
| `field` | String | No | Max 255 characters |

**Request Example:**

```json
{
  "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
  "institution": "Bulacan State University",
  "startDate": "2018-06-01T00:00:00",
  "endDate": "2022-05-30T00:00:00",
  "award": "Cum Laude",
  "degree": "Bachelor's Degree",
  "field": "Information Technology"
}
```

**Response:** **`201 Created`**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
  "institution": "Bulacan State University",
  "startDate": "2018-06-01T00:00:00",
  "endDate": "2022-05-30T00:00:00",
  "award": "Cum Laude",
  "degree": "Bachelor's Degree",
  "field": "Information Technology",
  "createdAt": "2026-09-09T09:00:00",
  "updatedAt": "2026-09-09T09:00:00"
}
```

---

### Get Educational Attainments (Paginated)

**`GET`** `/v1/educational-attainment`

#### Query Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | `0` | Page number (0-indexed) |
| `size` | int | `10` | Page size (max 100) |
| `sortBy` | String | `createdAt` | Field to sort by. Allowed: `uploaderId`, `institution`, `startDate`, `endDate`, `award`, `degree`, `field`, `createdAt`, `updatedAt` |
| `sortDirection` | String | `desc` | Sort direction (`asc` or `desc`) |
| `id` | UUID | - | Filter by exact ID |
| `uploaderId` | UUID | - | Filter by uploader ID |
| `search` | String | - | Search across `institution`, `degree`, `field`, `award` |
| `institution` | String | - | Filter by institution (LIKE, case-insensitive) |
| `degree` | String | - | Filter by degree (LIKE, case-insensitive) |
| `field` | String | - | Filter by field (LIKE, case-insensitive) |
| `award` | String | - | Filter by award (LIKE, case-insensitive) |

**Response:** **`200 OK`**

```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440002",
      "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
      "institution": "Bulacan State University",
      "startDate": "2018-06-01T00:00:00",
      "endDate": "2022-05-30T00:00:00",
      "award": "Cum Laude",
      "degree": "Bachelor's Degree",
      "field": "Information Technology",
      "createdAt": "2026-09-09T09:00:00",
      "updatedAt": "2026-09-09T09:00:00"
    }
  ],
  "totalElements": 50,
  "totalPages": 5,
  "currentPage": 0,
  "size": 10
}
```

---

### Update Educational Attainment

**`PUT`** `/v1/educational-attainment/{id}`

**Path Parameter:** `id` (UUID) - Educational attainment ID

#### Request Body

_All fields optional for partial update._

| Field | Type | Constraints |
|-------|------|-------------|
| `uploaderId` | UUID | Uploader's unique identifier |
| `institution` | String | Must not be blank, max 255 characters |
| `startDate` | DateTime | Must not be in the future, must not be after `endDate` |
| `endDate` | DateTime | Must not be in the future, nullable, must not be before `startDate` |
| `award` | String | Max 255 characters |
| `degree` | String | Max 255 characters |
| `field` | String | Max 255 characters |

**Request Example:**

```json
{
  "degree": "Master's Degree",
  "field": "Computer Science",
  "endDate": null
}
```

**Response:** **`200 OK`**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
  "institution": "Bulacan State University",
  "startDate": "2018-06-01T00:00:00",
  "endDate": null,
  "award": "Cum Laude",
  "degree": "Master's Degree",
  "field": "Computer Science",
  "createdAt": "2026-09-09T09:00:00",
  "updatedAt": "2026-09-09T10:00:00"
}
```

---

### Delete Educational Attainment

**`DELETE`** `/v1/educational-attainment/{id}`

**Path Parameter:** `id` (UUID) - Educational attainment ID

**Response:** **`204 No Content`**

_No response body._
