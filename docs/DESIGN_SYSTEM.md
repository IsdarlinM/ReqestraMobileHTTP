# Reqestra 3.5.1 — Visual Design System

## Objective

Deliver a consistent appearance in light and dark themes without changing HTTP behavior, sessions, or persistence.

## Palette

| Purpose | Dark | Light |
|---|---|---|
| Background | #07131F | #F3F7FA |
| Surface | #102130 | #FFFFFF |
| Secondary surface | #203647 | #E5EDF2 |
| Primary action | #34E8C2 | #006B5B |
| Primary text | #EAF4F8 | #152B36 |
| Secondary text | #B8C8D5 | #506472 |
| Information | #AED8FF | #225D85 |
| Warning | #FFD38E | #875000 |
| Error | #FFB4AE | #B3261E |

The `onPrimary`, `onSurface`, and `onContainer` colors are defined in `presentation/theme/Theme.kt`.

## Contrast and semantics

- Light primary text against dark background: 15.12:1.
- Dark-theme secondary text against dark secondary surface: 7.30:1.
- Dark primary action against white: 6.45:1.
- Light-theme secondary text against light secondary surface: 5.20:1.
- Text on the dark success container: 8.57:1.

These verified pairs exceed 4.5:1, but they are not a substitute for a comprehensive accessibility audit.

GET and HEAD use blue; POST uses green; PUT/PATCH use amber; and DELETE uses red. HTTP status codes 2xx appear in green, 3xx in amber, and 4xx/5xx in red. Color is always accompanied by text.

## Components

- `StudioScreenHeader`: consistent headings, overlines, and subtitles.
- `StudioSurfaceCard`: cards with subtle borders and 22 dp corners.
- `StudioTag` and `StudioMethodTag`: color-coded indicators with text labels.
- `StatusBanner`: blue informational and red error states with accessible controls.
- `CodeEditor`: soft background, legible monospace type, and green focus border.
- `ReqestraApp`: branded top bar and navigation with a prominent selected state.
- `WorkspaceScreen`: visible primary Send action and an improved HTTP summary.

## Dropdown menus and selection (3.5.1 review)

- HTTP method, body type, environment, and test-collection dropdowns explicitly set `containerColor = MaterialTheme.colorScheme.surface` and use `onSurface` for text in both themes. Secondary method descriptions use `onSurfaceVariant`.
- The language menu already uses `surface` / `onSurface`, and the selected option uses `primaryContainer` / `onPrimaryContainer`.
- The **More** bottom sheet uses `background` / `onBackground` and preserves its card design.
- Avoid transparent popup backgrounds, hard-coded hexadecimal menu colors, and inherited text colors from contexts with different contrast requirements.
- Test opening, scrolling, selection, closing, and legibility in both themes—including increased font size—for every menu trigger.
- The web interface has its own CSS tokens; do not copy Compose values directly into browser styles.

## Verification

In Termux, run `./gradlew :app:assembleDebug :app:testDebugUnitTest`.

Check both themes, larger text, TalkBack, screen rotation, long URLs, and all request states. Android compilation must not be marked successful until the local command completes successfully; GitHub Actions has previously encountered runner startup failures.
