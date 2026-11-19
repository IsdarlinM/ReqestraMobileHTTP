# Reqestra 3.5.1 — Android and Web

**Reqestra** is an HTTP workbench for **native Android and web browsers**, designed for development, API debugging, and authorized security testing. It lets users build, modify, send, and inspect complete HTTP requests without limiting editing to HTML.

```text
Reqestra
Mobile HTTP Workbench
imr :: v3.5.1
```

The name combines **request** and **orchestration**: a single workspace for composing and controlling the full request lifecycle.

## Menu color review — Android and Web

Android's HTTP method, body type, environment, and test-collection dropdowns explicitly use the theme's surface background and corresponding text colors. The language selector and **More** sheet already used themed colors. Reqestra's identity is preserved in light and dark modes without changing HTTP behavior. The web client maintains its own independent CSS system; see the [design system](docs/DESIGN_SYSTEM.md) and [web guide](docs/WEB_SETUP.md). Visual verification on a real device remains pending.

## Reqestra 3.5.1 — interface and delivery

- **Web:** The Donate button uses a **20 × 20 px SVG** heart instead of a font-dependent glyph, with a **44 × 44 px** mobile touch target. Light/dark mode uses sun and moon SVG icons with accessible labels translated into the selected language. The top bar is balanced at intermediate screen widths.
- **Android:** Language, Donate, and Appearance have independent touch targets, themed colors, and testable labels. Instrumentation tests check that they do not overlap.
- **Build:** New `scripts/build-termux.sh` validates tests, builds, and copies the debug APK to Downloads without uninstalling applications.
- Web regression tests cover the Donate button, icons, and six languages. **Android: 3.5.1 (versionCode 10); Web: 3.5.1.**

To update from Termux:

```bash
cd ~/ReqestraMobileHTTP
git pull --ff-only origin main
termux-setup-storage
bash scripts/build-termux.sh --open
```

The **debug** package (`com.imr.reqestra.debug`) installs separately from the release APK (`com.imr.reqestra`). Do not uninstall your app to update it: preserving installed release data requires a release build signed with the **same key** as the previous installation.

See the [3.5.1 release notes](docs/RELEASE_3.5.1.md).

## Overlapping-button fixes — no version change

Android and Web adjust top bars, cards, actions, and selectors to prevent overlapping controls at different screen widths. Header actions stay visible, rows with long buttons stack, and menus scroll within the available area.

See the [responsive interface guide](docs/INTERFAZ_RESPONSIVA.md) and its test matrix. These navigation improvements were originally introduced in **3.5.0**; the current version is **3.5.1**.

## About Reqestra and Help

Android includes **About & Help** under the bottom **More** menu. The web client provides **About & Help** in the sidebar and under **More** on phones. Both screens show the logo, current version, quick start, available features, privacy guidance, and links to documentation and issues.

The complete guide is at [docs/GUIA_DE_USO.md](docs/GUIA_DE_USO.md). This interface section is localized into all six supported languages. The help section was originally introduced in **3.5.0**.

## Language selector and translations — 3.5

Android and Web feature a language selector that highlights the active choice with brand colors and a check mark ✓ in light and dark themes. The web client uses English initial HTML and an accessible, keyboard-navigable menu. Android localizes headings, hints, menus, errors, and results through a local catalog. HTTP request contents remain unchanged across all six languages.

See the [localization audit](docs/INTERNACIONALIZACION_Y_EXTENSIONS.md).

## Mobile navigation and appearance — 3.4.1

The Android header includes **🌐 Language**, **♥ Donate**, and **☀️/🌙 Appearance**. The duplicate top-menu button was removed: the bottom-bar **More** menu is the only entry point for secondary tools (History, Inspector, Preview, Sessions, and Extensions).

The More sheet adapts its background to dark/light themes, uses clearly contrasted cards, and offers discreet touch feedback without an expansive ripple. The theme follows the system until the user makes a manual selection, which is saved on device. The logo remains the **same PNG** on Android and Web. See sections 6 and 7 of the [interface guide](docs/INTERNACIONALIZACION_Y_EXTENSIONS.md).

## Visual identity and languages — 3.4

Reqestra uses the same owner-supplied **R monogram with a turquoise orbit** for the Android icon, Compose top bar, web favicon, and PWA.

The interface supports **English (en), Spanish (es), Russian (ru), Simplified Mandarin Chinese (zh), Portuguese (pt), and French (fr)**. On first launch, it uses the device's primary language when supported, otherwise English. The 🌐 selector in the top bar allows manual changes and stores the preference locally. HTTP requests and responses are never translated.

Built-in extensions have an **Open** action leading to the relevant implemented tool. GraphQL Schema Explorer, external proxy connectors, and remote add-ons remain clearly marked as unimplemented. Third-party code is not executed.

See the [language, icon, and extension guide](docs/INTERNACIONALIZACION_Y_EXTENSIONS.md). UI text is localized without sending secrets to external services. Some technical messages and legacy strings still rely on English fallback and need review by native speakers before commercial publication.

## New clients and donations — 3.3

**Android:** Kotlin, Jetpack Compose, OkHttp, per-origin sessions, and Android Keystore-encrypted collections.

**Web:** Responsive, installable PWA under `web/`, with Studio, variables, Query, encrypted collections and environments, collection tests, bounded discovery, history, inspector, isolated preview, cURL/OpenAPI imports, and sessions. Browser mode does not require a framework or external service.

**Optional PayPal donations:** Support Project screen on Android and Donate/Support on Web. A public hosted PayPal button ID is configured by the owner; **no receiving account is preconfigured**, Reqestra does not process payments, and donating grants no digital benefits.

### Run the web client

```bash
cd web
npm run check
npm test
npm start
```

Open http://127.0.0.1:5173. Requires Node.js 20+. You can also publish `web/` as a static HTTPS site.

**Important parity limitation:** Browser Fetch is subject to CORS, TLS certificate rules, HttpOnly cookies, and restricted headers (Cookie, Host, etc.). For your own APIs or authorized environments, you can start the **restricted local gateway**, which is disabled by default:

```bash
cd web
REQESTRA_ALLOWED_ORIGINS=https://api.example.com node server.mjs
```

Select Gateway in Studio. Do not expose the gateway to the Internet: it binds to loopback, requires an exact origin allowlist, filters DNS and pins validated IPs, rate-limits requests, and does not follow redirects.

### PayPal donations

Create your own button at https://www.paypal.com/donate/buttons. Configure the public `hosted_button_id` in `web/config.js`. On Android, configure `paypalHostedButtonId` in `~/.gradle/gradle.properties` before building. Without an ID, the button is disabled.

### Full documentation

- [Features manual and 20 use cases](docs/FEATURES_AND_USE_CASES.md)
- [Web, PWA, and local gateway installation](docs/WEB_SETUP.md)
- [PayPal configuration and requirements](docs/PAYPAL_DONATIONS.md)
- [Architecture, threats, limits, and secret protection](docs/ARCHITECTURE_AND_SECURITY.md)
- [Test plan and release checklist](docs/TEST_PLAN.md)
- [Material 3 design system](docs/DESIGN_SYSTEM.md)
- [Android 3.2 API discovery and parameterization](docs/API_DISCOVERY_3_2.md)

**The web client is not hosted automatically** by the presence of these files: run it locally or configure your hosting. Android GitHub Actions has previously failed before starting a runner; build the APK in an environment with Android SDK 36.

## What's new in 3.2 — API Discovery Lab

- **Query** tab in Studio: add structured `name=value` parameters, preserving the existing query and encoding values.
- Environment variables such as `{{baseUrl}}` and `{{token}}` in URL, query, headers, cookies, and body, including saved collections.
- **Explore** in main navigation: analyze supplied paths and candidate parameters by comparison with baseline requests.
- Passive path hints from the last response: JSON `href/url/path/endpoint`, HTML `href`, and `Link` headers. They do not initiate requests automatically.
- Tests for **GET, HEAD, and OPTIONS**; POST, PUT, PATCH, and DELETE require explicit additional enablement and confirmation.
- Each discovery run is limited to the same origin, **up to 24 sequential requests at one per second**, without automatic redirects. No mass enumeration.
- Results show HTTP status, size, duration, Allow, and observed differences for candidate parameters (heuristics, not proof of vulnerability).

Read the [parameterization and discovery guide](docs/API_DISCOVERY_3_2.md) before testing endpoints.

## Visual design 3.1 — Studio palette

The interface uses a consistent Material 3 palette in light and dark themes: emerald for primary actions and success, blue for information, amber for warnings, and red for errors. Cards, code fields, notices, HTTP status indicators, and navigation share color, typography, and shape tokens.

See the [visual design guide](docs/DESIGN_SYSTEM.md) for hex values, contrast guidelines, and accessibility criteria.

## What's new in 3.0

- Compact navigation: Studio, Collections, Tests, and History.
- Last-response summary below the editor and access to the full inspector.
- Android Keystore-encrypted collections and environments, with variables like `{{baseUrl}}`.
- Import **JSON** OpenAPI 3.x specifications with absolute base URLs.
- Manually execute up to 20 sequential requests, with status/latency assertions and results.
- Internal extension catalog distinguishing built-in features from planned ones.
- JSON formatter, scrollable horizontal tabs, and more compact component sizes.

See the [3.0 roadmap](docs/ROADMAP_3_0.md) for requirements, usage, limitations, and unimplemented features.

## Features

### Request builder

- `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `HEAD`, `OPTIONS`, and custom methods.
- Fully editable URL: scheme, host, port, path, and query string.
- Duplicate and request-specific headers.
- Editable `Host` header.
- Dedicated cookie editor.
- Bodies:
  - No body.
  - Raw text.
  - JSON.
  - HTML.
  - XML.
  - `application/x-www-form-urlencoded`.
  - GraphQL.
  - Binary content supplied as Base64.
- Editable `Content-Type`.
- Optional redirect following.
- Cookies retained across intermediate redirect hops.
- Configurable timeout from 1 to 120 seconds.
- HTTP/1.1 preference for behavior comparison against HTTP/2.
- Explicit laboratory opt-in for unencrypted HTTP.
- Cancel the active request.

### Optional sessions

Reqestra has three modes:

1. **No session:** does not retain headers or cookies between requests.
2. **In memory:** retains session state until the app process exits.
3. **Encrypted persistent:** retains session state across restarts.

Session properties:

- Session headers scoped **per origin**: scheme, host, and port.
- An `Authorization` header for one origin is never applied to another domain.
- Cookies follow domain, path, expiration, and `Secure` rules.
- Cookies from intermediate responses are sent on subsequent redirect hops when applicable.
- Manual cookies can initialize or override session values.
- Request-specific headers override matching session headers.
- AES-GCM encrypted persistence with an Android Keystore-managed key.
- Backups and internal-data transfer disabled.

### Inspector

- Sent request: method, URL, headers, and size/body.
- Response: status, protocol, duration, content type, headers, and body.
- Visual redaction of `Authorization`, `Cookie`, `Set-Cookie`, common API keys, and tokens.
- Response reads limited to 4 MiB to prevent excessive memory consumption.
- Sent bodies limited to 5 MiB.

### Preview

- Locally rendered HTML in an isolated WebView.
- JavaScript, network access, files, content providers, DOM storage, and database access disabled.
- Monospace rendering for JSON, XML, GraphQL, Raw, and other bodies.

### History

History retains up to 50 entries and stores only:

- Method.
- Redacted URL.
- HTTP status.
- Duration.
- Date and time.

It does not store headers, cookies, bodies, or credentials.

## Header and cookie precedence

### Headers

```text
origin session headers < request-specific headers
```

### Cookies

```text
session cookies < Cookie header manually entered < dedicated cookie editor
```

## Deliberate technical limits

Reqestra allows editing the semantic parts of HTTP requests supported by OkHttp. Framing headers—`Content-Length`, `Transfer-Encoding`, `Connection`, `Proxy-Connection`, `Upgrade`, and `TE`—are computed or controlled internally to avoid inconsistent messages.

It is not a raw TCP-byte editor, request-smuggling engine, or interceptor for other applications.

## Project structure

```text
app/src/main/java/com/imr/reqestra/
├── core/
│   ├── request/          # Composition and precedence
│   ├── security/         # Secret redaction
│   └── validation/       # Defensive validation
├── data/
│   ├── history/          # Minimized history
│   ├── network/          # OkHttp engine and CookieJar
│   └── session/          # In-memory and encrypted persistent sessions
├── domain/model/         # Immutable models
└── presentation/         # Compose, ViewModel, screens, and theme
```

## Technology stack

- Kotlin 2.3.21.
- Jetpack Compose + Material 3.
- OkHttp 4.12.0.
- Android Gradle Plugin 8.13.2.
- Gradle 8.13.
- JDK 17.
- compileSdk / targetSdk 36.
- minSdk 26.

## Building and testing

Requirements:

- Android Studio compatible with AGP 8.13.
- JDK 17.
- Android SDK 36.

The repository does not store generated binaries. On first execution, `gradlew` or `gradlew.bat` runs `gradle/wrapper/WrapperBootstrap.java`, downloads the official Gradle 8.13 JAR, and requires this SHA-256 before use:

```text
81a82aaea5abcc8ff68b3dfcb58b3c3c429378efd98e7433460610fecd7ae45f
```

If the file already exists and matches, no additional connection is made to restore it.

Linux/macOS:

```bash
chmod +x scripts/verify.sh
./scripts/verify.sh
```

Windows:

```cmd
scripts\verify.cmd
```

The script runs:

```text
clean → testDebugUnitTest → lintDebug → assembleDebug
```

Generated APK:

```text
app/build/outputs/apk/debug/app-debug.apk
```

See [TEST_REPORT.md](TEST_REPORT.md) to distinguish checks performed in the original delivery environment from Android tests included for execution with the SDK.

## Authorized use

Use Reqestra only against systems you own, laboratories, CTFs, authorized audits, or assets explicitly included in the scope of a bug bounty program.
