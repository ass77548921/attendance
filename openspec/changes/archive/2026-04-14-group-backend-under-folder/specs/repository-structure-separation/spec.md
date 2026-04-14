## ADDED Requirements

### Requirement: Repository SHALL Separate Backend at Top-Level
The repository SHALL place all backend build/runtime assets under a dedicated top-level backend directory so backend and future frontend/admin modules remain clearly separated.

#### Scenario: Contributor inspects repository root
- **WHEN** a contributor lists the repository root directories
- **THEN** backend project assets SHALL be grouped under a dedicated backend directory instead of being mixed with other product modules

### Requirement: Operational Entry Points SHALL Resolve Backend Location
All documented and automated entry points used for backend development and operation SHALL correctly resolve the backend directory after structure migration.

#### Scenario: Developer runs backend from documented commands
- **WHEN** a developer follows repository documentation for backend build, test, and run commands
- **THEN** commands SHALL execute successfully using the backend directory paths

#### Scenario: Container workflow references backend paths
- **WHEN** Docker and Compose workflows are executed after migration
- **THEN** build context, Dockerfile path, and mounted/backend execution paths SHALL resolve without manual path fixes
