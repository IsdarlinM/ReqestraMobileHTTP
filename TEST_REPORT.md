# Reqestra v2.0.0 — Test Report

Fecha de validación: 25 de julio de 2026.

## Comprobaciones ejecutadas en el entorno de entrega

### Núcleo Kotlin puro

Se compiló y ejecutó con `kotlinc` el conjunto formado por modelos, validación, redacción, composición y `CoreValidation.kt`.

Resultado: **21/21 comprobaciones superadas**.

Cobertura funcional:

- HTTPS aceptado.
- HTTP bloqueado por defecto y permitido con opt-in.
- Credenciales en URL rechazadas.
- Fragmentos URL rechazados.
- `Host` editable.
- Headers de framing bloqueados.
- Inyección CRLF rechazada.
- Métodos personalizados válidos e inválidos.
- Política de cuerpo para GET y POST.
- Decodificación Base64 y rechazo de Base64 inválido.
- Parsing y caracteres de cookies.
- Precedencia de headers y cookies.
- Redacción de headers y query params sensibles.
- Timeout fuera de rango rechazado.

Salida completa: `validation/core-validation.txt`.

### Compilación de contratos

Se compiló con `kotlinc` contra stubs estrictamente limitados a las firmas utilizadas:

- Motor de red + `CookieJar` + repositorio de sesión: **OK**.
- Repositorio Android + cifrado + serialización: **OK**.
- ViewModel + repositorios + motor de red: **OK**.

Estas comprobaciones detectan errores Kotlin y discrepancias en las firmas utilizadas, pero no sustituyen una compilación Android real.

### Validaciones estáticas

- XML Android bien formado.
- Paquetes Kotlin coherentes con `com.imr.reqestra`.
- Imports `Modifier.weight` comprobados.
- Delimitadores Kotlin/KTS balanceados.
- Sin referencias activas al nombre o package anterior.
- Sintaxis del script Linux comprobada con `sh -n`.
- Gradle Wrapper JAR presente y checksum registrado.
- Integridad final del ZIP comprobada con `unzip -t`.

## Pruebas Android incluidas

### Unit tests

- `RequestValidatorTest`: URL, cleartext, Host, framing, custom methods, Base64 y cookies.
- `HttpEngineTest`:
  - Request editado con header/body.
  - Cookie conservada entre peticiones.
  - Cookie recibida en un redirect enviada al siguiente salto.
- `InMemorySessionRepositoryTest`:
  - Headers aislados por origen.
  - Modo sin sesión no persiste headers.

### Instrumented test

- `MainActivityTest`: inicio de la aplicación y renderizado de la pantalla principal.

## Limitación del entorno

El entorno de entrega no dispone de Android SDK y su terminal no pudo resolver `services.gradle.org`. Por ello no fue posible ejecutar aquí:

- `testDebugUnitTest` mediante Gradle.
- Android Lint.
- `assembleDebug`.
- Test instrumentado en emulador/dispositivo.

El intento está registrado en `validation/gradle-attempt.txt`. Ejecuta en Android Studio/SDK 36:

```bash
./scripts/verify.sh
```

O en Windows:

```cmd
scripts\verify.cmd
```
