## MODIFIED Requirements

### Requirement: Local Setup Guide SHALL Identify Backend Working Directory
Local development guidance SHALL explicitly instruct contributors to execute backend-related commands from the backend directory after repository restructuring. The guide SHALL also define where CORS-related environment values are declared, how they map to each environment, and how to validate CORS locally before merging.

#### Scenario: New contributor follows setup guide
- **WHEN** a new contributor executes local setup commands from documentation
- **THEN** the guide SHALL clearly indicate backend working-directory requirements and backend-relative command paths

#### Scenario: Contributor configures local CORS values
- **WHEN** a contributor prepares local environment variables for backend/frontend/flutter integration
- **THEN** the guide SHALL provide explicit CORS variable names, expected formats, and example values

#### Scenario: Contributor validates local CORS behavior
- **WHEN** a contributor completes local setup
- **THEN** the guide SHALL include a repeatable local validation flow covering preflight and credentialed request checks
