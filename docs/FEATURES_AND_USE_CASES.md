# Reqestra 3.3 — Manual de funcionalidades y casos de uso

**Estado:** cliente Android nativo (Kotlin/Jetpack Compose) y cliente web instalable (HTML/CSS/JavaScript). Ambos comparten la finalidad de construir, enviar, inspeccionar y probar solicitudes HTTP contra sistemas bajo autorización. No son un proxy de interceptación, un navegador sin restricciones, un motor de bytes TCP ni un escáner masivo.

## Índice

1. [Correspondencia Android / Web](#1-correspondencia-android--web)
2. [Navegación y pantallas](#2-navegación-y-pantallas)
3. [Constructor HTTP y parametrización](#3-constructor-http-y-parametrización)
4. [Sesiones, secretos y persistencia](#4-sesiones-secretos-y-persistencia)
5. [Colecciones, importaciones e historial](#5-colecciones-importaciones-e-historial)
6. [Pruebas automatizadas](#6-pruebas-automatizadas)
7. [Exploración de rutas y parámetros](#7-exploración-de-rutas-y-parámetros)
8. [Inspector y vista previa](#8-inspector-y-vista-previa)
9. [Donaciones con PayPal](#9-donaciones-con-paypal)
10. [Casos de uso paso a paso](#10-casos-de-uso-paso-a-paso)
11. [Límites conocidos y funcionalidades futuras](#11-límites-conocidos-y-funcionalidades-futuras)

## 1. Correspondencia Android / Web

| Capacidad | Android 3.3 | Web directo | Web gateway privado |
| --- | --- | --- | --- |
| GET/POST/PUT/PATCH/DELETE/HEAD/OPTIONS | Sí | Sí, sujeto a Fetch/CORS | Sí, con allowlist |
| Método HTTP personalizado | Sí | Condicionado al navegador | No, whitelist de siete métodos |
| Query estructurada, repetida y codificada | Sí | Sí | Sí |
| Variables en URL/headers/query/cookies/body | Sí | Sí | Sí |
| Content-Type editable | Sí | Sí, sujeto a CORS | Sí |
| Headers arbitrarios permitidos | Semántica OkHttp | Restringidos por navegador | Headers permitidos por Node, sin framing ni Host |
| Header Host personalizado | Android | No | No |
| Cookies manuales | Sí | No | Sí, en petición |
| Cookies de sesión persistentes | Keystore | Políticas del navegador; no gestionables por JavaScript | No hay jar HTTP persistente automático |
| Headers de sesión por origen | Sí | Sí; persistencia web cifrada bajo contraseña | Sí |
| Redirecciones | Configurables y controladas por cliente | Fetch, con restricciones/opacidad | No se siguen |
| Timeout/cancelación | Sí | Sí | Sí |
| HTTP sin TLS de laboratorio | Opt-in + config Android | Depende de origen, mixed content y navegador | Opt-in + allowlist |
| Preferencia HTTP/1.1 | Sí | No | No configurable |
| Cuerpos RAW/JSON/HTML/XML/Form/GraphQL/Base64 | Sí | Sí | Sí |
| Inspector de status/headers/body | Sí | Solo headers expuestos por CORS | Headers de respuesta transmitidos por gateway |
| Vista previa HTML aislada | WebView sin JavaScript ni red | Iframe con sandbox/CSP | La misma vista |
| Colecciones locales, 120 peticiones | Sí | Sí | Sí |
| Entornos locales, 12 | Sí | Sí | Sí |
| Importación OpenAPI 3 JSON, 50 operaciones | Sí | Sí | Sí |
| Importación cURL | Sí | Subconjunto simple | Subconjunto simple |
| cURL redactado | Sí | Sí | Sí |
| Pruebas secuenciales, 20 por colección | Sí | Sí | Sí |
| Exploración, máximo 24 peticiones a 1/segundo | Sí | Sí, sujeto a CORS | Sí, solo allowlist |
| GET/HEAD/OPTIONS y métodos mutantes opt-in | Sí | Sí | Sí |
| Historial local redactado, 50 | Sí | Sí | Sí |
| Almacenamiento confidencial | Android Keystore/AES-GCM | AES-GCM WebCrypto, PBKDF2 con contraseña | En navegador; gateway no almacena |
| Pago externo PayPal voluntario | Sí, configurable | Sí, configurable | Igual que web |
| Sincronización automática móvil/web | No | No | No |

**Importante:** la versión web proporciona el mismo conjunto de pantallas y los flujos centrales; no puede reproducir íntegramente algunas capacidades de OkHttp por restricciones del navegador. El gateway privado cubre CORS y cookies manuales para los orígenes que configures, pero **no** equivale a un proxy abierto ni a un interceptor de bajo nivel.

## 2. Navegación y pantallas

### Estudio

Editor de URL, método, query, headers, cookies, tipo de cuerpo, datos, timeout y redirecciones. Botones de enviar, cancelar, formatear JSON, importar cURL y copiar cURL redactado. Presenta un resumen de la última respuesta. En la web, el usuario elige motor directo o gateway privado.

### Colecciones

Guardar una solicitud con nombre y colección; cargarla, eliminarla, importar operaciones OpenAPI y administrar entornos y variables. Los datos de los dos clientes **no** se sincronizan automáticamente.

### Pruebas

Escoger colección, código HTTP esperado y máximo de latencia. Ejecutar una por una las solicitudes guardadas, mostrando PASS/FAIL y el error de red si existe.

### Explorar

Candidatos de rutas absolutas, nombres de parámetros de consulta, sugerencias pasivas de la última respuesta, selección de métodos HTTP y confirmación de autorización. Presenta el resultado individual por intento.

### Historial

Método, URL con parámetros redactados, status, duración y fecha. Límite de 50 entradas. La URL redactada puede requerir editar tokens o parámetros al reutilizar.

### Inspector

Petición preparada, status, headers disponibles, protocolo reportado, duración, cuerpo y marcador de truncamiento. No es un analizador de tráfico de otras apps.

### Vista previa

Android usa WebView aislada. Web usa iframe con sandbox y CSP restrictiva en modo HTML, o texto monoespaciado en otros modos. No representa el contenido como un navegador de Internet normal.

### Sesiones

Android: sin sesión, temporal en memoria o persistente cifrada, con cookies/headers por origen. Web: sin sesión, memoria o persistente para **headers** cifrados; cookies gestionadas por el navegador según CORS y sus políticas. El gateway no mantiene jar de cookies automáticamente.

### Extensiones

Inventario de funciones integradas y previstas; no se descargan ni ejecutan plugins de terceros.

### Donaciones

Acceso voluntario a PayPal desde la pantalla Apoyar proyecto (Android: menú de herramientas; Web: Donar / Ayuda).

## 3. Constructor HTTP y parametrización

### 3.1 URL completa

El cliente acepta una URL HTTP o HTTPS absoluta con host. Se puede incluir esquema, puerto, ruta y query. Se rechazan credenciales embebidas en la URL y fragmentos. HTTP sin cifrar requiere activación de laboratorio y puede seguir bloqueado por Android o el navegador.

Ejemplo:

    https://api.example.com:8443/v1/users?page=1

### 3.2 Métodos

GET, HEAD, OPTIONS, POST, PUT, PATCH, DELETE; Android permite además método custom. Los cuerpos GET y HEAD se rechazan para evitar ambigüedad. POST/PUT/PATCH pueden enviar un cuerpo vacío. El motor web local admite solo los siete métodos estándar mencionados.

### 3.3 Parámetros Query estructurados

Escribe una pareja por línea. Se añaden a la query existente y cada valor se codifica; pueden existir nombres repetidos.

    page=2
    filter={{searchTerm}}
    tag=red
    tag=blue

Límite: 40 entradas estructuradas por solicitud. Las variables se sustituyen antes de construir la URL.

### 3.4 Variables por entorno

Se crean en Colecciones → Entornos; el nombre usa letras, números y guión bajo y empieza por letra. La sintaxis es doble llave. Se expanden URL, Query, headers, cookies, Content-Type y body.

    baseUrl = https://api.example.com
    token = [valor privado]
    searchTerm = developer

URL:

    {{baseUrl}}/v1/users

Headers:

    Accept: application/json
    Authorization: Bearer {{token}}
    X-Trace: reqestra

Body:

    {"search":"{{searchTerm}}"}

Si falta una variable, la solicitud no se envía. No hay evaluación de expresiones ni acceso a variables de entorno del sistema.

### 3.5 Headers y precedencia

Se escriben con el formato Nombre: Valor. Android combina headers de sesión del origen con los específicos; estos últimos prevalecen. En web se respeta esa precedencia, pero no todos los headers pueden ser establecidos desde JavaScript. Los campos Connection/Content-Length/Transfer-Encoding/Upgrade/TE se reservan al motor. El motor web gateway también reserva Host.

### 3.6 Cookies

Android administra cookies de sesión con ámbito de dominio, ruta, expiración y Secure. Los valores del editor pueden prevalecer sobre la sesión.

En web directo, JavaScript no puede imponer el header Cookie ni leer cookies HttpOnly; el usuario puede escoger un modo que permite enviar cookies que gestione el navegador. El gateway permite enviar cookies manuales declaradas en el editor, pero no mantiene automáticamente una jar de cookies de respuestas previas.

### 3.7 Tipos de cuerpo

Sin cuerpo, Raw, JSON, HTML, XML, application/x-www-form-urlencoded, GraphQL y binario como Base64. Content-Type es editable. El tamaño máximo de cuerpo enviado es 5 MiB y el máximo recibido para inspección es 4 MiB. En la web se usa UTF-8 para cuerpos de texto; el modo Base64 se decodifica a bytes.

### 3.8 Otras opciones

Timeout 1-120 segundos, cancelación de solicitud y seguimiento opcional de redirecciones. La configuración de HTTP/1.1 se respeta solo en Android. Los navegadores pueden presentar respuestas de redirección como opaqueredirect si no se permite seguirlas.

### 3.9 Redacción

Los headers sensibles y los valores de query se redactan al registrar resultados y compartir cURL; un cuerpo puede contener secretos y **no se considera automáticamente redactado al mostrarlo en el Inspector**. No compartas manualmente cuerpos sensibles.

## 4. Sesiones, secretos y persistencia

### Android

Los datos de colecciones, entornos y sesiones persistentes se cifran con AES-GCM y claves protegidas por Android Keystore. No se exportan automáticamente; backup de datos internos deshabilitado. Sin sesión no se persisten cookies. Los headers de sesión se aíslan por origen.

### Web

El espacio web comienza **temporal**. Crear un espacio cifrado requiere una contraseña de al menos diez caracteres. El navegador deriva una clave mediante PBKDF2-SHA256 (260 000 iteraciones) y guarda datos cifrados con AES-256-GCM en localStorage. La contraseña y la clave de descifrado se mantienen solo en memoria de la pestaña. Cuando se cierra o se bloquea, hay que volver a desbloquear.

Se guardan cifrados: solicitudes, variables, headers persistentes, borrador y registro redactado de historial. No se guardan respuestas completas. Los respaldos son archivos JSON **cifrados**, no contienen el texto plano de las variables.

**No hay recuperación de contraseña, sincronización cloud, cuentas de usuario ni compartición automática entre dispositivos.** La protección depende también del navegador y del equipo; una extensión maliciosa, XSS o malware con acceso a la sesión desbloqueada pueden leer datos en memoria.

## 5. Colecciones, importaciones e historial

- Máximo 120 peticiones guardadas y 12 entornos.
- Guardar una petición almacena la plantilla sin resolver variables; el entorno activo determina sus valores al enviar.
- Se importan OpenAPI 3.x JSON con base HTTP(S) absoluta y hasta 50 operaciones por importación; YAML, referencias remotas y autenticación OpenAPI avanzada no se resuelven.
- El importador cURL de Android admite un subconjunto de opciones POSIX; el web importa un subconjunto simple y no ejecuta comandos de shell.
- El exportador cURL web resuelve las variables y redacta los valores de query, headers sensibles y body. No se exportan credenciales.
- Historial local de hasta 50 registros por cliente, sin cuerpos ni secretos.

## 6. Pruebas automatizadas

Una colección de 1 a 20 peticiones puede ejecutarse manualmente de forma **secuencial**, con un intervalo de un segundo entre solicitudes. El usuario define status esperado (100-599) y máximo de latencia (1-120000 ms). Cada resultado incluye status, duración, verificaciones y errores. Una petición fallida no bloquea las siguientes, salvo cancelación.

La ejecución no limita las operaciones a métodos seguros; asegúrate de que no creará, actualizará o borrará datos salvo que esa sea tu intención explícita.

## 7. Exploración de rutas y parámetros

1. Configura en Estudio una URL de **origen autorizado** y, opcionalmente, variables y headers.
2. En Explorar escribe hasta ocho rutas absolutas, como /health o /api/v1.
3. Añade hasta ocho nombres de parámetros candidatos, como include, filter o page.
4. Selecciona GET, HEAD u OPTIONS. Para POST/PUT/PATCH/DELETE activa el control adicional de métodos mutantes.
5. Confirma tener autorización sobre el origen y acepta la ejecución.
6. Revisa cada intento: método, ruta, status, duración, tamaño y, si existe, header Allow.

Hay como máximo **24 solicitudes** por ejecución, **1 por segundo**, con timeout acotado. Se conserva el esquema, host y puerto; no se admiten rutas con secuencias de traversal ni destinos ajenos y no se siguen redirecciones durante el descubrimiento.

### Pistas pasivas

La última respuesta puede contener enlaces HTML, valores JSON href/url/path/endpoint o headers Link. Se muestran candidatos del mismo origen; agregar una pista al formulario **no envía tráfico por sí solo**.

### Parámetros candidatos u ocultos

Para cada nombre propuesto se compara status y tamaño de la respuesta con el baseline sin el parámetro añadido. Una diferencia es solo una **pista heurística**: contenido dinámico, errores, CDN o parámetros ignorados pueden producir variaciones. No se realizan fuzzing masivo, recursión, subdominios ni ataque contra autenticación.

## 8. Inspector y vista previa

Se inspeccionan datos de la solicitud preparada, status, protocolo disponible, tiempo, headers y un cuerpo truncado a 4 MiB. En web los headers expuestos dependen de CORS. El preview HTML usa sandbox sin permisos y CSP que bloquea scripts y recursos remotos; no sirve para validar el comportamiento de páginas externas completas.

## 9. Donaciones con PayPal

PayPal aloja la página de contribución y procesa el pago. La app sólo genera una URL fija a paypal.com con identificador público de botón configurado por el propietario. No hay formularios de tarjeta, backend de cobro, webhooks de transacción, recibos ni almacenamiento de datos financieros de PayPal en Reqestra. El botón permanece desactivado cuando falta la configuración.

**La donación es siempre opcional, 100 % voluntaria y no concede funciones, insignias ni beneficios digitales.** Las políticas de Google Play y los requisitos de PayPal deben revisarse antes de publicar la app (véase docs/PAYPAL_DONATIONS.md).

## 10. Casos de uso paso a paso

### UC-01 — Enviar un GET simple

- Actor: desarrollador.
- Preparación: API propia o de prueba que permita GET.
- Acción: Estudio → URL completa → GET → Enviar petición.
- Resultado esperado: status, duración y cuerpo visible en resumen e Inspector.
- Fallos: TLS, DNS, bloqueo CORS, URL inválida o timeout.

### UC-02 — Consultar con token por entorno

- Actor: desarrollador o QA.
- Acción: Colecciones → crea token; Estudio → Authorization: Bearer {{token}}.
- Resultado: token insertado en memoria al ejecutar; historial redactado.
- Seguridad: evita compartir capturas del contenido de respuesta.

### UC-03 — Filtrar registros con query

- URL de partida: https://api.example.com/v1/users?active=true.
- Query: page=2 y filter={{searchTerm}}.
- Resultado: ambas parejas se agregan sin perder active=true y con escape URL correcto.

### UC-04 — Enviar POST JSON a un entorno de pruebas

- Método POST; body JSON; Content-Type application/json.
- Los datos se envían solo al pulsar Enviar.
- El usuario verifica si la API crea realmente un recurso; Reqestra no deshace cambios.

### UC-05 — Reutilizar una petición en varios entornos

- Guarda plantilla en una colección; crea entornos Local y QA con baseUrl distinto.
- Cambia entorno y ejecuta la petición guardada.
- No se copian secretos ni se sincronizan cuentas entre equipos.

### UC-06 — Importar y auditar una colección OpenAPI

- Pega especificación JSON v3 con servers[0].url HTTP(S) absoluto.
- Importa; revisa operaciones generadas; ejecuta solo las autorizadas.
- La importación no resuelve automáticamente refs externos ni esquemas complejos.

### UC-07 — Migrar una llamada cURL compatible

- Pega un comando cURL simple en Estudio y pulsa Importar.
- Comprueba método, URL, headers y body antes de enviar.
- Rechaza opciones de TLS inseguro y no ejecuta shell.

### UC-08 — Compartir una petición sin secretos

- Usa Copiar cURL redactado.
- Comprueba que la query, el body y los headers confidenciales están ocultos.
- No sustituyas esto por copiar a mano el Inspector con contenido real.

### UC-09 — Validar código HTTP y latencia

- Guarda 1-20 peticiones en una colección.
- Indica 200 y límite 2000 ms, por ejemplo.
- Ejecuta pruebas y revisa PASS/FAIL individualmente.

### UC-10 — Enumerar pocas rutas autorizadas

- Escoge URL de API de tu laboratorio.
- Propón /health y /api/v1, confirma el permiso y ejecuta.
- Revisa 200/401/403/404/405 manualmente; no concluyas vulnerabilidad sin evidencia.

### UC-11 — Revisar un candidato a parámetro desconocido

- Introduce include y compara con baseline.
- Si cambia status o tamaño, revisa el contenido y reproduce manualmente.
- No se consideran hallazgos confirmados solo por diferir en bytes.

### UC-12 — Comparar métodos HTTP

- Selecciona GET, HEAD y OPTIONS por defecto.
- Habilita métodos de escritura solo en un ambiente con datos desechables y con confirmación.
- La cabecera Allow es informativa; el control de autorización debe comprobarse por separado.

### UC-13 — Cancelar una petición lenta

- Establece timeout dentro de 1-120 segundos.
- Ejecuta y pulsa Cancelar.
- El cliente interrumpe la operación actual; el servidor podría haberla procesado ya.

### UC-14 — Utilizar headers de sesión por origen

- En Sesiones configura headers y selecciona memoria o persistente.
- Envía a la misma URL de origen.
- No deben reenviarse a un dominio diferente. En Web la persistencia requiere almacén desbloqueado.

### UC-15 — Trabajar sin guardar secretos

- Abre Web sin crear un almacén y utiliza el modo temporal.
- Al recargar, los borradores y secretos temporales se pierden.
- Usa un espacio cifrado si quieres persistirlos.

### UC-16 — Recuperar una colección cifrada web

- Desbloquea y descarga el respaldo cifrado.
- En otro navegador importa el archivo y utiliza la misma contraseña.
- Este flujo no importa automáticamente datos Android Keystore.

### UC-17 — Diagnosticar un fallo de CORS

- Envía una petición desde Web directo a una API que no habilita tu origen.
- Aparece error Fetch/CORS; no lo interpretes como fallo de autenticación.
- Usa Android o el gateway local con allowlist del origen, si está autorizado.

### UC-18 — Previsualizar HTML aislado

- En Estudio elige body HTML y pega una plantilla.
- Abre Vista previa: se muestra en sandbox sin ejecutar scripts ni cargar recursos remotos.
- No se usa para navegar websites ajenos.

### UC-19 — Instalar la web como PWA

- Sirve la carpeta web por localhost o HTTPS.
- Instálala desde el navegador compatible.
- La caché sólo conserva la interfaz, no resultados de API ni credenciales.

### UC-20 — Donar voluntariamente al proyecto

- Administrador configura su botón alojado por PayPal.
- Usuario abre Donar/Ayuda o Apoyar proyecto y decide abrir PayPal.
- Los pagos ocurren fuera de Reqestra, sin desbloquear privilegios ni funciones.

## 11. Límites conocidos y funcionalidades futuras

- Sin servidor de sincronización, cuentas, permisos multiusuario ni colaboración real-time.
- Sin proxy de interceptación, sniffing de red, inspector WebSocket, SSE ni generación de tráfico de alto volumen.
- Web directo sujeto a CORS, cookies HttpOnly, headers prohibidos, restricciones mixed-content y redirecciones opacas.
- Gateway opcional exclusivo para loopback con allowlist de orígenes, no apto para publicar sin una arquitectura de autenticación adicional.
- Las donaciones no tienen confirmación de pago dentro de la app ni suscripciones/beneficios.
- OpenAPI sólo JSON v3, sin YAML ni resolución remota.
- No hay importación de HAR/Postman completa ni OAuth2 PKCE automatizado.
- La compilación Android y las pruebas web deben ejecutarse en un entorno con Android SDK/Node; un commit no equivale a un build verificado.

Para configuración, véanse **docs/WEB_SETUP.md**, **docs/PAYPAL_DONATIONS.md**, **docs/ARCHITECTURE_AND_SECURITY.md** y **docs/TEST_PLAN.md**.
