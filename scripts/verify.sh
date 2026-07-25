#!/usr/bin/env sh
set -eu

ROOT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$ROOT_DIR"

./gradlew --version
./gradlew clean testDebugUnitTest lintDebug assembleDebug

echo "[OK] Unit tests, Android lint and debug APK build completed."
echo "APK: app/build/outputs/apk/debug/app-debug.apk"
