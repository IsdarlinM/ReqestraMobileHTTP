# Reqestra 3.2.0 — Parametrización y exploración controlada

## Parámetros, variables y headers

En Estudio, introduce una URL completa o una plantilla, por ejemplo:

    {{baseUrl}}/v1/users

En Colecciones > Entornos crea las variables `baseUrl` y `token` con sus valores. En la pestaña Query del Estudio escribe parámetros de consulta, uno por línea:

    page=2
    filter={{filter}}
    expand=roles,permissions

La URL original conserva su query; los parámetros nuevos se añaden en orden y sus valores se codifican (espacios, & y =). Se permiten hasta 40 parámetros estructurados por petición. Se pueden usar nombres duplicados intencionalmente, como tag=x y tag=y.

El editor Headers también acepta variables: `Authorization: Bearer {{token}}`, `X-Tenant: {{tenant}}`. Lo mismo se aplica a cookies manuales, body y Content-Type. Las variables se resuelven inmediatamente antes del envío, no al guardar la petición. Las peticiones guardadas se conservan cifradas por Android Keystore.

Copiar cURL redactado usa la petición resuelta, incluida la query estructurada, pero elimina los valores confidenciales del comando exportado. La importación cURL no ejecuta scripts.

## Explorar API

1. En Estudio configura la URL, el entorno, los headers y los parámetros normales.
2. Abre Explorar. La URL del Estudio define el origen (esquema, host y puerto) para todo el análisis.
3. Opcionalmente introduce rutas absolutas, una por línea, como `/health` o `/api/v1`. Hasta 8.
4. Opcionalmente escribe nombres de parámetros candidatos para la URL actual, por ejemplo `include` o `page`. Hasta 8.
5. Opcionalmente incorpora pistas pasivas extraídas de la última respuesta (JSON href/url/path/endpoint, enlaces HTML o cabeceras Link). Estas pistas no se ejecutan automáticamente.
6. Elige los métodos. GET, HEAD y OPTIONS están disponibles por defecto. POST, PUT, PATCH y DELETE requieren habilitación manual expresa.
7. Confirma autorización para el origen y pulsa Ejecutar. Puedes cancelar en cualquier momento.

La ejecución se limita a 24 peticiones HTTP secuenciales, 1 por segundo, sin seguir redirecciones, sin usar sesiones persistentes y con timeout de hasta 15 segundos por petición. Se conserva el mismo esquema/host/puerto de origen. El cliente reutiliza los headers y cookies introducidos manualmente en Estudio, por lo que solo debes probar servicios donde tengas autorización para utilizar esas credenciales.

Los resultados muestran estado HTTP, duración aproximada, bytes de respuesta y cabecera Allow si está presente. Para parámetros candidatos compara código y longitud de la respuesta con una petición de referencia. Una diferencia es una **observación heurística**, no prueba que el parámetro sea oculto, haya fuga de datos, ni que exista una vulnerabilidad.

## Seguridad y límites

- Sin listas integradas de fuerza bruta, descubrimiento recursivo, detección de subdominios ni escaneo de redes.
- El origen se valida antes de cada petición; no se prueban redirecciones a destinos ajenos.
- No se leen secretos del sistema ni se envían contenidos a servicios externos de análisis.
- Los métodos que pueden modificar recursos están desactivados por defecto y requieren otra confirmación.
- El usuario debe verificar los límites de su programa bug bounty, entorno de laboratorio o API propia.
- Las respuestas pueden ser dinámicas: un cambio de bytes no confirma un parámetro oculto.
- La selección de métodos no equivale a una prueba completa de controles de autorización; revisar manualmente 401/403/405.
- El comportamiento 'seguro' de GET/HEAD/OPTIONS depende de implementaciones correctas del servidor.

## Arquitectura

- `core/variables/RequestParameterizer.kt`: expansión de plantillas y query codificada.
- `core/discovery/EndpointExplorer.kt`: planificación, restricciones de origen, extracción pasiva y ejecución secuencial.
- `presentation/discovery/DiscoveryScreen.kt`: interfaz y confirmación de consentimiento.
- `presentation/MainViewModel.kt`: coordinación y cancelación.
- `data/workspace/WorkspaceStore.kt`: persistencia del texto de query de colecciones sin alterar los archivos guardados de versiones anteriores.

## Verificar versión

    git pull --ff-only origin main
    grep 'versionName =' app/build.gradle.kts
    ./gradlew --no-daemon testDebugUnitTest assembleDebug --stacktrace

La compilación no debe declararse aprobada hasta ejecutar Gradle con SDK Android 36 en Termux o Android Studio. El workflow de GitHub Actions en ejecuciones anteriores falló antes de obtener un runner.
