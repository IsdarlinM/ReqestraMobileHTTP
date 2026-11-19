# Reqestra 3.3 — Features and Use Cases

**Status:** Native Android client (Kotlin/Jetpack Compose) and installable web client (HTML/CSS/JavaScript). Both let users compose, send, inspect, and test HTTP requests against authorized systems. Neither is an interception proxy, an unrestricted browser, a raw TCP-byte engine, or a mass scanner.

## Contents

1. [Android / Web feature parity](#1-android--web-feature-parity)
2. [Navigation and screens](#2-navigation-and-screens)
3. [HTTP request builder and parameterization](#3-http-request-builder-and-parameterization)
4. [Sessions, secrets, and persistence](#4-sessions-secrets-and-persistence)
5. [Collections, imports, and history](#5-collections-imports-and-history)
6. [Automated tests](#6-automated-tests)
7. [Path and parameter discovery](#7-path-and-parameter-discovery)
8. [Inspector and preview](#8-inspector-and-preview)
9. [PayPal donations](#9-paypal-donations)
10. [Step-by-step use cases](#10-step-by-step-use-cases)
11. [Known limitations and future features](#11-known-limitations-and-future-features)

## 1. Android / Web feature parity

| Capability | Android 3.3 | Direct web | Private web gateway |
| --- | --- | --- | --- |
| GET/POST/PUT/PATCH/DELETE/HEAD/OPTIONS | Yes | Yes, subject to Fetch/CORS | Yes, with allowlist |
| Custom HTTP method | Yes | Browser-dependent | No, seven-method allowlist |
| Structured, repeated, encoded query | Yes | Yes | Yes |
| Variables in URL/headers/query/cookies/body | Yes | Yes | Yes |
| Editable Content-Type | Yes | Yes, subject to CORS | Yes |
| Allowed arbitrary headers | OkHttp semantics | Browser restrictions | Node-permitted headers; no framing or Host |
| Custom Host header | Android only | No | No |
| Manual cookies | Yes | No | Yes, per request |
| Persistent session cookies | Keystore | Browser policies; not managed by JavaScript | No automatic persistent HTTP cookie jar |
| Origin-scoped session headers | Yes | Yes, encrypted password-based web persistence | Yes |
| Redirects | Client-configured and controlled | Fetch, with restrictions/opaque results | Not followed |
| Timeout/cancellation | Yes | Yes | Yes |
| Laboratory HTTP without TLS | Opt-in + Android configuration | Depends on origin, mixed content, browser | Opt-in + allowlist |
| Prefer HTTP/1.1 | Yes | No | Not configurable |
| RAW/JSON/HTML/XML/Form/GraphQL/Base64 bodies | Yes | Yes | Yes |
| Status/headers/body inspector | Yes | Headers exposed by CORS only | Response headers relayed by gateway |
| Isolated HTML preview | WebView without JavaScript or network | Sandbox/CSP iframe | Same preview |
| Local collections, up to 120 requests | Yes | Yes | Yes |
| Up to 12 local environments | Yes | Yes | Yes |
| OpenAPI 3 JSON import, up to 50 operations | Yes | Yes | Yes |
| cURL import | Yes | Simple subset | Simple subset |
| Redacted cURL export | Yes | Yes | Yes |
| Sequential tests, up to 20 per collection | Yes | Yes | Yes |
| Discovery, up to 24 requests at one per second | Yes | Yes, subject to CORS | Yes, allowlisted only |
| GET/HEAD/OPTIONS with state-changing methods opt-in | Yes | Yes | Yes |
| Redacted local history, up to 50 entries | Yes | Yes | Yes |
| Confidential storage | Android Keystore/AES-GCM | WebCrypto AES-GCM with PBKDF2 password | Browser-based; gateway stores nothing |
| Voluntary external PayPal donation | Yes, configurable | Yes, configurable | Same as web |
| Automatic mobile/web synchronization | No | No | No |

**Important:** The web client covers the same main screens and workflows, but browser restrictions prevent full parity with OkHttp. The private gateway can address CORS and manual-cookie restrictions for explicitly configured origins; it is **not** an open proxy or low-level interceptor.

## 2. Navigation and screens

### Studio

Editors for URL, method, query, headers, cookies, body type, body content, timeout, and redirects. Actions include Send, Cancel, Format JSON, Import cURL, and Copy Redacted cURL. The most recent response is summarized on screen. The web client lets users choose direct Fetch or the private gateway.

### Collections

Save named requests into collections, load and delete them, import OpenAPI operations, and manage environments and variables. Data from the two clients is **not** synchronized automatically.

### Tests

Choose a collection, expected HTTP status, and maximum latency. Execute stored requests one by one, displaying PASS/FAIL and any network errors.

### Explore

Specify candidate absolute paths, query parameter names, passive suggestions from the last response, HTTP methods, and an explicit authorization confirmation. Individual attempts have separate results.

### History

Method, URL with sensitive query values redacted, status, duration, and timestamp. Maximum 50 entries. Redacted URLs may require restoring tokens or parameter values before reuse.

### Inspector

Prepared request, status, available headers, reported protocol, duration, body, and truncation marker. Not a traffic analyzer for other applications.

### Preview

Android uses an isolated WebView. The web client uses a sandboxed iframe with restrictive CSP for HTML and a monospace text view for other types. Neither behaves as an unrestricted Internet browser.

### Sessions

Android: disabled, temporary in-memory, or encrypted persistent sessions with cookies and headers scoped to an origin. Web: disabled, in-memory, or encrypted persistent **headers**; browser cookies remain subject to CORS and browser policies. The gateway has no automatic cookie jar.

### Extensions

Catalog of built-in and planned features; third-party plugins are not downloaded or executed.

### Donations

Voluntary PayPal access from **Support Project** (Android tools menu; web Donate / Support).

## 3. HTTP request builder and parameterization

### 3.1 Complete URL

The client accepts absolute HTTP or HTTPS URLs with a host, including scheme, port, path, and query. Credentials embedded in URLs and URL fragments are rejected. Plain HTTP requires laboratory opt-in and can still be blocked by Android or the browser.

Example:

    https://api.example.com:8443/v1/users?page=1

### 3.2 Methods

GET, HEAD, OPTIONS, POST, PUT, PATCH, and DELETE; Android also supports custom methods. GET and HEAD bodies are rejected to avoid ambiguity. POST/PUT/PATCH can send an empty body. The local web engine supports only the seven standard methods listed.

### 3.3 Structured query parameters

Enter one name/value pair per line. New parameters are appended to the existing query, and each value is encoded. Duplicate names are allowed.

    page=2
    filter={{searchTerm}}
    tag=red
    tag=blue

Limit: 40 structured entries per request. Variables are resolved before URL construction.

### 3.4 Environment variables

Create variables under Collections → Environments. Names use letters, digits, and underscores, starting with a letter. References use double braces. Variables expand in URL, Query, headers, cookies, Content-Type, and body.

    baseUrl = https://api.example.com
    token = [private value]
    searchTerm = developer

URL:

    {{baseUrl}}/v1/users

Headers:

    Accept: application/json
    Authorization: Bearer {{token}}
    X-Trace: reqestra

Body:

    {"search":"{{searchTerm}}"}

Requests with missing variables are not sent. Variables are not executable expressions and cannot access system environment variables.

### 3.5 Headers and precedence

Use `Name: Value` syntax. Android combines origin-scoped session headers with request-specific headers; request-specific values take precedence. The web client follows this precedence but cannot set every header in JavaScript. Connection/Content-Length/Transfer-Encoding/Upgrade/TE are reserved for the transport engine. The web gateway also reserves Host.

### 3.6 Cookies

Android manages session cookies according to domain, path, expiration, and Secure. Explicit editor values can override session cookies.

In direct browser mode, JavaScript cannot set the Cookie header or read HttpOnly cookies. Users may enable browser-managed cookie sending when permitted. The gateway supports manually entered cookies on each request but does not automatically keep cookies from earlier responses.

### 3.7 Body types

No body, Raw, JSON, HTML, XML, application/x-www-form-urlencoded, GraphQL, and binary data encoded as Base64. Content-Type is editable. Maximum outgoing body: 5 MiB; maximum inspected response: 4 MiB. The web client uses UTF-8 for text bodies and decodes Base64 into bytes.

### 3.8 Other settings

Timeouts from 1 to 120 seconds, request cancellation, and optional redirect following. HTTP/1.1 preference applies only to Android. Browsers may return `opaqueredirect` for redirects that are not followed.

### 3.9 Redaction

Sensitive headers and query values are redacted in recorded results and shared cURL. A response body may contain secrets and **is not automatically redacted when shown in Inspector**. Do not manually share sensitive response bodies.

## 4. Sessions, secrets, and persistence

### Android

Collections, environments, and persistent sessions are encrypted with AES-GCM and Android Keystore-protected keys. They are not exported automatically; app-data backup is disabled. No-session mode does not persist cookies. Session headers are isolated by origin.

### Web

The web workspace starts in **temporary** mode. Creating an encrypted vault requires a password of at least ten characters. The browser derives a key using PBKDF2-SHA256 (260,000 iterations) and saves AES-256-GCM ciphertext in localStorage. The password and decryption key remain in tab memory and must be supplied again after locking or closing.

Encrypted content includes saved requests, variables, persistent headers, the current draft, and redacted history. Full responses are not stored. Backup JSON files are **encrypted** and do not expose plaintext variables.

**There is no password recovery, cloud synchronization, user-account system, or automatic sharing between devices.** Protection also relies on a trustworthy browser and device; malicious extensions, XSS, or malware with access to an unlocked session can read in-memory data.

## 5. Collections, imports, and history

- Up to 120 saved requests and 12 environments.
- Saved requests retain unexpanded templates; the active environment provides values at send time.
- Import OpenAPI 3.x JSON with an absolute HTTP(S) base URL and up to 50 operations per import; YAML, remote references, and advanced OpenAPI authentication are unsupported.
- Android's cURL importer supports a subset of POSIX options; the web importer handles a simpler subset without executing shell commands.
- The web cURL exporter resolves variables and redacts query values, sensitive headers, and bodies. Credentials are not exported.
- Up to 50 redacted local history entries per client, with no bodies or secrets.

## 6. Automated tests

A collection of 1–20 requests can be run manually and **sequentially**, with a one-second gap between requests. The user sets an expected status (100–599) and maximum latency (1–120,000 ms). Every result includes status, duration, assertions, and errors. An unsuccessful request does not block later requests unless execution is cancelled.

The runner does not limit collection operations to safe methods. Ensure tests do not create, update, or delete data unless explicitly intended.

## 7. Path and parameter discovery

1. Set a URL from an **authorized origin** in Studio, optionally with variables and headers.
2. In Explore, enter up to eight absolute paths such as `/health` or `/api/v1`.
3. Add up to eight candidate parameter names such as `include`, `filter`, or `page`.
4. Select GET, HEAD, or OPTIONS. To use POST/PUT/PATCH/DELETE, enable the extra control for state-changing methods.
5. Confirm that you are authorized for the origin and approve execution.
6. Review each attempt: method, path, status, duration, response size, and Allow header when present.

A run is capped at **24 requests**, **one per second**, with bounded timeouts. Scheme, host, and port are preserved; path traversal sequences and off-origin destinations are rejected. Discovery does not follow redirects.

### Passive hints

The last response may contain HTML links, JSON href/url/path/endpoint values, or Link headers. Same-origin candidates are displayed; adding a hint to the form **does not send traffic**.

### Candidate or hidden parameters

For each proposed parameter name, status and response length are compared with a baseline request lacking that parameter. A difference is merely a **heuristic hint**: dynamic content, errors, CDNs, and ignored parameters may produce variations. There is no mass fuzzing, recursion, subdomain discovery, or authentication attack.

## 8. Inspector and preview

The inspector displays the prepared request, status, available protocol, duration, headers, and a response body truncated at 4 MiB. On the web, available headers depend on CORS. HTML preview runs in a permissionless sandbox with CSP blocking scripts and remote resources; it is not designed to validate complete third-party web pages.

## 9. PayPal donations

PayPal hosts the contribution page and processes the payment. The app only constructs a fixed paypal.com URL using a public hosted-button ID provided by the owner. Reqestra has no card forms, payment backend, transaction webhooks, receipts, or PayPal financial-data storage. The button is disabled without configuration.

**Donations are always optional, entirely voluntary, and do not grant features, badges, or digital benefits.** Review Google Play policies and PayPal requirements before publication (see [PayPal donations](PAYPAL_DONATIONS.md)).

## 10. Step-by-step use cases

### UC-01 — Send a simple GET

- Actor: developer.
- Prerequisite: own or test API permitting GET.
- Action: Studio → full URL → GET → Send Request.
- Expected: status, duration, and body visible in the summary and Inspector.
- Possible failures: TLS, DNS, CORS restriction, invalid URL, or timeout.

### UC-02 — Use an environment token

- Actor: developer or QA.
- Action: Collections → create a token variable; Studio → `Authorization: Bearer {{token}}`.
- Result: token injected in memory at execution time; redacted history.
- Security: avoid sharing screenshots of response content.

### UC-03 — Filter records using query parameters

- Starting URL: `https://api.example.com/v1/users?active=true`.
- Query: `page=2` and `filter={{searchTerm}}`.
- Result: both pairs added while preserving `active=true` and URL-encoding values correctly.

### UC-04 — Send JSON POST to a test environment

- Method POST; JSON body; Content-Type application/json.
- Data is sent only when Send is selected.
- Users must verify whether the API creates a resource; Reqestra does not undo changes.

### UC-05 — Reuse a request across environments

- Save a template in a collection; create Local and QA environments with different `baseUrl` values.
- Switch environments and run the saved request.
- Secrets are not automatically copied or synchronized across devices.

### UC-06 — Import and review an OpenAPI collection

- Paste a v3 JSON specification containing an absolute HTTP(S) `servers[0].url`.
- Import it, review the generated operations, and execute only authorized ones.
- The import does not resolve external references or complex schemas automatically.

### UC-07 — Migrate a compatible cURL request

- Paste a simple cURL command into Studio and select Import.
- Check method, URL, headers, and body before sending.
- Insecure TLS options are rejected; shell commands are not executed.

### UC-08 — Share a request without secrets

- Use Copy Redacted cURL.
- Verify that query values, body data, and confidential headers are hidden.
- Do not replace this with a manual copy of Inspector containing real data.

### UC-09 — Check HTTP status and latency

- Save 1–20 requests in a collection.
- For example, set expected status 200 and maximum latency 2,000 ms.
- Run tests and examine each PASS/FAIL result.

### UC-10 — Enumerate a few authorized paths

- Choose a URL for your own lab API.
- Propose `/health` and `/api/v1`, confirm authorization, and run.
- Manually examine 200/401/403/404/405; do not assert vulnerabilities without evidence.

### UC-11 — Inspect a candidate unknown parameter

- Enter `include` and compare with the baseline.
- If status or size changes, inspect the content and reproduce manually.
- Byte differences alone are not confirmed findings.

### UC-12 — Compare HTTP methods

- Use GET, HEAD, and OPTIONS by default.
- Enable writing methods only in an environment with disposable data and explicit confirmation.
- The Allow header is informational; authorization must be tested separately.

### UC-13 — Cancel a slow request

- Set a timeout within 1–120 seconds.
- Run and select Cancel.
- The client interrupts the current operation, but the server may already have processed it.

### UC-14 — Use origin-scoped session headers

- Configure headers under Sessions and choose memory or persistent mode.
- Send to the same origin.
- Headers must not be forwarded to another domain. Web persistence requires an unlocked vault.

### UC-15 — Work without saving secrets

- Open the web client without creating a vault and use temporary mode.
- Reloading loses temporary drafts and secrets.
- Create an encrypted vault if you need persistence.

### UC-16 — Recover an encrypted web collection

- Unlock the vault and download an encrypted backup.
- Import the file in another browser using the same password.
- Android Keystore data is not imported automatically.

### UC-17 — Diagnose a CORS failure

- Send a direct web request to an API that does not allow your origin.
- Fetch/CORS error appears; do not assume authentication failure.
- Use Android or the allowlisted local gateway if authorized.

### UC-18 — Preview HTML in isolation

- In Studio, choose an HTML body and paste a template.
- Open Preview: the sandbox renders it without scripts or remote resources.
- It is not intended for browsing third-party websites.

### UC-19 — Install the web client as a PWA

- Serve the web folder using localhost or HTTPS.
- Install it from a compatible browser.
- The cache stores only the UI shell, not API results or credentials.

### UC-20 — Donate voluntarily

- Maintainer configures a hosted PayPal button.
- User opens Donate/Support or Support Project and chooses whether to visit PayPal.
- Payment occurs outside Reqestra, without unlocking privileges or features.

## 11. Known limitations and future features

- No synchronization server, accounts, multiuser permissions, or real-time collaboration.
- No interception proxy, network sniffing, WebSocket inspector, SSE, or high-volume traffic generator.
- Direct web mode is subject to CORS, HttpOnly cookies, forbidden headers, mixed-content restrictions, and opaque redirects.
- Optional gateway is loopback-only with an origin allowlist; it is unsuitable for public deployment without an additional authentication architecture.
- Donations do not have in-app payment confirmation, subscriptions, or rewards.
- OpenAPI support is limited to v3 JSON, without YAML or remote reference resolution.
- No complete HAR/Postman import or automated OAuth2 PKCE.
- Android builds and web tests require an Android SDK/Node environment; a commit is not a verified build.

For setup and architecture, see [Web setup](WEB_SETUP.md), [PayPal donations](PAYPAL_DONATIONS.md), [Architecture and security](ARCHITECTURE_AND_SECURITY.md), and [Test plan](TEST_PLAN.md).
