# Reqestra 3.3 — Functional, Security, and Accessibility Test Plan

## Acceptance criteria

1. Both clients retain their existing HTTP request composition features.
2. Size, rate, and discovery-scope limits are enforced.
3. Secrets do not appear in history, exported cURL, or plaintext web backups.
4. PayPal cannot be accessed until a valid public button ID is configured; every feature remains usable without donating.
5. Builds are not considered verified until reproducible Gradle, Node, and browser results exist.

## 1. Automated web tests

Requirements: Node.js 20 or later; no external dependencies.

    cd web
    npm run check
    npm test

Files: `web/tests/core.test.mjs` and `web/tests/vault.test.mjs`. They cover variable expansion, query construction, validation failures, OpenAPI imports, same-origin paths, disabled state-changing methods, passive heuristics, redacted cURL, assertions, and WebCrypto vault encryption.

**Note:** these unit tests do not automatically exercise the real DOM, CORS, or browser permissions.

## 2. Automated Android tests

    cd ~/ReqestraMobileHTTP
    ./gradlew --no-daemon testDebugUnitTest assembleDebug --stacktrace

In addition to existing HTTP, session, cURL, discovery, collection, and validation tests, `PayPalDonationsTest` checks safe HTTPS URL construction for www.paypal.com and rejects empty or malicious IDs.

Historically, GitHub Actions has sometimes failed before assigning a runner. A pushed commit is not proof of a successful build. Build locally and retain error logs.

## 3. Manual feature test matrix

| ID | Component | Test | Expected result |
|---|---|---|---|
| WEB-01 | Studio | GET to an API with permitted CORS | HTTP/status/body/latency |
| WEB-02 | URL | URL with username:password | Rejected |
| WEB-03 | HTTP | Disable allowCleartext and send HTTP | Rejected |
| WEB-04 | Headers | Set Content-Length manually | Rejected |
| WEB-05 | Cookies | Manual Cookie with browser transport | Limitation notice |
| WEB-06 | Query | existing=1 and a filter containing spaces | Existing query preserved; filter encoded |
| WEB-07 | Variables | Undefined {{token}} variable | Error before sending |
| WEB-08 | Variables | Sensitive token value | Not in history or exported cURL |
| WEB-09 | Body | Invalid JSON + Format button | Warning without losing text |
| WEB-10 | cURL | Import simple cURL | Editable draft loaded |
| WEB-11 | cURL | cURL using -k | Insecure TLS rejected |
| WEB-12 | OpenAPI | 3.x document with GET/POST | Imports two operations |
| WEB-13 | OpenAPI | YAML or relative server | Documented rejection |
| WEB-14 | Vault | Create, lock, unlock | Encrypted data restored |
| WEB-15 | Vault | Incorrect password | Rejected without exposing data |
| WEB-16 | Vault | Export/import backup | Same passphrase required |
| WEB-17 | Collections | Save/retrieve draft | Fields preserved |
| WEB-18 | Collections | Delete a request | Confirmation required |
| WEB-19 | Environments | Switch from Local to QA | Variables change |
| WEB-20 | Tests | Two-request collection | PASS/FAIL and progress |
| WEB-21 | Tests | Cancel during execution | Stops without new requests |
| WEB-22 | Discovery | /health on same origin | Route result |
| WEB-23 | Discovery | External URL used as path | Rejected |
| WEB-24 | Discovery | Nine paths | Rejected by limit |
| WEB-25 | Discovery | DELETE without opt-in | Blocked |
| WEB-26 | Discovery | DELETE with opt-in and confirmation | Intentional request sent |
| WEB-27 | Discovery | Additional parameter | Status/size difference if present |
| WEB-28 | Preview | HTML with script and remote image | Scripts blocked; remote resources not loaded |
| WEB-29 | History | 51 requests | Maximum of 50 rows |
| WEB-30 | Gateway | No allowed origins | 403; gateway disabled |
| WEB-31 | Gateway | Origin outside allowlist | Blocked |
| WEB-32 | Gateway | 127.0.0.1 without opt-in | Blocked |
| WEB-33 | Gateway | Explicit private-target opt-in in lab | Only authorized origin accessible |
| WEB-34 | Gateway | HTTP 302 redirect | Location not followed |
| WEB-35 | PayPal | Empty ID | Button disabled |
| WEB-36 | PayPal | Valid public ID | Link to www.paypal.com only |
| WEB-37 | PWA | Install and use offline | App shell only; HTTP responses not cached |
| WEB-38 | CSP | Inject HTML in response/history | Rendered as text; scripts not executed |
| MOB-01 | Android | Build with JDK 17/SDK 36 | BUILD SUCCESSFUL |
| MOB-02 | Android | Four-tab navigation + menu | Every screen accessible |
| MOB-03 | Android | GET/POST/PUT/PATCH/DELETE/HEAD/OPTIONS | Expected responses |
| MOB-04 | Android | SessionMode NONE/MEMORY/PERSISTENT | Correct origin semantics |
| MOB-05 | Android | RequestParameterizer variables + query | Resolution, limits, encoding |
| MOB-06 | Android | Discovery Planner + Explorer | Limit of 24; same origin |
| MOB-07 | Android | PayPal without ID | Button inactive |
| MOB-08 | Android | PayPal with public ID | Opens PayPal link in external browser |
| MOB-09 | Android | TalkBack/rotation/larger text | Accessible and readable fields |

## 4. Accessibility verification

- Keyboard navigation and visible web focus.
- Labels associated with inputs; buttons with accessible names; notices using role=status/alert.
- 44 px web and 48 dp Android touch targets where appropriate.
- Light/dark themes and contrast for critical text.
- 200% text scale, landscape, mobile widths 320–420 px, and desktop widths 1280–1920 px.
- Reduced-motion preferences on the web.
- Accessible focus after section changes and opening the mobile submenu.
- PayPal opens a new context only after explicit interaction.

## 5. Devices and browsers

Suggested platforms: Chrome Android, Firefox Android, desktop Chrome/Edge/Firefox, and modern Safari on macOS/iOS using HTTPS or localhost. Do not assume every platform offers identical storage, WebCrypto, header, or cookie access. For Android, test at least an API 26 device and an API 35/36 device if available.

## 6. Gateway security test (local only)

Host your own API on localhost:8080 and explicitly enable:

    REQESTRA_ALLOWED_ORIGINS=http://127.0.0.1:8080 REQESTRA_ALLOW_PRIVATE_TARGETS=1 node server.mjs

Verify that out-of-origin URLs are rejected, redirects are not followed, Host and Content-Length cannot be set manually, the process does not listen publicly when an allowlist is set, and logs contain no tokens. Do not perform unauthorized external scans.

## 7. Before publishing

- [ ] WEB: `npm run check` and `npm test` succeed.
- [ ] MOB: `testDebugUnitTest` and `assembleDebug` succeed.
- [ ] PayPal: receiving account and beneficiary confirmed; buttons disabled without an ID.
- [ ] Google Play policy reviewed; donors receive no digital benefits.
- [ ] Private gateway not exposed publicly without authentication.
- [ ] HTTPS website with CSP and no secrets in client code.
- [ ] Manual CORS and cookie tests documented.
- [ ] Documentation and license reviewed.
- [ ] Screenshots and test data are fictional; no real test credentials in the repository.
- [ ] Security, privacy, and authorized-use policies reviewed.

## 8. Reporting a bug

Include the commit version, environment (Android/Termux/browser), launch instructions, minimal reproduction steps, expected and actual behavior, and a sanitized error excerpt **without tokens, cookies, keys, or sensitive internal URLs**. Avoid publishing full responses from private APIs.
