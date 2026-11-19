# Reqestra 3.0 — Implementation Status and Roadmap

## Delivered in this iteration

- Four primary destinations: Studio, Collections, Tests, and History. Inspector, Preview, Sessions, and Extensions are under **More Tools**.
- Studio retains the HTTP/cURL editor and shows a summary of the last response on the same screen.
- A more compact code editor, scrollable tabs, and JSON formatting button.
- Named collections and logical folders stored **encrypted** with Android Keystore and AES-GCM, separately from session preferences.
- Up to 12 environments with variables such as `{{baseUrl}}`, expanded only when requests are sent. Values are hidden in lists.
- Import of standard operations from **OpenAPI 3.x JSON** (up to 50, requiring an absolute `servers[0].url`). External references are not downloaded and YAML is not interpreted.
- Manual sequential collection runner: up to 20 requests, at least one second apart, status and latency assertions, and per-request results.
- Declarative catalog of built-in and future extensions; it **does not** execute downloaded plugins or arbitrary code.
- Additional unit tests for templates, assertions, and the extension catalog.
- Existing cross-origin redirect safeguards, redaction, and cURL behavior are preserved.

## Usage

1. In **Studio**, prepare a request and check headers, cookies, and body.
2. In **Collections**, name the request and assign it to a collection.
3. Add an environment. Enter variable names without braces (for example, `baseUrl`) and use `{{baseUrl}}` in the URL, headers, or body.
4. Return to **Studio** to send requests individually. Use **View Inspector** for the full response.
5. In **Tests**, select a collection, expected status, and maximum latency, then select **Run Tests Now**. **Real requests will be sent** and POST/PUT/DELETE may modify destination systems.
6. To import OpenAPI routes, open **Collections → Import OpenAPI JSON** and paste a 3.x document with a base URL.

## Privacy and security

- Collections may contain sensitive headers, bodies, and variables; they are stored encrypted and separately from session preferences. App backups are disabled.
- This data is never automatically placed in logs, CI, or an AI service.
- The OpenAPI importer parses user-provided text; it makes no network requests and does not execute `$ref`.
- The collection runner reuses `RequestValidator` and `HttpEngine` safeguards.
- Automation is manual; there is no autonomous background scheduling or bulk traffic generation.
- Third parties cannot execute arbitrary plugins with network or storage permissions.

## Planned: do not confuse catalog entries with available features

- JSON editor with syntax highlighting, line numbers, and folding.
- Tablet layout with resizable panels and side navigation.
- Room (encrypted migration from current storage), advanced search, and controlled export.
- Postman importer, OpenAPI YAML, HAR, multipart, and selectable binary files.
- OAuth 2.0/OIDC + PKCE, mTLS profiles, and explicit debugging proxy configuration.
- EventListener DNS/TCP/TLS/TTFB metrics and structured response comparison.
- Opt-in WorkManager scheduling subject to OS policies and traffic authorization.
- Advanced workflows with JSONPath extraction, cross-step variables, JSON Schema, retry limits, and reports.
- WebSocket/SSE/GraphQL.
- Full plugin SDK with capability-based permissions, isolation, signature verification, and dependency audits.
- Opt-in AI assistant with redaction and explicit consent before sharing content.

## Validation

Run with JDK 17 and Android SDK 36:

    ./scripts/verify.sh

GitHub Actions workflow: **Android CI**. An earlier run failed before obtaining a runner; this must be addressed in Actions configuration/infrastructure. Do not mistake an infrastructure failure for a compilation error. No feature is considered built or device-tested until CI or a complete local build passes.

## Next phase

Prioritize successful builds, instrumentation, an advanced JSON editor, and migration to Room, with regression tests for encryption, navigation, and sessions. Do not add plugin execution or AI agents without a permission model and security testing.
