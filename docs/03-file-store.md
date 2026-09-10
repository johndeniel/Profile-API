# File Store API

**Base URL:** `/v1/file-store`

The **File Store API** handles file uploads and management through Vercel Blob storage. It supports **multi-file upload** (up to 10 files per request), **paginated listing**, **filtering by uploader**, and **bulk delete** (up to 10 IDs per request). Only image files are accepted.

---

## Endpoint Summary

| Method | Endpoint | Operation |
|--------|----------|-----------|
| **`POST`** | [`/v1/file-store`](#upload-files) | Upload Files |
| **`GET`** | [`/v1/file-store`](#get-files-paginated) | Get Files (Paginated) |
| **`DELETE`** | [`/v1/file-store`](#delete-files) | Delete Files |

---

## Endpoints

### Upload Files

**`POST`** `/v1/file-store`

Content-Type: `multipart/form-data`

#### Form Parameters

| Parameter | Type | Required | Constraints |
|-----------|------|----------|-------------|
| **`files`** | MultipartFile | **Yes** | Image files only, max 10 files per request |
| **`uploaderId`** | UUID | **Yes** | Uploader's unique identifier |

**Request Example:**

```
files: @photo1.png
files: @photo2.jpg
uploaderId: 550e8400-e29b-41d4-a716-446655440000
```

**Response:** **`201 Created`**

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
    "blobUrl": "https://example.blob.vercel-storage.com/uploads/photo1-a1b2c3d4.png",
    "createdAt": "2026-09-08T09:00:00",
    "updatedAt": "2026-09-08T09:00:00"
  },
  {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
    "blobUrl": "https://example.blob.vercel-storage.com/uploads/photo2-e5f6g7h8.jpg",
    "createdAt": "2026-09-08T09:00:01",
    "updatedAt": "2026-09-08T09:00:01"
  }
]
```

---

### Get Files (Paginated)

**`GET`** `/v1/file-store`

#### Query Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | `0` | Page number (0-indexed) |
| `size` | int | `10` | Page size (max 100) |
| `sortBy` | String | `createdAt` | Field to sort by. Allowed: `uploaderId`, `createdAt`, `updatedAt` |
| `sortDirection` | String | `desc` | Sort direction (`asc` or `desc`) |
| `id` | UUID | - | Filter by exact file ID |
| `uploaderId` | UUID | - | Filter by uploader ID |

**Response:** **`200 OK`**

```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
      "blobUrl": "https://example.blob.vercel-storage.com/uploads/photo1-a1b2c3d4.png",
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

### Delete Files

**`DELETE`** `/v1/file-store`

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| **`ids`** | List\<UUID\> | **Yes** | Comma-separated file IDs to delete (max 10) |

**Request Example:**

```
DELETE /v1/file-store?ids=550e8400-e29b-41d4-a716-446655440000,660e8400-e29b-41d4-a716-446655440001
```

**Response:** **`204 No Content`**

_No response body._

**Behavior:** All-or-nothing. If any ID is not found, no files are deleted and a `404` error is returned.
