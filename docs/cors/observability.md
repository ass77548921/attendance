# CORS Observability Baseline

## Metrics to Track

- Preflight success rate (`OPTIONS` 2xx / total `OPTIONS`)
- CORS rejection count (requests rejected by origin policy)
- 4xx ratio on browser-originated API traffic
- Authentication failures by allowed origin

## Suggested Log Fields

- `origin`
- `method`
- `path`
- `corsDecision` (`allow` or `reject`)
- `environment` (`dev`, `stage`, `pro`)

## Follow-up Improvement Items

1. Add dashboard panel for preflight success rate by environment.
2. Add alert when CORS rejection count spikes after release.
3. Add periodic audit for origin allowlist drift between env files and deployment variables.
