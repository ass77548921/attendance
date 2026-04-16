# CORS and API Endpoint Matrix

## Unified Fields

Backend CORS policy fields (single source of truth):
- `APP_CORS_ALLOWED_ORIGINS`
- `APP_CORS_ALLOWED_METHODS`
- `APP_CORS_ALLOWED_HEADERS`
- `APP_CORS_EXPOSED_HEADERS`
- `APP_CORS_ALLOW_CREDENTIALS`
- `APP_CORS_MAX_AGE_SECONDS`

## Current Environment Matrix

| Environment | Frontend `VITE_API_BASE_URL` | Flutter `API_BASE_URL` | Expected backend origin allowlist |
|---|---|---|---|
| dev | `https://dev.for-sky.com` | `https://dev.for-sky.com` | include `http://localhost:5173`, and deployed dev web origins as needed |
| stage | `http://localhost:8080` | `http://localhost:8080` | include `http://localhost:5173`, `http://localhost:8081`, `http://localhost:8082`, `http://localhost:8083` |
| pro | `https://for-sky.com` | `https://for-sky.com` | include production web origins only |

## Inventory (Before Refactor)

- Backend: no dedicated CORS policy source; Security chain did not explicitly enable CORS.
- Frontend: Vite proxy target existed but used `process.env` in config.
- Flutter: `API_BASE_URL` existed in flavor files; no policy matrix validation.

## Inventory (After Refactor)

- Backend: CORS centralized in `CorsProperties` + `CorsConfig`, applied via Security.
- Frontend: mode-aware env loading via `loadEnv`, aligned `.env.dev/.env.stage/.env.pro`.
- Flutter: startup validates `API_BASE_URL` against approved matrix and fails fast when missing.
- Flutter Web: uses same `API_BASE_URL` as mobile, calls backend directly (no nginx proxy for API).
