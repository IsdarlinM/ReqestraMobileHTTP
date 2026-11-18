#!/usr/bin/env bash
# Configura la ID pública de un botón de donaciones PayPal en Android y Web.
# No solicita ni almacena credenciales privadas de PayPal.
set -Eeuo pipefail
export LC_ALL=C

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
android_build="$repo_root/app/build.gradle.kts"
web_config="$repo_root/web/config.js"
gradle_dir="${GRADLE_USER_HOME:-$HOME/.gradle}"
gradle_props="$gradle_dir/gradle.properties"

if [[ ! -f "$android_build" || ! -f "$web_config" ]]; then
  printf 'ERROR: ejecuta este script desde un clon completo de Reqestra.\n' >&2
  exit 2
fi

case "${1:-}" in
  --help|-h)
    printf 'Uso: bash scripts/configure-paypal.sh [HOSTED_BUTTON_ID]\n'
    printf 'Sin argumento solicitará la ID pública de un botón PayPal Donate.\n'
    exit 0
    ;;
  '')
    read -r -p 'hosted_button_id público de PayPal: ' paypal_id
    ;;
  *)
    if (( $# != 1 )); then
      printf 'ERROR: solo se acepta una ID pública.\n' >&2
      exit 2
    fi
    paypal_id="$1"
    ;;
esac

if [[ ! "$paypal_id" =~ ^[A-Za-z0-9]{6,64}$ ]]; then
  printf 'ERROR: ID no válida. Usa solo letras/números (6 a 64 caracteres).\n' >&2
  exit 2
fi

# Propiedad local de Gradle fuera del repositorio: no contiene credenciales.
mkdir -p "$gradle_dir"
if [[ -e "$gradle_props" && ! -f "$gradle_props" ]]; then
  printf 'ERROR: gradle.properties no es un archivo normal.\n' >&2
  exit 2
fi
if [[ ! -f "$gradle_props" ]]; then
  : > "$gradle_props"
  chmod 600 "$gradle_props"
fi
scratch="$(mktemp "$gradle_dir/.reqestra-paypal.XXXXXXXX")"
trap 'rm -f "$scratch"' EXIT
awk -v id="$paypal_id" '
  BEGIN { found = 0 }
  /^paypalHostedButtonId=/ {
    if (!found) { print "paypalHostedButtonId=" id; found = 1 }
    next
  }
  { print }
  END { if (!found) print "paypalHostedButtonId=" id }
' "$gradle_props" > "$scratch"
chmod 600 "$scratch"
mv -f "$scratch" "$gradle_props"

# Configuración pública para Web. Este archivo se puede versionar y publicar.
cat > "$web_config" <<CONFIG
// Identificador PUBLICO del boton PayPal Donate del propietario.
// Nunca pongas contraseñas, client secrets, API keys o tokens en este archivo.
window.REQESTRA_CONFIG = Object.freeze({
  paypalHostedButtonId: "$paypal_id",
});
CONFIG

printf '\nConfiguración local completada:\n'
printf '  Android: %s\n' "$gradle_props"
printf '  Web:     %s\n' "$web_config"
printf '  URL:     https://www.paypal.com/donate/?hosted_button_id=%s\n\n' "$paypal_id"
printf 'IMPORTANTE: antes de publicar, abre la URL y verifica que el receptor es tu cuenta PayPal.\n'
printf 'Para incluir la configuración web en GitHub después de verificarla:\n'
printf '  git add web/config.js && git commit -m "chore(paypal): configure public donation button" && git push origin main\n'
printf 'Para compilar Android:\n'
printf '  ./gradlew --no-daemon :app:assembleDebug\n'
