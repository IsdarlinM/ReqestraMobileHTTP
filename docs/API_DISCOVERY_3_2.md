# Reqestra 3.2.0 — Parameterization and Controlled API Discovery

## Parameters, variables, and headers

In Studio, enter a complete URL or a template, for example:

    {{baseUrl}}/v1/users

Under Collections > Environments, create the `baseUrl` and `token` variables and assign their values. In Studio's Query tab, enter one query parameter per line:

    page=2
    filter={{filter}}
    expand=roles,permissions

The original URL retains its existing query string. New parameters are appended in order, and values are encoded (including spaces, `&`, and `=`). Up to 40 structured parameters are supported per request. Duplicate names such as `tag=x` and `tag=y` are allowed intentionally.

The Headers editor also supports variables: `Authorization: Bearer {{token}}` and `X-Tenant: {{tenant}}`. The same applies to manual cookies, the body, and Content-Type. Variables are resolved just before sending, not when saving a request. Saved requests are encrypted using Android Keystore.

Copying a redacted cURL command uses the resolved request, including its structured query, but removes confidential values from the exported command. Importing cURL does not execute scripts.

## Explore APIs

1. In Studio, configure the URL, environment, headers, and normal query parameters.
2. Open Explore. The Studio URL defines the origin (scheme, host, and port) for the entire analysis.
3. Optionally enter absolute paths, one per line, such as `/health` or `/api/v1` (maximum 8).
4. Optionally enter candidate parameter names for the current URL, such as `include` or `page` (maximum 8).
5. Optionally include passive hints extracted from the last response (JSON href/url/path/endpoint, HTML links, or Link headers). These hints are not requested automatically.
6. Choose methods. GET, HEAD, and OPTIONS are available by default. POST, PUT, PATCH, and DELETE require explicit manual enablement.
7. Confirm authorization for the origin and select Run. You can cancel at any time.

Execution is limited to 24 sequential HTTP requests at one per second, without following redirects, without using persistent sessions, and with a timeout of up to 15 seconds per request. The same scheme, host, and port are retained. The client reuses headers and cookies entered manually in Studio; only test services where you are authorized to use those credentials.

Results show HTTP status, approximate duration, response bytes, and the Allow header if present. Candidate parameters are compared with a baseline request by status code and response length. A difference is a **heuristic observation**, not proof of a hidden parameter, data leakage, or a vulnerability.

## Security and limits

- No built-in brute-force wordlists, recursive discovery, subdomain detection, or network scanning.
- Origins are validated before every request; redirects to unrelated destinations are not tested.
- No system secrets are read or sent to external analysis services.
- Potentially state-changing methods are disabled by default and require another confirmation.
- Users must verify the scope limits of their bug bounty program, lab, or own API.
- Responses may be dynamic: a byte-count difference does not confirm a hidden parameter.
- Method selection is not a complete authorization-control test; manually review 401/403/405.
- The safety of GET/HEAD/OPTIONS depends on correct server implementations.

## Architecture

- `core/variables/RequestParameterizer.kt`: template expansion and encoded query construction.
- `core/discovery/EndpointExplorer.kt`: planning, origin restrictions, passive extraction, and sequential execution.
- `presentation/discovery/DiscoveryScreen.kt`: UI and consent confirmation.
- `presentation/MainViewModel.kt`: coordination and cancellation.
- `data/workspace/WorkspaceStore.kt`: persistence of collection query text without changing older saved files.

## Verify the version

    git pull --ff-only origin main
    grep 'versionName =' app/build.gradle.kts
    ./gradlew --no-daemon testDebugUnitTest assembleDebug --stacktrace

Do not claim a successful build until Gradle completes with Android SDK 36 in Termux or Android Studio. Earlier GitHub Actions runs failed before a runner became available.
