## ADDED Requirements

### Requirement: Unified CORS Policy Source
The system SHALL define a single CORS policy source per environment that includes allowed origins, allowed methods, allowed headers, exposed headers, and credential policy.

#### Scenario: Load environment-specific CORS policy
- **WHEN** the application starts in `dev`, `stage`, or `pro`
- **THEN** the runtime SHALL load exactly one CORS policy object for that environment

#### Scenario: Missing required CORS fields
- **WHEN** required CORS fields are missing or malformed in configuration
- **THEN** the application SHALL fail fast with a clear startup error

### Requirement: Consistent CORS Behavior Across Endpoints
The backend SHALL apply the same CORS policy through a single registration path so that preflight and actual requests behave consistently for protected and public API routes.

#### Scenario: Preflight request on protected endpoint
- **WHEN** a browser sends an `OPTIONS` preflight request to a protected API endpoint from an allowed origin
- **THEN** the response SHALL include required CORS headers and a successful status code

#### Scenario: Request from disallowed origin
- **WHEN** a request originates from an origin not in the active policy
- **THEN** the backend SHALL reject CORS access and SHALL NOT include permissive CORS headers

### Requirement: Environment CORS Verification Checklist
The project SHALL maintain a verification checklist for dev, stage, and pro that validates origin allowance, credential behavior, and preflight success.

#### Scenario: Release candidate verification
- **WHEN** a release candidate is prepared for deployment
- **THEN** the team SHALL execute and record the CORS verification checklist for the target environment
