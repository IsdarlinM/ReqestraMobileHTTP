# Overlap Fixes — Reqestra 3.5.0

## Objective

Prevent buttons, headings, language controls, list actions, and navigation bars from overlapping in **Android (Jetpack Compose)** and **Web/PWA**, while preserving existing behavior and the 3.5.0 version.

## Changes made

### Web

- The header reserves space for **Donate**, **language**, and **theme**. At widths of 480 px or less, the logo stays visible while the brand text is hidden; all three controls retain a 44 × 44 px touch target. Status indicators are hidden on small screens.
- Form and panel buttons use one or two columns, with wrapping for long labels. List actions can move to the next row instead of imposing rigid minimum widths.
- Card headings can wrap. Fields use a single column when content space is narrow.
- Floating menus have a maximum height and internal scrolling; they must not overlap or sit behind bottom navigation.
- Bottom content respects the fixed bar and system safe area.
- Branding, the 🌐 selector, and donations remain accessible in light and dark modes.

### Android

- The header displays only the monogram below **390 dp**; below **600 dp**, Donate uses a heart icon. Language and appearance remain accessible. Icons have 44 dp touch targets.
- Bottom navigation reduces horizontal icon spacing to avoid collisions on 320–360 dp phones.
- In **Studio**, Send, load response, copy/import cURL, and Cancel actions stack vertically with consistent spacing. The HTTP summary and Inspector button may wrap.
- In **Sessions**, Save and Clear appear separately and span the available width.
- In **History**, the heading and Clear control no longer compete for the same row. HTTP metadata uses readable rows.
- In **Collections**, Open and Delete actions are separated from card content; long names and menus truncate or wrap without shrinking touch targets.
- In **Inspector**, request information, headers, and copy actions each have sufficient space.
- In **Automation**, collection selectors fit the available width, including long names.
- In **Explore**, long checkbox labels take remaining space and may span multiple lines.

## Regression tests

The `web/tests/layout.test.mjs` test checks responsive rules and Android components associated with layout collisions. Run it with the other web tests:

```bash
cd web && npm run check && npm test
```

To build Android:

```bash
./gradlew --no-daemon :app:testDebugUnitTest :app:assembleDebug
```

## Visual verification matrix before distribution

| Environment | Suggested widths | Checks |
| --- | --- | --- |
| Mobile web | 280, 320, 360, 390, 420, 480 px | Header, menus, cards, bottom bar |
| Tablet web | 600, 750, 800, 1024 px | Reflowing panels and lists |
| Desktop web | 1280 and 1440 px | Sidebar navigation and forms |
| Android | 320, 360, 390, 420, 600 dp | Header, bottom bar, forms |
| Accessibility | 100%, 150%, 200% text scale | Truncation and control sizing |
| Themes and languages | Light/dark, en/es/ru/zh/pt/fr | Long labels and contrast |

In each combination, activate every action. Static tests are not a substitute for visual checks in a browser and on a real device. Pay special attention to landscape orientation, the virtual keyboard, and large font sizes.

**Historical release note:** these improvements were introduced without changing the Reqestra **3.5.0** version; the current release is 3.5.1.
