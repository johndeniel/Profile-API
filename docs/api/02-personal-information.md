# Personal Information API

**Base URL:** `/v1/personal-information`

The **Personal Information API** manages user profile data — including name, contact details, headline, location, and blob storage references — through a full-featured REST interface. It supports complete **CRUD operations**, **paginated listing**, **multi-field filtering**, **text search**, and **sortable results**.

---

## Endpoint Summary

| Method | Endpoint | Operation |
|--------|----------|-----------|
| **`POST`** | [`/v1/personal-information`](#create-personal-information) | Create Personal Information |
| **`GET`** | [`/v1/personal-information`](#get-personal-information-paginated) | Get Personal Information (Paginated) |
| **`PUT`** | [`/v1/personal-information/{id}`](#update-personal-information) | Update Personal Information |
| **`DELETE`** | [`/v1/personal-information/{id}`](#delete-personal-information) | Delete Personal Information |

---

## Endpoints

### Create Personal Information

**`POST`** `/v1/personal-information`

#### Headers

| Header | Required | Description |
|--------|----------|-------------|
| **`Idempotency-Key`** | **Yes** | Unique key for idempotent request (max 64 chars) |

#### Request Body

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| **`firstName`** | String | **Yes** | Max 100 characters |
| `middleName` | String | No | Max 100 characters |
| **`lastName`** | String | **Yes** | Max 100 characters |
| `headline` | String | No | Max 300 characters |
| `blobUrl` | String | No | Valid URL, max 2048 characters |
| `blobId` | UUID | No | Blob storage identifier |
| `emailAddress` | String | No | Valid email, max 255 characters |
| `phoneNumber` | String | No | Max 20 characters |
| `location` | String | No | Max 255 characters |

**Request Example:**

```json
{
  "firstName": "Juan",
  "middleName": "Dela",
  "lastName": "Cruz",
  "headline": "Software Engineer",
  "blobUrl": "https://example.com/blob/profile.jpg",
  "blobId": "550e8400-e29b-41d4-a716-446655440000",
  "emailAddress": "juan@example.com",
  "phoneNumber": "+639123456789",
  "location": "Manila, Philippines"
}
```

**Response:** **`201 Created`**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "firstName": "Juan",
  "middleName": "Dela",
  "lastName": "Cruz",
  "headline": "Software Engineer",
  "blobUrl": "https://example.com/blob/profile.jpg",
  "blobId": "550e8400-e29b-41d4-a716-446655440000",
  "emailAddress": "juan@example.com",
  "phoneNumber": "+639123456789",
  "location": "Manila, Philippines",
  "createdAt": "2026-09-08T09:00:00",
  "updatedAt": "2026-09-08T09:00:00"
}
```

---

### Get Personal Information (Paginated)

**`GET`** `/v1/personal-information`

#### Query Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | `0` | Page number (0-indexed) |
| `size` | int | `10` | Page size (max 100) |
| `sortBy` | String | `createdAt` | Field to sort by. Allowed: `firstName`, `middleName`, `lastName`, `headline`, `emailAddress`, `phoneNumber`, `location`, `createdAt`, `updatedAt` |
| `sortDirection` | String | `desc` | Sort direction (`asc` or `desc`) |
| `id` | UUID | - | Filter by exact ID |
| `blobId` | UUID | - | Filter by exact blob ID |
| `search` | String | - | Search across `firstName`, `middleName`, `lastName`, `headline`, `emailAddress`, `phoneNumber`, `location` |
| `firstName` | String | - | Filter by first name (LIKE) |
| `middleName` | String | - | Filter by middle name (LIKE) |
| `lastName` | String | - | Filter by last name (LIKE) |
| `headline` | String | - | Filter by headline (LIKE) |
| `emailAddress` | String | - | Filter by email address (LIKE) |
| `phoneNumber` | String | - | Filter by phone number (LIKE) |
| `location` | String | - | Filter by location (LIKE) |

**Response:** **`200 OK`**

```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "firstName": "Juan",
      "middleName": "Dela",
      "lastName": "Cruz",
      "headline": "Software Engineer",
      "blobUrl": "https://example.com/blob/profile.jpg",
      "blobId": "550e8400-e29b-41d4-a716-446655440000",
      "emailAddress": "juan@example.com",
      "phoneNumber": "+639123456789",
      "location": "Manila, Philippines",
      "createdAt": "2026-09-08T09:00:00",
      "updatedAt": "2026-09-08T09:00:00"
    }
  ],
  "totalElements": 100,
  "totalPages": 10,
  "currentPage": 0,
  "size": 10
}
```

---

### Update Personal Information

**`PUT`** `/v1/personal-information/{id}`

**Path Parameter:** `id` (UUID) - Personal information ID

#### Request Body

_All fields optional for partial update._

| Field | Type | Constraints |
|-------|------|-------------|
| `firstName` | String | Max 100 characters |
| `middleName` | String | Max 100 characters |
| `lastName` | String | Max 100 characters |
| `headline` | String | Max 300 characters |
| `blobUrl` | String | Valid URL, max 2048 characters |
| `blobId` | UUID | Blob storage identifier |
| `emailAddress` | String | Valid email, max 255 characters |
| `phoneNumber` | String | Max 20 characters |
| `location` | String | Max 255 characters |

**Request Example:**

```json
{
  "firstName": "Juan",
  "lastName": "Cruz",
  "headline": "Senior Software Engineer"
}
```

**Response:** **`200 OK`**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "firstName": "Juan",
  "middleName": "Dela",
  "lastName": "Cruz",
  "headline": "Senior Software Engineer",
  "blobUrl": "https://example.com/blob/profile.jpg",
  "blobId": "550e8400-e29b-41d4-a716-446655440000",
  "emailAddress": "juan@example.com",
  "phoneNumber": "+639123456789",
  "location": "Manila, Philippines",
  "createdAt": "2026-09-08T09:00:00",
  "updatedAt": "2026-09-08T10:00:00"
}
```

---

### Delete Personal Information

**`DELETE`** `/v1/personal-information/{id}`

**Path Parameter:** `id` (UUID) - Personal information ID

**Response:** **`204 No Content`**

_No response body._