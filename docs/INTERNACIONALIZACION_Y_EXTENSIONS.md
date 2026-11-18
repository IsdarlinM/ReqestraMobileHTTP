# Reqestra 3.4: idiomas, logotipo y extensiones

## 1. Idiomas compatibles

| Código | Idioma | Valor predeterminado en dispositivo |
| --- | --- | --- |
| en | Inglés | Cuando el dispositivo usa inglés o no tiene un idioma compatible |
| es | Español | Cuando el idioma principal del dispositivo es español |
| ru | Ruso | Cuando el idioma principal del dispositivo es ruso |
| zh | Mandarín, escritura simplificada | Cuando el idioma principal comienza por zh (por ejemplo zh-CN) |
| pt | Portugués | Cuando el idioma principal comienza por pt |
| fr | Francés | Cuando el idioma principal comienza por fr |

El idioma es una **preferencia de interfaz**; no altera cuerpos HTTP, credenciales,
headers ni respuestas de APIs. El catálogo fuente es local y no usa servicios de
traducción remotos que puedan recibir datos sensibles.

### Selección automática

- Android: idioma principal desde la configuración del dispositivo,
  con inglés como respaldo. Las preferencias manuales se guardan
  en `SharedPreferences("reqestra_ui")`.
- Web: se lee `navigator.languages[0]` (o `navigator.language`), no los
  idiomas secundarios. El cambio manual se guarda en `localStorage`
  bajo `reqestra.locale`.
- El selector 🌐 de la **barra superior** muestra las seis alternativas.
- Si el usuario cambia manualmente el idioma, esa selección prevalece sobre
  la detección automática en aperturas posteriores.
- Chino utiliza `zh` y muestra caracteres **simplificados**.
- Si el idioma del sistema no está soportado, se usa inglés, nunca español por defecto.

### Cobertura actual y calidad

Se localizaron los menús principales, encabezados, controles, etiquetas,
acciones y gran parte de las explicaciones de interfaz. Los mensajes técnicos
producidos por validadores heredados y algunas descripciones especializadas pueden
seguir presentándose en inglés de respaldo (o, en Android, una cadena heredada)
mientras se completa la localización integral. Esto **no** constituye
certificación de traducción profesional. Para lanzamiento público, se recomienda
revisión de las seis variantes por hablantes especializados en HTTP/ciberseguridad.

### Archivos

- `web/lib/i18n.mjs`: catálogo Web, selección, persistencia y traducción
  de nodos de interfaz, incluyendo tarjetas creadas dinámicamente.
- `app/src/main/java/com/imr/reqestra/presentation/i18n/ReqestraLanguages.kt`:
  catálogo Compose, normalización y traducciones.
- `app/src/main/res/xml/locales_config.xml`: idiomas declarados ante Android.
- `app/src/main/java/com/imr/reqestra/presentation/ReqestraApp.kt`:
  icono del selector 🌐 y persistencia del idioma.

## 2. Identidad visual consistente

El propietario eligió un **monograma R con órbita cian sobre fondo azul oscuro**.
La copia reducida del PNG original se almacena en:

- `app/src/main/res/drawable-nodpi/reqestra_brand.png` — Android launcher,
  icono redondo y cabecera Compose.
- `web/icon.png` — cabecera web, favicon, instalador PWA.

El original de alta resolución debe conservarse fuera del repositorio si el
propietario quiere preparar materiales de impresión u otras resoluciones.
La conversión para aplicaciones está reducida y optimizada; puede haber pérdida
leve de gradientes respecto al original.

## 3. Extensiones integradas que se pueden abrir

| Extensión Android | Pantalla real | Estado |
| --- | --- | --- |
| Collection Manager | Colecciones | Integrada |
| Environment Variables | Colecciones → entornos | Integrada |
| Test Assertions | Pruebas | Integrada |
| cURL Toolkit | Estudio → cURL | Integrada |
| OpenAPI Importer | Colecciones → importación | Integrada |
| Query Parameterizer | Estudio → Query | Integrada |
| Endpoint Explorer | Explorar | Integrada |
| GraphQL Schema Explorer | Sin pantalla implementada | Planeada |
| Proxy Connector | Sin implementación | Planeada |
| AI Assistant | Sin implementación | Planeada |

Las extensiones integradas son **módulos internos**, no APK independientes.
Su botón Abrir lleva a la pantalla correspondiente. Para funciones planeadas,
no se presenta un botón de ejecución engañoso.

### Web

Estudio, entornos, colecciones cifradas, pruebas, exploración, importación cURL y
OpenAPI, sesiones y gateway privado están enlazados desde Extensiones.
El gateway solo funciona si se configura `REQESTRA_ALLOWED_ORIGINS` en el
servidor local; abrir la pantalla no activa por sí solo el gateway.
No se ofrecen extensiones descargables, interceptación de otras apps ni
manipulación arbitraria de paquetes TCP.

## 4. Matriz de comprobación

| Caso | Android | Web | Resultado esperado |
| --- | --- | --- | --- |
| Dispositivo con es-DO | Sí | Sí | Español al abrir sin preferencia previa |
| Dispositivo con ru-RU | Sí | Sí | Ruso |
| Dispositivo con zh-CN | Sí | Sí | Chino simplificado |
| Dispositivo con pt-BR | Sí | Sí | Portugués |
| Dispositivo con fr-FR | Sí | Sí | Francés |
| Dispositivo con it-IT | Sí | Sí | Inglés de respaldo |
| Selección manual 🌐 | Sí | Sí | Re-render de interfaz y persistencia |
| Cambiar idioma durante un request | Sí | Sí | No modificar el request/response |
| Extensiones disponibles | Sí | Sí | Navegación a herramienta |
| Extensiones futuras | Sí | Sí | Estado planeado; sin ejecución |
| Logo en cabecera | Sí | Sí | Misma imagen elegida |
| Logo de aplicación | Sí | PWA | Misma imagen optimizada |

## 5. Comandos de validación

### Web
```bash
cd ~/ReqestraMobileHTTP/web
npm run check
npm test
node server.mjs
```

### Android
```bash
cd ~/ReqestraMobileHTTP
./gradlew --no-daemon testDebugUnitTest assembleDebug --stacktrace
```

Las pruebas unitarias no sustituyen verificaciones visuales en dispositivos,
CORS real, accesibilidad ni la validación de gradientes del logo.
La actualización a GitHub no demuestra por sí sola que el APK esté compilado.


## 6. Navegación y visibilidad de las donaciones

### Android

La barra superior utiliza un encabezado alineado a la izquierda con el icono
corporativo. A su derecha, y **antes** del selector de idioma 🌐 y del botón
de menú ⋮, se ubica el acceso directo a donaciones.

- En teléfonos estrechos, el acceso se representa como un botón táctil de
  **corazón** con descripción accesible localizada.
- En pantallas más anchas, muestra el **corazón y el texto Donate/Donar**
  traducido al idioma de interfaz.
- En ambos casos, el botón conduce a la pantalla informativa de apoyo al proyecto,
  donde el usuario elige explícitamente abrir la página oficial PayPal.
  No inicia pagos ni cambia permisos por sí solo.

La barra inferior tiene **cinco** destinos: Estudio, Colecciones, Pruebas,
Explorar y Más. Los cuatro primeros abren funciones principales y Más
despliega una hoja inferior con filas de iconos, títulos y descripciones
para Historial, Inspector, Vista previa, Sesiones, Extensiones y Apoyar
proyecto. El icono ⋮ de la cabecera abre la **misma hoja**: ambos accesos
comparten un solo menú y no hay opciones desconectadas.

La etiqueta y la función semántica seleccionada de cada pestaña se
actualizan con el destino activo. Las etiquetas largas pueden usar
puntos suspensivos visuales en pantallas pequeñas, pero el botón conserva
su descripción de accesibilidad.

### Web

La barra superior muestra permanentemente un botón contrastado
**Donate/Donar** con icono ♥, incluso en la página principal. Se
encuentra junto al selector de idiomas y al control de apariencia.
En pantallas muy estrechas se conserva un botón de corazón de 44 px
con nombre accesible, para evitar que se solape con el logotipo.
El botón abre la sección **Support/Apoyar proyecto**, no PayPal directamente.

El sitio web mantiene el menú lateral de escritorio y cinco
accesos inferiores para dispositivos móviles; se añadieron
iconos a las acciones de navegación móvil.

### Seguridad, voluntariedad y configuración

- La aportación es opcional y **no activa funciones**.
- Solo la pantalla de apoyo ofrece el enlace saliente de pago,
  con el dominio oficial de PayPal, si existe una ID pública válida.
- El \`hosted_button_id\` configurado por el propietario proviene de
  un enlace de administración; se debe **verificar por separado**
  la página pública y la cuenta beneficiaria antes de publicar.
- El botón no recopila números de tarjeta ni credenciales en Reqestra.

### Comprobación rápida

1. Abre Android y Web en inglés y español; revisa el texto del botón.
2. Comprueba teléfonos de 320, 360 y 420 dp/px para detectar
   desbordamientos de cabecera y navegación.
3. En Android pulsa ⋮ y Más: ambos deben mostrar la misma hoja.
4. Abre cada una de las seis herramientas secundarias y confirma
   que la pantalla correspondiente se presenta.
5. Pulsa Donate/Donar: debe abrir la pantalla de apoyo sin lanzar
   un pago automáticamente.
6. Comprueba PayPal solo tras verificar el destinatario.
