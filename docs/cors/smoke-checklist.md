# CORS Smoke Checklist

Run this checklist before release on each environment (`dev`, `stage`, `pro`).

## 1. Preflight (Allowed Origin)

Command:

```bash
curl -i -X OPTIONS "http://localhost:8080/api/admin/users" \
  -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: GET" \
  -H "Access-Control-Request-Headers: Authorization,Content-Type"
```

Expected:
- HTTP 200
- `Access-Control-Allow-Origin` equals request origin
- `Access-Control-Allow-Credentials: true`

## 2. Preflight (Disallowed Origin)

Command:

```bash
curl -i -X OPTIONS "http://localhost:8080/api/admin/users" \
  -H "Origin: http://evil.example" \
  -H "Access-Control-Request-Method: GET"
```

Expected:
- HTTP 403
- No permissive `Access-Control-Allow-Origin` header

## 3. Actual Request (Protected Endpoint)

Command:

```bash
curl -i "http://localhost:8080/api/admin/users" \
  -H "Origin: http://localhost:5173"
```

Expected:
- HTTP 401 (without token) is acceptable
- Response still includes `Access-Control-Allow-Origin` for allowed origin

## 4. Credentials Rule Guard

Validation:
- Confirm `APP_CORS_ALLOW_CREDENTIALS=true` is never combined with wildcard origin `*`.
- App startup must fail if invalid combination is configured.

## 5. Release Record

Record for each environment:
- Timestamp
- Tester
- Commit SHA
- Checklist pass/fail
- Any temporary allowlist exceptions
