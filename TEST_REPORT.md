# Reqestra v2.0.0 — Test Report

Validation date: July 25, 2026.

## Checks performed in the delivery environment

### Pure Kotlin core

The models, validation, redaction, composition, and `CoreValidation.kt` were compiled and executed with `kotlinc`.

Result: **21/21 checks passed**.

Functional coverage:

- HTTPS accepted.
- HTTP blocked by default and allowed with explicit opt-in.
- URL credentials rejected.
- URL fragments rejected.
- Editable `Host` header.
- Framing headers blocked.
- CRLF injection rejected.
- Valid and invalid custom methods.
- GET and POST body policies.
- Base64 decoding and rejection of invalid Base64.
- Cookie parsing and allowed characters.
- Header and cookie precedence.
- Redaction of sensitive headers and query parameters.
- Out-of-range timeouts rejected.

Complete output: `validation/core-validation.txt`.

### Contract compilation

Compiled with `kotlinc` against strictly limited stubs corresponding to the interfaces used:

- Network engine + `CookieJar` + session repository: **OK**.
- Android repository + encryption + serialization: **OK**.
- ViewModel + repositories + network engine: **OK**.

These checks detect Kotlin errors and signature mismatches, but cannot replace a real Android build.

### Static checks

- Well-formed Android XML.
- Kotlin packages consistent with `com.imr.reqestra`.
- Checked `Modifier.weight` imports.
- Balanced Kotlin/KTS delimiters.
- No active references to the previous name or package.
- Linux script syntax checked with `sh -n`.
- `WrapperBootstrap.java` compiled with JDK 17.
- Bootstrap tested against the official Gradle 8.13 JAR.
- Required SHA-256: `81a82aaea5abcc8ff68b3dfcb58b3c3c429378efd98e7433460610fecd7ae45f`.
- Final ZIP integrity verified with `unzip -t`.

## Included Android tests

### Unit tests

- `RequestValidatorTest`: URL, cleartext, Host, framing, custom methods, Base64, and cookies.
- `HttpEngineTest`:
  - Edited request with headers/body.
  - Cookie preserved between requests.
  - Cookie received during a redirect sent on the next hop.
- `InMemorySessionRepositoryTest`:
  - Headers isolated by origin.
  - No-session mode does not persist headers.

### Instrumentation test

- `MainActivityTest`: app startup and main-screen rendering.

## Environment limitations

The delivery environment did not have Android SDK, and its terminal could not resolve `services.gradle.org`. Consequently, it could not execute:

- Gradle `testDebugUnitTest`.
- Android Lint.
- `assembleDebug`.
- Instrumentation tests on an emulator or device.

The attempt is recorded in `validation/gradle-attempt.txt`. Run the following with Android Studio/SDK 36:

```bash
./scripts/verify.sh
```

Or on Windows:

```cmd
scripts\verify.cmd
```
