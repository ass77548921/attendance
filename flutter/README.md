# Flutter Attendance Frontend

Employee attendance frontend built with Flutter.

## Local Run

Use `Makefile` shortcuts:

```bash
make pub-get
make run-stage
make run-dev
make run-pro
```

Web run:

```bash
make run-web-stage
make run-web-dev
make run-web-pro
```

## Docker Build (Flutter Web)

This folder includes Docker setup for Flutter Web with Nginx.

Key behavior:
- Serves Flutter Web static files via Nginx.
- Web uses `API_BASE_URL` from flavor JSON to call backend directly (requires backend CORS configuration).
- `API_BASE_URL` is required in flavor JSON and must match the approved matrix in `../docs/cors/config-matrix.md`; app startup will fail fast if invalid.
- Nginx serves static files only; API calls go directly to backend origin.

### Start (default stage)

Create local env file first:

```bash
cp .env.example .env
```

Then start:

```bash
docker compose up --build -d
```

Open:
- `http://localhost:8081`

### Build with other flavors

You can use `--env-file` directly without editing `.env`:

Dev:

```bash
docker compose --env-file .env.dev up --build -d
```

Pro:

```bash
docker compose --env-file .env.pro up --build -d
```

Stage:

```bash
docker compose --env-file .env.stage up --build -d
```

Included env templates:
- `.env.example`
- `.env.stage`
- `.env.dev`
- `.env.pro`

### Stop

```bash
docker compose down
```

## Build Artifacts

Manual web build:

```bash
make build-web-stage
make build-web-dev
make build-web-pro
```
