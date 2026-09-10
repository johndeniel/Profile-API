# Professional Experience API

**Base URL:** `/v1/professional-experience`

The **Professional Experience API** manages work experience records — including title, company, type, location, and employment dates — through a full-featured REST interface. It supports complete **CRUD operations**, **paginated listing**, **multi-field filtering**, **text search**, and **sortable results**.

---

## Endpoint Summary

| Method | Endpoint | Operation |
|--------|----------|-----------|
| **`POST`** | [`/v1/professional-experience`](#create-professional-experience) | Create Professional Experience |
| **`GET`** | [`/v1/professional-experience`](#get-professional-experiences-paginated) | Get Professional Experiences (Paginated) |
| **`PUT`** | [`/v1/professional-experience/{id}`](#update-professional-experience) | Update Professional Experience |
| **`DELETE`** | [`/v1/professional-experience/{id}`](#delete-professional-experience) | Delete Professional Experience |

---

## Endpoints

### Create Professional Experience

**`POST`** `/v1/professional-experience`

#### Headers

| Header | Required | Description |
|--------|----------|-------------|
| **`Idempotency-Key`** | **Yes** | Unique key for idempotent request (max 64 chars) |

#### Request Body

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| **`uploaderId`** | UUID | **Yes** | Uploader's unique identifier |
| **`title`** | String | **Yes** | Max 255 characters |
| **`company`** | String | **Yes** | Max 255 characters |
| **`type`** | String | **Yes** | Must be `FULL_TIME`, `PART_TIME`, `CONTRACT`, `INTERNSHIP`, `FREELANCE`, or `SELF_EMPLOYED` |
| **`startDate`** | DateTime | **Yes** | Must not be in the future, must not be after `endDate` |
| `location` | String | No | Max 255 characters |
| `endDate` | DateTime | No | Must not be in the future, nullable, must not be before `startDate` |

**Request Example:**

```json
{
  "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Senior Software Engineer",
  "company": "Acme Corp",
  "type": "FULL_TIME",
  "location": "San Francisco, CA",
  "startDate": "2023-01-15T00:00:00",
  "endDate": "2025-08-01T00:00:00"
}
```

**Response:** **`201 Created`**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Senior Software Engineer",
  "company": "Acme Corp",
  "type": "FULL_TIME",
  "location": "San Francisco, CA",
  "startDate": "2023-01-15T00:00:00",
  "endDate": "2025-08-01T00:00:00",
  "createdAt": "2026-09-09T09:00:00",
  "updatedAt": "2026-09-09T09:00:00"
}
```

---

### Get Professional Experiences (Paginated)

**`GET`** `/v1/professional-experience`

#### Query Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | `0` | Page number (0-indexed) |
| `size` | int | `10` | Page size (max 100) |
| `sortBy` | String | `createdAt` | Field to sort by. Allowed: `uploaderId`, `title`, `company`, `type`, `location`, `startDate`, `endDate`, `createdAt`, `updatedAt` |
| `sortDirection` | String | `desc` | Sort direction (`asc` or `desc`) |
| `id` | UUID | - | Filter by exact ID |
| `uploaderId` | UUID | - | Filter by uploader ID |
| `search` | String | - | Search across `title`, `company`, `location` |
| `title` | String | - | Filter by title (LIKE, case-insensitive) |
| `company` | String | - | Filter by company (LIKE, case-insensitive) |
| `type` | String | - | Filter by type (equals, case-insensitive: `FULL_TIME`, `PART_TIME`, `CONTRACT`, `INTERNSHIP`, `FREELANCE`, `SELF_EMPLOYED`) |
| `location` | String | - | Filter by location (LIKE, case-insensitive) |

**Response:** **`200 OK`**

```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440002",
      "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
      "title": "Senior Software Engineer",
      "company": "Acme Corp",
      "type": "FULL_TIME",
      "location": "San Francisco, CA",
      "startDate": "2023-01-15T00:00:00",
      "endDate": "2025-08-01T00:00:00",
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

### Update Professional Experience

**`PUT`** `/v1/professional-experience/{id}`

**Path Parameter:** `id` (UUID) - Professional experience ID

#### Request Body

_All fields optional for partial update._

| Field | Type | Constraints |
|-------|------|-------------|
| `uploaderId` | UUID | Uploader's unique identifier |
| `title` | String | Must not be blank, max 255 characters |
| `company` | String | Must not be blank, max 255 characters |
| `type` | String | Must be `FULL_TIME`, `PART_TIME`, `CONTRACT`, `INTERNSHIP`, `FREELANCE`, or `SELF_EMPLOYED` |
| `location` | String | Max 255 characters |
| `startDate` | DateTime | Must not be in the future, must not be after `endDate` |
| `endDate` | DateTime | Must not be in the future, nullable, must not be before `startDate` |

**Request Example:**

```json
{
  "title": "Staff Software Engineer",
  "company": "Acme Corp",
  "endDate": null
}
```

**Response:** **`200 OK`**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Staff Software Engineer",
  "company": "Acme Corp",
  "type": "FULL_TIME",
  "location": "San Francisco, CA",
  "startDate": "2023-01-15T00:00:00",
  "endDate": null,
  "createdAt": "2026-09-09T09:00:00",
  "updatedAt": "2026-09-09T10:00:00"
}
```

---

### Delete Professional Experience

**`DELETE`** `/v1/professional-experience/{id}`

**Path Parameter:** `id` (UUID) - Professional experience ID

**Response:** **`204 No Content`**

_No response body._
