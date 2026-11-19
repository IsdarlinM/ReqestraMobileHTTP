#!/usr/bin/env bash
# Build and verify the 3.5.1 debug APK, then copy it to Android Downloads.
# The debug package is separate from production; no application data is deleted.
set -Eeuo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
VERSION="3.5.1"
LOG="${HOME}/reqestra-${VERSION}-build.log"
DOWNLOADS="${HOME}/storage/downloads"
APK="${ROOT}/app/build/outputs/apk/debug/app-debug.apk"
DEST="${DOWNLOADS}/Reqestra-${VERSION}-debug.apk"

if [[ ! -x "${ROOT}/gradlew" ]]; then
  printf 'ERROR: no encuentro gradlew en %s\n' "${ROOT}" >&2
  exit 1
fi
if [[ ! -d "${DOWNLOADS}" ]]; then
  printf 'ERROR: Downloads no está disponible. Ejecuta termux-setup-storage.\n' >&2
  exit 1
fi

printf 'Compilando Reqestra %s. Registro: %s\n' "${VERSION}" "${LOG}"
if ! (cd "${ROOT}" && ./gradlew --no-daemon --console=plain \
  :app:testDebugUnitTest :app:assembleDebug) >"${LOG}" 2>&1; then
  printf 'ERROR: compilación o pruebas fallidas. Últimas líneas:\n' >&2
  tail -n 80 "${LOG}" >&2
  exit 1
fi
if [[ ! -s "${APK}" ]]; then
  printf 'ERROR: Gradle terminó, pero no existe el APK esperado: %s\n' "${APK}" >&2
  exit 1
fi

cp -- "${APK}" "${DEST}.tmp"
mv -f -- "${DEST}.tmp" "${DEST}"
printf 'APK listo en Descargas: %s\n' "${DEST}"
ls -lh -- "${DEST}"
command -v sha256sum >/dev/null && sha256sum -- "${DEST}"

if [[ "${1:-}" == "--open" ]]; then
  if command -v termux-open >/dev/null; then
    termux-open --view --content-type application/vnd.android.package-archive "${DEST}"
  else
    printf 'Instálalo desde la aplicación Archivos / Descargas.\n'
  fi
fi
