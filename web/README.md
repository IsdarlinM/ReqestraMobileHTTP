# Reqestra Web 3.5

Cliente web multilingüe de Reqestra: Estudio HTTP, query/variables, colecciones cifradas, OpenAPI/cURL, sesiones por origen, pruebas, exploración acotada, historial, inspector, vista previa HTML aislada y donaciones PayPal configurables.

## Iniciar

Requiere Node 20 o superior:

    cd web
    npm run check
    npm test
    npm start

Abre http://127.0.0.1:5173.

## Modalidades de envío

**Navegador:** Fetch directo; está sujeto a CORS, cookies, políticas TLS y headers prohibidos.

**Gateway local privado:** habilita manualmente una lista de orígenes de API bajo tu control:

    REQESTRA_ALLOWED_ORIGINS=https://api.example.com node server.mjs

En la interfaz selecciona **Gateway local autorizado**. No se configura por defecto ni puede exponerse fuera de loopback cuando la allowlist está activa. Para APIs locales propias se requiere REQESTRA_ALLOW_PRIVATE_TARGETS=1 explícito; evita hacerlo en redes compartidas.

## Secretos y almacenamiento

La app funciona en modo temporal sin guardar secretos. Para persistir, crea un almacén cifrado AES-GCM con contraseña (WebCrypto/PBKDF2). Se puede descargar un backup cifrado. No hay recuperación de contraseña ni sincronización automática con Android.

## Donaciones

Crea tu botón de donación desde https://www.paypal.com/donate/buttons y escribe su **ID pública**, sin secretos, en web/config.js. La opción permanece desactivada hasta que exista una ID válida. Los pagos se completan fuera de Reqestra en PayPal.

## Documentación

- [Manual funcional y casos de uso](../docs/FEATURES_AND_USE_CASES.md)
- [Guía completa de instalación y despliegue](../docs/WEB_SETUP.md)
- [Integración PayPal](../docs/PAYPAL_DONATIONS.md)
- [Arquitectura y seguridad](../docs/ARCHITECTURE_AND_SECURITY.md)
- [Matriz de pruebas](../docs/TEST_PLAN.md)

No publiques el gateway en un dominio público ni confundas la ausencia de CORS con un problema del servidor de destino.

## Idioma e identidad visual

Los textos de la interfaz usan inglés de respaldo. Se detecta el idioma principal
 del dispositivo (inglés, español, ruso, mandarín simplificado, portugués o francés);
 el control 🌐 de la barra superior permite elegir manualmente uno de los seis.
 La selección se guarda en localStorage, separada del almacén cifrado de solicitudes.

La PWA, favicon y cabecera comparten el logo R con órbita turquesa mediante web/icon.png.
Los datos de tus solicitudes y respuestas nunca se traducen automáticamente.
Las descripciones extensas heredadas pueden conservar inglés de respaldo y requieren
revisión lingüística antes de presentar el producto como totalmente traducido.

## Auditoría de idiomas 3.5

El HTML inicial está en inglés. El selector 🌐 muestra el idioma activo
con un borde, fondo y ✓ en claro y oscuro, permite seleccionar por
teclado y guarda la selección. Los catálogos locales cubren 328
etiquetas web en los seis idiomas e incluyen plantillas para mensajes
dinámicos. No se traducen URLs, secretos, ni cuerpos de peticiones.

## Acerca de la aplicación y ayuda

La página **About & Help** está disponible en el menú lateral y en la
opción **More** para móviles. Incluye una guía de uso rápida, funciones
y limitaciones del navegador, privacidad, versión y enlaces oficiales
a la [guía de uso](../docs/GUIA_DE_USO.md), la configuración Web y
el repositorio de problemas. No envía solicitudes HTTP automáticamente.

La versión sigue siendo **3.5.0**; solo se renovó la caché interna
de la PWA para propagar los cambios visuales.
