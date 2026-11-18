# Reqestra 3.0 — estado de implementación y hoja de ruta

## Entregado en esta iteración

- Navegación de cuatro destinos: Estudio, Colecciones, Pruebas, Historial. Inspector, Vista previa, Sesiones y Extensiones se encuentran en «Más herramientas».
- El Estudio conserva el editor HTTP/cURL y muestra un resumen de la última respuesta en la misma pantalla.
- Editor de código de altura más reducida, pestañas desplazables y botón para formatear JSON.
- Colecciones con nombre y carpetas lógicas, guardadas **cifradas** con Android Keystore y AES-GCM en un archivo de preferencias independiente de las sesiones.
- Hasta 12 entornos con variables del tipo {{baseUrl}}. Se expanden únicamente al enviar. Los valores no se muestran en listas.
- Importación OpenAPI **3.x JSON** de operaciones comunes (máximo 50, con servers[0].url absoluto). No descarga referencias externas ni interpreta YAML.
- Ejecutor de colecciones manual y secuencial: máximo 20 peticiones, intervalo mínimo de 1 segundo, assertions de estado y latencia, resultados por petición.
- Catálogo declarativo de extensiones integradas y futuras; **no** ejecuta plugins descargados ni código arbitrario.
- Tests unitarios adicionales para plantillas, assertions y catálogo.
- Protección existente de redirecciones entre orígenes, redacción y cURL preservada.

## Uso

1. En «Estudio» crea una petición y comprueba sus headers, cookies y cuerpo.
2. En «Colecciones» da un nombre a la petición y asígnala a una colección.
3. Agrega un entorno. Introduce variables sin llaves (ej.: baseUrl) y utiliza {{baseUrl}} en la URL, headers o cuerpo.
4. Vuelve a «Estudio» para enviar individualmente. Si hace falta abrir la respuesta completa, usa «Ver inspector».
5. En «Pruebas» elige la colección, fija el código esperado y la latencia máxima, y pulsa «Ejecutar pruebas ahora». **Se enviarán peticiones reales** y POST/PUT/DELETE podrían modificar los sistemas de destino.
6. Para importar rutas OpenAPI, abre «Colecciones» → «Importar OpenAPI JSON» y pega un documento 3.x con URL base.

## Privacidad y seguridad

- El almacenamiento de colecciones puede contener headers, cuerpos y variables sensibles: se guarda cifrado y separado de preferencias de sesión. El sistema bloquea copias de seguridad de la aplicación.
- Nunca incluimos esos datos automáticamente en logs, CI o un servicio de IA.
- El importador OpenAPI lee texto suministrado por el usuario; no hace conexiones ni ejecuta $ref.
- El motor de colecciones reutiliza las salvaguardas de RequestValidator y HttpEngine.
- El botón de automatización es manual; no hay programación autónoma en segundo plano ni carga masiva.
- No se permite a terceros ejecutar plugins arbitrarios con permisos de red o almacenamiento.

## Pendiente: no confundir catálogo con funcionalidad disponible

- Editor JSON con resaltado de sintaxis, números de línea y plegado.
- UI adaptable para tabletas con paneles redimensionables y navegación lateral.
- Room (migración cifrada desde almacenamiento actual), búsqueda avanzada y exportación controlada.
- Importador Postman, OpenAPI YAML, HAR, multipart y archivos binarios seleccionables.
- OAuth 2.0/OIDC + PKCE, perfiles de mTLS y configuración explícita de proxy de depuración.
- Recolección EventListener de DNS/TCP/TLS/TTFB y comparación estructural de respuestas.
- WorkManager para tareas programadas opt-in, subjecto a políticas del sistema y autorización de tráfico.
- Motor avanzado de flujos: extracción JSONPath, variables entre pasos, JSON Schema, límites de reintentos y reportes.
- WebSocket/SSE/GraphQL.
- SDK real de plugins con permisos por capacidad, aislamiento, verificación de firmas y auditoría de dependencias.
- Asistente de IA opt-in con redacción y consentimiento explícitos antes de compartir contenidos.

## Validación

Ejecutar con JDK 17 y SDK Android 36:

    ./scripts/verify.sh

En GitHub Actions: «Android CI». La ejecución anterior en GitHub falló antes de obtener un runner; ese error debe solucionarse en la configuración/infraestructura de Actions. No hay que interpretar un fallo de infraestructura como un fallo de compilación. Ninguna funcionalidad se considera compilada o probada en dispositivo hasta que pase CI o una compilación local completa.

## Próxima fase

Priorizar recompilación verde, instrumentación, editor JSON avanzado y persistencia migrada a Room, con pruebas de regresión para cifrado, navegación y sesiones. No agregar ejecución de plugins ni agentes de IA sin un modelo de permisos y pruebas de seguridad.
