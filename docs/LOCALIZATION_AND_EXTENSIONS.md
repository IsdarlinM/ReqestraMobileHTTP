# Reqestra 3.4 — Languages, Logo, and Extensions

## 1. Supported languages

| Code | Language | Device default behavior |
| --- | --- | --- |
| en | English | Used when the device is in English or its language is unsupported |
| es | Spanish | Used when the primary device language is Spanish |
| ru | Russian | Used when the primary device language is Russian |
| zh | Simplified Mandarin Chinese | Used when the primary device language begins with zh (e.g., zh-CN) |
| pt | Portuguese | Used when the primary device language begins with pt |
| fr | French | Used when the primary device language begins with fr |

Language is an **interface preference**; it does not alter HTTP bodies, credentials, headers, or API responses. Translation catalogs are bundled locally rather than calling remote translation services that could receive sensitive data.

### Automatic language selection

- Android: uses the device's primary language with English fallback. Manual choices are stored in `SharedPreferences("reqestra_ui")`.
- Web: reads `navigator.languages[0]` (or `navigator.language`), not secondary languages. Manual choices are stored under `reqestra.locale` in `localStorage`.
- The 🌐 selector in the **top bar** offers all six options.
- A manual choice overrides automatic detection on subsequent launches.
- Chinese uses the `zh` code and **simplified** characters.
- Unsupported system languages fall back to English, never Spanish.

### Current coverage and quality

Primary menus, headings, controls, labels, actions, and much of the explanatory UI are localized. Technical messages from legacy validators and some specialized descriptions may still use English fallbacks (or an older string on Android) until localization is complete. This **does not** constitute professional translation certification. For public launch, specialist native speakers familiar with HTTP/cybersecurity should review all six language variants.

### Files

- `web/lib/i18n.mjs`: web catalogs, language selection, persistence, and UI node translation, including dynamically created cards.
- `app/src/main/java/com/imr/reqestra/presentation/i18n/ReqestraLanguages.kt`: Compose catalogs, normalization, and translations.
- `app/src/main/res/xml/locales_config.xml`: Android-declared languages.
- `app/src/main/java/com/imr/reqestra/presentation/ReqestraApp.kt`: 🌐 selector icon and language persistence.

## 2. Consistent visual identity

The owner selected an **R monogram with a cyan orbit against a dark blue background**. Optimized versions of the original PNG are stored at:

- `app/src/main/res/drawable-nodpi/reqestra_brand.png` — Android launcher, round icon, and Compose header.
- `web/icon.png` — web header, favicon, and PWA installation.

The high-resolution original should be kept outside the repository if the owner wants printing assets or other resolutions. App-sized assets have been downsized and optimized, with potentially minor gradient differences.

## 3. Built-in extensions with navigation actions

| Android extension | Actual screen | Status |
| --- | --- | --- |
| Collection Manager | Collections | Built in |
| Environment Variables | Collections → Environments | Built in |
| Test Assertions | Tests | Built in |
| cURL Toolkit | Studio → cURL | Built in |
| OpenAPI Importer | Collections → Import | Built in |
| Query Parameterizer | Studio → Query | Built in |
| Endpoint Explorer | Explore | Built in |
| GraphQL Schema Explorer | No implemented screen | Planned |
| Proxy Connector | Not implemented | Planned |
| AI Assistant | Not implemented | Planned |

Built-in extensions are **internal modules**, not separate APKs. Their Open action navigates to the corresponding screen. Planned features are not given misleading execution buttons.

### Web

Studio, environments, encrypted collections, tests, exploration, cURL/OpenAPI imports, sessions, and the private gateway are linked from Extensions. The gateway works only when `REQESTRA_ALLOWED_ORIGINS` is configured on the local server; opening the screen does not activate it. Downloadable extensions, interception of other applications, and arbitrary TCP-packet manipulation are not provided.

## 4. Verification matrix

| Case | Android | Web | Expected result |
| --- | --- | --- | --- |
| Device set to es-DO | Yes | Yes | Spanish on first launch with no saved preference |
| Device set to ru-RU | Yes | Yes | Russian |
| Device set to zh-CN | Yes | Yes | Simplified Chinese |
| Device set to pt-BR | Yes | Yes | Portuguese |
| Device set to fr-FR | Yes | Yes | French |
| Device set to it-IT | Yes | Yes | English fallback |
| Manual 🌐 selection | Yes | Yes | UI rerenders and preference persists |
| Language changed during a request | Yes | Yes | HTTP request/response unchanged |
| Available extensions | Yes | Yes | Navigate to working tool |
| Future extensions | Yes | Yes | Planned state without execution |
| Header logo | Yes | Yes | Same chosen image |
| Application icon | Yes | PWA | Same optimized image |

## 5. Validation commands

### Web

```bash
cd ~/ReqestraMobileHTTP/web
npm run check
npm test
node server.mjs
```

### Android

```bash
cd ~/ReqestraMobileHTTP
./gradlew --no-daemon testDebugUnitTest assembleDebug --stacktrace
```

Unit tests do not replace visual checks on devices, real CORS testing, accessibility testing, or logo gradient review. Updating GitHub does not itself prove that an APK was built.

## 6. Navigation and donation visibility

### Android

The top bar shows the PNG logo beside the Reqestra name. On the right are the 🌐 language selector, **Donate** button, and ☀️/🌙 appearance control for switching between light and dark themes. The duplicate top-menu button has been removed.

- On narrow phones, Donate appears as a tappable **heart** icon with a localized accessible description.
- On wider screens, it shows the **heart and localized Donate text**.
- Both variants lead to the Support Project information screen, where the user explicitly chooses whether to open PayPal's official page. They do not initiate payment or change permissions automatically.

The bottom bar has **five** destinations: Studio, Collections, Tests, Explore, and More. The first four open primary features. More opens a bottom sheet with icon, title, and description rows for History, Inspector, Preview, Sessions, and Extensions. **More is the only button that opens this sheet**. Project support already has a permanent top-bar action and is not duplicated in the sheet.

The selected label and accessibility semantics update with the active destination. Long labels may be visually ellipsized on small screens, but controls retain their accessible descriptions.

### Web

The top bar permanently displays a contrasting **Donate** button, including on the home screen, beside the language and appearance controls. On narrow displays, a 44 px heart button with an accessible name prevents overlap with the logo. It opens **Support Project**, not PayPal directly.

The web app retains desktop sidebar navigation and five mobile bottom-navigation destinations. Icons were added to mobile navigation actions.

### Security, voluntary donations, and configuration

- Contributions are optional and **never activate features**.
- Only the support screen offers an outbound link to PayPal's official domain when a valid public button ID is configured.
- The owner's `hosted_button_id` candidate originated from an administrative link; **verify the public donation page and receiving account separately** before publication.
- Reqestra does not collect card numbers or PayPal credentials.

### Quick check

1. Open Android and web clients in English and Spanish; inspect the button labels.
2. Test 320, 360, and 420 dp/px phones for header or navigation overflow.
3. On Android, select More in the bottom bar; verify there is no second top-bar control opening the same sheet.
4. Open all five secondary tools and confirm the intended screen appears.
5. Select Donate; it should open the support screen without initiating payment.
6. Verify the PayPal recipient before testing the public destination.

## 7. Light/dark appearance and touch feedback — Android 3.4.1

Reqestra's dark palette uses navy and petroleum-blue surfaces. Light mode uses an extremely pale blue-gray background with white cards. The **More** bottom sheet follows the current theme, with individual cards, icons on turquoise surfaces, and discreet separators. Mixing inconsistent Material 3 palettes is avoided.

### ☀️ / 🌙 selector

- The top-bar action offers **Light mode** while dark mode is active and **Dark mode** while light mode is active.
- On first launch, the app follows the system theme.
- After a manual change, the selection is saved under the `theme_mode` key in `SharedPreferences("reqestra_ui")`, without modifying language or workspace settings.
- Device status and navigation bars also adjust icon contrast.
- The control's accessible names are available in all six interface languages.
- The web client has an independent appearance setting.

### Touch feedback

Bottom navigation and More-sheet rows avoid the expanding ripple effect on touch. Instead, they display a **subtle background-color change while pressed**. They remain actionable, labeled, have generous touch targets, and preserve accessible semantics. General controls and form fields use standard Material 3 behavior.

### PNG logo

The files `app/src/main/res/drawable-nodpi/reqestra_brand.png` and `web/icon.png` were verified to contain **identical PNG bytes**. The Android manifest uses `@drawable/reqestra_brand`; the web uses that image in its header, favicon, and PWA manifest. There is no replacement branding or second generated logo.

### Additional test matrix

| Action | Expected result |
| --- | --- |
| Open with dark system theme and no saved preference | Dark theme appears |
| Open with light system theme and no saved preference | Light theme appears |
| Tap ☀️ in dark mode | Switch to light and retain choice |
| Tap 🌙 in light mode | Switch to dark and retain choice |
| Rotate or restart Android | Manual theme persists |
| Change interface language | Appearance accessibility text changes |
| Tap More | Open a single bottom sheet |
| Tap a menu row | Navigate without a large ripple |
| Tap a bottom tab | Highlight active tab without pointer effect |
| Tap Donate | Open support screen without automatic payment |
| Open web and Android | Both use the same PNG |

## 8. Localization audit and language selector — 3.5.0

### Android selector

The 🌐 dropdown follows Material 3 and Reqestra's palette in light and dark modes. It displays a localized **Interface language** heading, a code (EN, ES, RU, ZH, PT, or FR), the native-language name, and a check mark for the selected option. The active row uses `primaryContainer` with `onPrimaryContainer` text, which both adjust to the theme. Selection is announced for accessibility and persisted locally.

### Web selector

A visible native `select` was replaced by an accessible menu with `aria-expanded`, `role="listbox"`, and `aria-selected` rows. The active option has a ✓ check mark, border, and accent background in both themes. Keyboard, Escape, arrows, outside clicks, and focus restoration on close are supported. The earlier selector remains hidden only for internal compatibility.

### Strings and coverage

- The six languages are **en, es, ru, zh (simplified), pt, and fr**.
- The **web** catalog contains 328 static keys in all six languages, all nonempty, plus templates for dynamic messages with names or numbers. A total of 165 initial HTML labels and attributes were changed from Spanish to English while preserving the option to retranslate into Spanish or other languages.
- The **Android app** centralizes 384 localized strings plus templates for dynamic testing, networking, and validation messages. Hard-coded text was corrected in Collections, Discovery, Automation, Inspector, Sessions, Preview, and Extensions. UI status notices use the catalog before display.
- Localization applies to the **interface and application messages**, not user-generated content, URLs, headers, variable values, or HTTP bodies. Arbitrary messages returned by servers or external libraries remain unchanged except for recognized local error strings.
- Tests cover translation values, six languages, dynamic messages, highlighted language selection, and English initial text.

### Areas requiring checks on real devices

Coverage unit tests verify that keys are present, **not** professional linguistic quality or readability at every screen size. For international release, manually test navigation and alerts in all six languages, particularly long Russian, French, and Portuguese labels. Review TalkBack, VoiceOver, browser screen readers, and web-menu focus restoration.

### Verification

```bash
cd ~/ReqestraMobileHTTP
git pull --ff-only origin main
cd web
npm run check && npm test
cd ..
./gradlew --no-daemon :app:testDebugUnitTest :app:assembleDebug
```

## 9. About Reqestra and Help — no version change

### Android

Use **More** in bottom navigation to open **About & Help**. It reuses the project styling, the same corporate PNG, and the version from `BuildConfig.VERSION_NAME`. It includes a quick start, built-in tools, privacy and security guidance, and buttons to open the user guide, report issues, or view GitHub source code via HTTPS.

### Web

On desktop, **About & Help** appears in the sidebar; on mobile, under **More**. It covers the same topics, describes browser CORS limits, and links to Studio, Extensions, documentation, and issues. Links open in a separate tab protected by `noopener noreferrer`. The page is informational and does not send network requests until an external link is followed.

### Languages and checks

The new labels, paragraphs, and links have translations for English, Spanish, Russian, Simplified Mandarin Chinese, Portuguese, and French. Tests were added for navigation and text availability. See the full [user guide](USER_GUIDE.md).

The help section was introduced without a version change in **3.5.0**; the current public release is **3.5.1**.
