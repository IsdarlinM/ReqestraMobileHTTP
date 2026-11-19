# Reqestra Web 3.5.1

Reqestra's multilingual web client includes an HTTP Studio, query parameters and variables, encrypted collections, OpenAPI/cURL, origin-scoped sessions, tests, bounded discovery, history, inspector, isolated HTML preview, and configurable PayPal donations.

## Getting started

Requires Node.js 20 or later:

    cd web
    npm run check
    npm test
    npm start

Open http://127.0.0.1:5173.

## Request transport modes

**Browser:** Direct Fetch, subject to CORS, browser cookies, TLS policies, and forbidden request headers.

**Private local gateway:** Manually configure an allowlist of API origins under your control:

    REQESTRA_ALLOWED_ORIGINS=https://api.example.com node server.mjs

Choose **Authorized Local Gateway** in the interface. It is not configured by default and cannot bind outside loopback when the allowlist is active. Your own local APIs require the explicit `REQESTRA_ALLOW_PRIVATE_TARGETS=1` option; avoid enabling this on shared networks.

## Secrets and storage

The app can operate temporarily without persisting secrets. For persistence, create a password-protected AES-GCM encrypted vault (WebCrypto/PBKDF2). You can download an encrypted backup. There is no password recovery or automatic Android synchronization.

## Donations

Create a donation button at https://www.paypal.com/donate/buttons and put its **public ID**, never a secret, in `web/config.js`. The option remains disabled until a valid ID is configured. Payments take place outside Reqestra on PayPal.

## Documentation

- [Feature manual and use cases](../docs/FEATURES_AND_USE_CASES.md)
- [Full installation and deployment guide](../docs/WEB_SETUP.md)
- [PayPal integration](../docs/PAYPAL_DONATIONS.md)
- [Architecture and security](../docs/ARCHITECTURE_AND_SECURITY.md)
- [Test matrix](../docs/TEST_PLAN.md)

Do not expose the gateway on a public domain or interpret a CORS error as proof that the destination server is down.

## Languages and visual identity

English is the interface fallback language. The app detects the device's primary language (English, Spanish, Russian, Simplified Mandarin Chinese, Portuguese, or French); the 🌐 control in the top bar allows manual selection of any of the six. This choice is saved in localStorage separately from the encrypted request vault.

The PWA, favicon, and header share the same turquoise-orbit R logo via `web/icon.png`. Your request and response data are never translated automatically. Some legacy descriptive text may use English fallbacks and should undergo native-language review before claiming complete localization.

## Language audit — 3.5

The initial HTML is in English. The 🌐 selector highlights the active language using a border, background, and check mark ✓ in both themes, supports keyboard selection, and persists the choice. Local catalogs cover 328 web labels in six languages, including templates for dynamic messages. URLs, secrets, and request bodies are not translated.

## About the application and help

The **About & Help** page is available in the desktop sidebar and under **More** on mobile. It includes a quick-start guide, features and browser limitations, privacy, the current version, and official links to the [user guide](../docs/USER_GUIDE.md), web setup, and issue tracker. It never sends HTTP requests automatically.

The help page was introduced in 3.5.0. The current version is **3.5.1**.

## Accessible header — 3.5.1

The Donate button uses a heart SVG with fixed geometry and dimensions instead of the font-dependent `♥` glyph. On phones, a centered 20 px icon sits inside a 44 × 44 px touch target.

The appearance button uses sun/moon SVG icons and an accessible label translated into six languages, without hiding access to donations. Browser status indicators are hidden at intermediate widths to prevent overlapping controls.

Payment links open only on PayPal; the application does not collect financial information.

## Visual consistency with Android (3.5.1)

The web client retains its own CSS color system rather than sharing Jetpack Compose tokens directly. The Android dropdown review (HTTP method, body mode, environments, and test collections) is documented in [`docs/DESIGN_SYSTEM.md`](../docs/DESIGN_SYSTEM.md).

To verify visual parity, inspect web and Android menus in both light and dark themes, including keyboard focus, selection states, and text contrast. This note does not mean automated visual tests were executed on both platforms.
