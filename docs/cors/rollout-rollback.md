# CORS Rollout and Rollback Guide

## Rollout

1. Update backend CORS env variables for target environment.
2. Confirm frontend `VITE_API_BASE_URL` and flutter flavor `API_BASE_URL` match the matrix.
3. Deploy backend first, then frontend/flutter clients.
4. Execute `docs/cors/smoke-checklist.md` and store results in release notes.

## Rollback

1. Revert CORS env variables to the previous known-good values.
2. Redeploy backend with reverted variables.
3. If client URLs changed in same release, revert corresponding frontend/flavor env files.
4. Re-run smoke checklist to confirm service recovery.

## Troubleshooting

- Browser shows CORS error on preflight:
  - Check backend allows requested method and headers.
  - Confirm origin exists in `APP_CORS_ALLOWED_ORIGINS`.
- Browser shows missing credentials support:
  - Check `APP_CORS_ALLOW_CREDENTIALS=true` and no wildcard origin.
- Flutter startup fails with API URL validation:
  - Check flavor JSON `API_BASE_URL` is one of approved matrix URLs.
