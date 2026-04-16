# CORS Dev/Stage Validation Record (2026-04-14)

## Scope

- Change: `refactor-project-cors-handling`
- Purpose: complete task 5.2 (dev/stage integration validation and record)

## Validation Commands

### 1) Backend CORS integration tests with dev origin matrix

```bash
cd backend
APP_CORS_ALLOWED_ORIGINS="https://dev.for-sky.com,http://localhost:5173" \
./gradlew test \
  --tests com.attendance.AuthIntegrationTest.preflightToProtectedEndpointFromAllowedOriginShouldReturnCorsHeaders \
  --tests com.attendance.AuthIntegrationTest.preflightFromDisallowedOriginShouldBeForbidden \
  --tests com.attendance.AuthIntegrationTest.actualRequestFromAllowedOriginShouldKeepCorsHeaders
```

Result:
- `BUILD SUCCESSFUL`

### 2) Backend CORS integration tests with stage origin matrix

```bash
cd backend
APP_CORS_ALLOWED_ORIGINS="http://localhost:5173,http://localhost:8081,http://localhost:8082,http://localhost:8083" \
./gradlew test \
  --tests com.attendance.AuthIntegrationTest.preflightToProtectedEndpointFromAllowedOriginShouldReturnCorsHeaders \
  --tests com.attendance.AuthIntegrationTest.preflightFromDisallowedOriginShouldBeForbidden \
  --tests com.attendance.AuthIntegrationTest.actualRequestFromAllowedOriginShouldKeepCorsHeaders
```

Result:
- `BUILD SUCCESSFUL`

### 3) Frontend build verification with env modes

```bash
cd frontend
npm run build -- --mode dev
npm run build -- --mode stage
```

Result:
- Both builds completed successfully.

## Conclusion

- Dev and stage matrices validated through backend integration tests and mode-based frontend build checks.
- CORS behavior for allowed/disallowed origins and protected-endpoint response headers is verified in both matrices.
- Ready to proceed to pro rollout using `docs/cors/rollout-rollback.md` and `docs/cors/smoke-checklist.md`.
