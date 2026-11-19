# Reqestra 3.5.1 — Design, Accessibility, and Upgrades

This maintenance release of **Reqestra HTTP Studio** covers Android and web. It does not introduce accounts, new payment systems, or remote extension execution. Work remains local, and voluntary donations continue through external PayPal pages.

## Implemented improvements

### Web

- The top donation button replaces the typographic `♥` character with a vector SVG using `viewBox="0 0 24 24"` and a fixed **20 × 20 px** size, avoiding variations across fonts and devices.
- On small screens, the button has a **44 × 44 px** square touch target without distorting the icon or overlapping Language and Appearance.
- The theme control uses sun/moon SVG icons consistent with Reqestra's palette and borders. Its accessible action name changes with the theme and is translated into **six languages**.
- The top bar hides secondary status labels at intermediate widths where they caused crowding.
- The PWA cache is updated to serve the new styles.

### Android

- The top bar preserves **10 dp** spacing among Language, Donate, and Appearance, **44 dp** touch targets, and consistent backgrounds and shapes.
- Test tags are added to these buttons to detect overlap in Compose instrumentation tests.
- Existing PNG branding and bottom navigation remain behaviorally unchanged.

### Delivery

- Android: `versionName 3.5.1`, `versionCode 10`.
- Web: version `3.5.1`; `npm run check` also checks the internationalization file.
- `scripts/build-termux.sh` runs unit tests, builds the project, checks that the APK is nonempty, and copies it to `~/storage/downloads/Reqestra-3.5.1-debug.apk`.
- Web tests check SVG dimensions, donation flow, theme support, accessible labels, and translations.
- Android instrumentation tests check that Language, Donate, and Appearance controls are visible and do not overlap.

## Building in Termux

```bash
termux-setup-storage
cd ~/ReqestraMobileHTTP
git pull --ff-only origin main
cd web
npm run check && npm test
cd ..
bash scripts/build-termux.sh --open
```

**Important installation warning:** the debug APK uses `applicationId=com.imr.reqestra.debug`, while the production app uses `com.imr.reqestra`. Therefore, a debug build **cannot replace an installed release build** or recover its data. Updating a release without data loss requires the same application ID and signing key. Do not uninstall the previous version just to force installation.

## Recommended checks before distribution

1. Inspect the web top bars and donation menu at **320, 360, 390, 480, 750, 1024, and 1440 px**.
2. Repeat with both themes and all six languages.
3. Verify contrast, keyboard focus order, and TalkBack.
4. Confirm that Donate leads to the information screen and that the final link opens `www.paypal.com` with the correct beneficiary.
5. Test Android on narrow screens, with larger fonts and both themes.
6. Run all tests and builds in Termux or a CI runner.

**Known limitations:** GitHub Actions previously failed before runner assignment. Code tests do not replace visual verification on physical devices. The account owner must verify the PayPal beneficiary's identity.
