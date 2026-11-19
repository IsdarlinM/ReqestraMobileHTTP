# Reqestra 3.5.1 Scope

Version **3.5.1** delivers high-impact fixes for visual design, accessibility, and distribution. Other initiatives remain proposals and are not considered implemented automatically.

## Control spacing and accessibility

- The Android top bar places **Language 🌐**, **Donate ♥**, and **Appearance ☀/🌙** in one row with `Arrangement.spacedBy(10.dp)`.
- It adds `padding(end = 8.dp)`, keeps buttons away from the edge, and preserves **44 dp** touch targets and the six-language menu.
- Build and physical-device verification are still required; static checks reside in `web/tests/layout.test.mjs`.

## Remaining priorities and acceptance criteria

| Priority | Improvement | Acceptance criteria |
| --- | --- | --- |
| P0 | Visual verification of top bar | Language, Donate, and Theme remain separate at 320, 360, 390, 420, and 600 dp in both themes and six languages, without clipping or overlap |
| P0 | Restore Android and web CI | Every GitHub Actions run executes tests rather than failing before runner assignment; a test APK is produced |
| P0 | Document APK upgrades correctly | Distinguish `applicationIdSuffix=".debug"` from the release APK; verify signing, versionCode, and data-preserving upgrades |
| P1 | Automated Compose UI tests | Check independent taps, accessibility, and opening of Language, Donate, and Appearance, including narrow screens |
| P1 | Adaptive web tests | Run visual checks at 320–1440 px with open menus, themes, and languages; detect actual collisions |
| P1 | Internationalization | Test long Russian/French/Portuguese labels, system-language normalization, and validation notices without translating HTTP responses or user data |
| P1 | HTTP robustness | Check cancellation, timeouts, TLS errors, cross-origin redirects, large responses, and preservation of encrypted data |
| P1 | Secure donations | Verify the public PayPal URL and beneficiary; never use an administrative URL as the payment destination |
| P2 | Accessibility | Verify screen-reader labels, focus order, contrast, and 150%/200% font sizes |
| P2 | Performance and UX | Optimize large JSON responses, loading states, and navigation without losing drafts |

## Out of scope for this maintenance release

- Executable remote extensions.
- Cloud synchronization between Android and web.
- Accounts, in-app payments, or new features that change the data model.

Such features require separate design and security review for 3.6 or later.

## Reproducible Termux validation

```bash
cd ~/ReqestraMobileHTTP
git pull --ff-only origin main
cd web && npm run check && npm test && cd ..
./gradlew --no-daemon --console=plain :app:testDebugUnitTest :app:assembleDebug
```

Check the Android APK at 320, 360, and 420 dp with all six languages, both themes, and larger text. Static tests cannot prove the absence of overlap on every device.

**Status:** `versionName` was updated to **3.5.1** and `versionCode` to **10**. A release upgrade requires the same signing key as the previous version; the debug APK is a separate package.

## Changes already implemented in 3.5.1

- Web Donate uses SVG to avoid distortion, with theme-compatible background and border and width-responsive layout.
- Web appearance uses sun/moon SVG icons with accessible action names translated into six languages.
- Android top-bar actions are visibly separated, identifiable by instrumentation tests, and styled consistently.
- A Termux build script requires completed unit tests, verifies the APK, and copies it to Downloads using the versioned filename.
- Web tests cover the support button and appearance control.

Previous GitHub Actions failures appear to have occurred before runners were assigned. CI is not declared repaired, and no APK build is claimed until actual tests complete.
