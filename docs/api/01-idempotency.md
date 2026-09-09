# Idempotency

The **Idempotency** mechanism guarantees safe retries of `POST` requests — if a client sends the same request more than once (network retry, timeout, double-click), the server executes it **once** and returns the **identical cached response** for every repeat. It supports **duplicate detection**, **response replay**, **concurrent-request protection**, **automatic key expiry**, and **error cleanup**.

It is enforced by the `IdempotencyFilter` on every matching `POST` request via the `Idempotency-Key` header. No response is ever cached for failed requests — only **`2xx`** responses are stored and replayed.

---

## Scope Summary

| Method | Endpoint | Idempotency-Key |
|--------|----------|-----------------|
| **`POST`** | `/v1/personal-information` | **Required** |
| **`POST`** | `/v1/social-links` | **Required** |
| **`POST`** | `/v1/license-certificate` | **Required** |
| **`POST`** | `/v1/file-store` | **Required** |
| **`POST`** | `/v1/curriculum-vitae` | **Required** |
| `GET`, `PUT`, `DELETE` | All endpoints | Not required (ignored) |

---

## Header

**Header:** `Idempotency-Key`

| Constraint | Value |
|------------|-------|
| Required on | `POST` to the endpoints above |
| Must not be blank | Empty or missing header is rejected |
| Max length | 64 characters |
| Key lifetime (TTL) | 86400 seconds (24 hours) |
| Echoed back | **Yes** — on both the first response and every replay |

**Request Example:**

```
POST /v1/social-links
Content-Type: application/json
Idempotency-Key: 550e8400-e29b-41d4-a716-446655440000

{
  "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
  "platform": "GITHUB",
  "platformUrl": "https://github.com/juandelacruz"
}
```

**First Response:** **`201 Created`**

```
HTTP/1.1 201
Idempotency-Key: 550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json
```

```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
  "platform": "GITHUB",
  "platformUrl": "https://github.com/juandelacruz",
  "createdAt": "2026-09-08T09:00:00",
  "updatedAt": "2026-09-08T09:00:00"
}
```

---

## Replay Behavior

Retrying with the **same key** returns the stored response byte-for-byte — same **status code**, same **body**, same **content type** — without re-executing the operation. No duplicate record is created.

**Replay Response:** **`201 Created`** _(identical to the first response)_

```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "uploaderId": "550e8400-e29b-41d4-a716-446655440000",
  "platform": "GITHUB",
  "platformUrl": "https://github.com/juandelacruz",
  "createdAt": "2026-09-08T09:00:00",
  "updatedAt": "2026-09-08T09:00:00"
}
```

---

## Statuses

| Status | Meaning |
|--------|---------|
| **`2xx`** | First execution succeeded, or replay of a cached success. The `Idempotency-Key` header is echoed back. |
| **`400 Bad Request`** | Missing, blank, or over-long (`> 64` chars) `Idempotency-Key` header. Nothing is stored. |
| **`409 Conflict`** | Another request with the same key is still processing. Retry later with the same key. |

**Missing Header Response:** **`400 Bad Request`**

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Missing required header: Idempotency-Key"
}
```

**Concurrent Duplicate Response:** **`409 Conflict`**

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Request with this Idempotency-Key is already processing"
}
```

---

## Lifecycle Rules

- **Success (`2xx`)** — the response (status, body, content type) is stored and replayed for every later request with the same key.
- **Failure (non-`2xx`)** — validation errors, `4xx`, and `5xx` responses are **never cached**. The key is released, so retrying with the same key re-executes the request.
- **Concurrent duplicates** — while one request is still processing, any concurrent request with the same key gets **`409 Conflict`**.
- **Race on first use** — if two requests with a new key arrive at the same instant, one wins and the other transparently picks up the existing key (no `500`).
- **Expiry** — keys live for **24 hours**. An expired key (whether completed or still in progress) is discarded and the next request with that key executes fresh.

> **Note:** the key alone identifies the request — the request body is not fingerprinted. Reusing a key with a *different* payload still returns the first response. Always generate a new key for a new operation.
