# Reqestra 3.3 — Architecture, Data, and Security

## 1. Overview

Reqestra consists of **two distinct clients** sharing functionality, visual conventions, and variable syntax, but not a codebase or common secret store:

- **Android:** Kotlin, Jetpack Compose, OkHttp, AndroidSessionRepository, WorkspaceStore, and Android Keystore.
- **Web:** semantic HTML, responsive CSS, JavaScript ES modules, Fetch API, WebCrypto, a restricted service worker, and an optional Node.js 20+ local gateway.
- **PayPal:** an external link to a PayPal-hosted donation page. There is no order, capture, or webhook integration.

Neither client requires Reqestra accounts, a centralized backend, a remote database, or telemetry.

## 2. Repository structure

    app/src/main/java/com/imr/reqestra/
      core/validation/RequestValidator.kt
      core/variables/RequestParameterizer.kt
      core/automation/CollectionRunner.kt
      core/discovery/EndpointExplorer.kt
      core/curl/CurlCodec.kt
      core/openapi/OpenApiImporter.kt
      core/donations/PayPalDonations.kt
      data/network/HttpEngine.kt
      data/session/AndroidSessionRepository.kt
      data/workspace/WorkspaceStore.kt
      presentation/MainViewModel.kt
      presentation/ReqestraApp.kt
      presentation/support/SupportScreen.kt
    web/
      index.html             Accessible UI and navigation
      styles.css             Material Studio palette
      config.js              Optional public PayPal ID
      app.mjs                State, actions, and coordination
      lib/core.mjs           Query, variables, OpenAPI, cURL, discovery
      lib/ui.mjs             Safe DOM rendering using textContent
      lib/transport.mjs      Direct Fetch and gateway
      lib/vault.mjs          AES-256-GCM workspace encryption
      server.mjs             Origin-restricted local gateway
      sw.js                  App-shell-only cache
      package.json           npm scripts (no external dependencies)
      tests/                 Node tests
    docs/
      FEATURES_AND_USE_CASES.md
      WEB_SETUP.md
      PAYPAL_DONATIONS.md
      ARCHITECTURE_AND_SECURITY.md
      TEST_PLAN.md

## 3. Request flow

### Android

User → Compose WorkspaceScreen → MainViewModel → RequestParameterizer → RequestValidator → HttpEngine/OkHttp → ResponseSnapshot → Inspector/History.

Session headers and cookies are applied per origin. Reqestra enforces size limits and controls redirects, TLS, and transport via OkHttp.

### Direct web

User → app.mjs → core.parametrizeDraft → core.validateDraft → Browser Fetch → transport.readLimited → Inspector/History.

Browser Fetch must obey CORS, cookie policies, TLS certificate validation, mixed-content restrictions, and forbidden-header rules. Errors appear as network/CORS failures rather than invented HTTP status codes.

### Web with gateway

User → app.mjs → same-origin Web Fetch POST /api/execute → web/server.mjs → exact allowlist + DNS filtering and pinning → Node HTTPS/HTTP → limited JSON response → Inspector.

- Requests require user interaction.
- The gateway does not initiate discovery or scheduled jobs by itself.
- It remains disabled if `REQESTRA_ALLOWED_ORIGINS` is empty.
- When authorized to forward requests, it listens on loopback only.
- The entire origin (scheme, host, port) is validated before DNS lookup.
- DNS is resolved first and the validated IP is pinned for connection; reserved ranges are rejected by default.
- Node does not follow redirects.
- The server avoids logging URLs, headers, bodies, or tokens.
- Limits: 60 requests/minute per client, two concurrent connections, and bounded payloads.

**Limitations:** this is not a production gateway. It has no multiuser authentication, advanced upstream proxy protection, or cookie persistence. Do not expose it to the Internet or place it behind a public reverse proxy without a complete architecture review. Malicious users with local access to the process could target configured allowed origins.

## 4. Data model

### RequestDraft

URL, method, customMethod, queryParamsText, headersText, cookiesText, bodyMode, contentType, body, timeoutSeconds, allowCleartext, followRedirects, forceHttp1, and session configuration.

### SavedRequest

ID, name, collection name, original RequestDraft with variables unexpanded. Uses random IDs.

### Environment

Name and variable map. The map may contain secrets and must remain encrypted when persisted.

### Workspace

List of requests, environments, active environment, and origin-scoped persistent headers on the web. Android stores its session repository separately using Keystore.

### HistoryEntry

ID, method, redacted URL, status, duration, and timestamp. **No** headers, body, or complete response are persisted.

### Response

Status, exposed headers, durationMs, bounded body, truncation indicator, and sanitized request. On the web, this is retained only in memory.

## 5. Encrypted storage

### Android

AES-GCM and Android Keystore through SecurePreferences. There is no cross-device synchronization. Uninstalling the app or losing the key may render data inaccessible.

### Web

The user creates a password-protected workspace (minimum ten characters), using WebCrypto:

1. Generate a random 16-byte salt.
2. Run PBKDF2-SHA256 for 260,000 iterations.
3. Derive a non-exportable AES-256-GCM key.
4. Generate a 12-byte nonce per save.
5. Encrypt JSON containing draft, collections, environments, persistent sessions, and history.
6. Store salt, nonce, and ciphertext in localStorage.

Neither the password nor decryption key is intentionally stored on disk. There is no passphrase recovery. Locking or reloading requires unlocking again.

**Encryption at rest is not protection against XSS or active malware.** While the vault is unlocked, legitimate JavaScript can access data in memory. Use a trusted browser and review its extensions.

## 6. Threat model and mitigations

| Risk | Mitigation | Residual risk |
|---|---|---|
| Secrets in history | Redacted URL and headers; no persisted body | Displayed bodies may contain secrets |
| Web storage disclosure | AES-GCM with passphrase-derived key | Extension/XSS while the tab is unlocked |
| Gateway SSRF | Exact origin allowlist, DNS filtering, pinned IP, loopback | Operator can explicitly allow private IPs |
| Public gateway abuse | No public deployment; loopback binding | Other local processes may connect |
| Local resource access | No file enumeration or file:// | Device owner retains local control |
| Excess traffic | Up to 24/20 requests, one per second; gateway 60/min | User may repeat sessions |
| Memory exhaustion | 5 MiB requests and 4 MiB responses | UI memory usage on small devices |
| External code in preview | Sandbox iframe + CSP; isolated Android WebView | Complex HTML may not render |
| Malicious cURL commands | Parse as text, no shell execution, reject -k | Partial import support |
| Referer leakage | Gateway Referrer-Policy: no-referrer | Static host headers depend on provider |
| PayPal destination tampering | Fixed HTTPS URL and alphanumeric ID | Owner must verify actual beneficiary |
| CORS/cookies | Standard browser policies | Inherent feature limitations |

### Local server CSP headers

Node serves Content-Security-Policy, X-Content-Type-Options, X-Frame-Options, and Referrer-Policy headers. When publishing as a static site, configure equivalent headers with the host. Do not allow arbitrary inline scripts.

### Service worker cache

The PWA uses a closed list of UI assets and a network-first update strategy; it does not cache /api, remote URLs, headers, or HTTP client responses. Secrets are stored only in the encrypted vault.

## 7. Bounded discovery

- No internal wordlists, recursion, IP scanning, or attempts to evade access controls.
- A baseline GET is followed by candidate paths, parameters, and methods from the same origin.
- Differences in status and size compared with the baseline are only imperfect heuristics.
- GET, HEAD, and OPTIONS are selected by default. Potentially state-changing methods require additional enablement and confirmation.
- Cancellation is best-effort: it cannot guarantee that a server has not already applied a mutation.

## 8. Donations and compliance

A PayPal `hosted_button_id` is not an API secret. The URL opens PayPal externally and is not evidence of a completed donation. Reqestra does not exchange financial information with PayPal; donors navigate there voluntarily.

Google Play may require Play Billing if contributions unlock digital goods, features, or services. Reqestra **does not** grant benefits for donations. Future changes to incentives or store policies require review. See [PayPal donations](PAYPAL_DONATIONS.md).

## 9. Testing and release

### Android

    ./gradlew --no-daemon testDebugUnitTest assembleDebug --stacktrace

### Web

    cd web
    npm run check
    npm test
    npm start

Manually verify CORS, TLS, gateway restrictions, encrypted persistence, cancellation, state-changing methods, and PayPal buttons. Tests are not a comprehensive security audit.

## 10. Future development

True network parity between Android and a remotely hosted web service would require a multiuser backend with authentication, scoped ACLs, DNS pinning, sanitized observability, account quotas, CSRF protection, TLS, and privacy review. Do not confuse these requirements with the existing local gateway. Remote secret synchronization would require separate cryptographic design and explicit consent.
