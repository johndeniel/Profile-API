# Social Link API

**Base URL:** `/v1/social-links`

The **Social Link API** manages user social media links — including platform type and URL — through a full-featured REST interface. It supports complete **CRUD operations**, **paginated listing**, **multi-field filtering**, **text search**, and **sortable results**.

---

## Endpoint Summary

| Method | Endpoint | Operation |
|--------|----------|-----------|
| **`POST`** | [`/v1/social-links`](#create-social-link) | Create Social Link |
| **`GET`** | [`/v1/social-links`](#get-social-links-paginated) | Get Social Links (Paginated) |
| **`PUT`** | [`/v1/social-links/{id}`](#update-social-link) | Update Social Link |
| **`DELETE`** | [`/v1/social-links/{id}`](#delete-social-link) | Delete Social Link |

---

## Endpoints

### Create Social Link

**`POST`** `/v1/social-links`

#### Headers

| Header | Required | Description |
|--------|----------|-------------|
| **`Idempotency-Key`** | **Yes** | Unique key for idempotent request (max 64 chars) |

#### Request Body

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| **`uploaderId`** | UUID | **Yes** | Uploader's unique identifier |
| **`platform`** | String | **Yes** | Must be `LINKEDIN`, `GITHUB`, `INSTAGRAM`, or `LEETCODE` |
| **`platformUrl`** | String | **Yes** | Valid URL, max 2048 characters |

**Request Example:**

```json
{
  "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
  "platform": "LINKEDIN",
  "platformUrl": "https://linkedin.com/in/juandelacruz"
}
```

**Response:** **`201 Created`**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
  "platform": "LINKEDIN",
  "platformUrl": "https://linkedin.com/in/juandelacruz",
  "createdAt": "2026-09-08T09:00:00",
  "updatedAt": "2026-09-08T09:00:00"
}
```

---

### Get Social Links (Paginated)

**`GET`** `/v1/social-links`

#### Query Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | `0` | Page number (0-indexed) |
| `size` | int | `10` | Page size (max 100) |
| `sortBy` | String | `createdAt` | Field to sort by. Allowed: `uploaderId`, `platform`, `createdAt`, `updatedAt` |
| `sortDirection` | String | `desc` | Sort direction (`asc` or `desc`) |
| `id` | UUID | - | Filter by exact ID |
| `uploaderId` | UUID | - | Filter by uploader ID |
| `platform` | String | - | Filter by platform (equals) |
| `search` | String | - | Search across `platform` |

**Response:** **`200 OK`**

```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
      "platform": "LINKEDIN",
      "platformUrl": "https://linkedin.com/in/juandelacruz",
      "createdAt": "2026-09-08T09:00:00",
      "updatedAt": "2026-09-08T09:00:00"
    }
  ],
  "totalElements": 25,
  "totalPages": 3,
  "currentPage": 0,
  "size": 10
}
```

---

### Update Social Link

**`PUT`** `/v1/social-links/{id}`

**Path Parameter:** `id` (UUID) - Social link ID

#### Request Body

_All fields optional for partial update._

| Field | Type | Constraints |
|-------|------|-------------|
| `uploaderId` | UUID | Uploader's unique identifier |
| `platform` | String | Must be `LINKEDIN`, `GITHUB`, `INSTAGRAM`, or `LEETCODE` |
| `platformUrl` | String | Valid URL, max 2048 characters |

**Request Example:**

```json
{
  "platformUrl": "https://linkedin.com/in/juandelacruz-updated"
}
```

**Response:** **`200 OK`**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
  "platform": "LINKEDIN",
  "platformUrl": "https://linkedin.com/in/juandelacruz-updated",
  "createdAt": "2026-09-08T09:00:00",
  "updatedAt": "2026-09-08T10:00:00"
}
```

---

### Delete Social Link

**`DELETE`** `/v1/social-links/{id}`

**Path Parameter:** `id` (UUID) - Social link ID

**Response:** **`204 No Content`**

_No response body._
