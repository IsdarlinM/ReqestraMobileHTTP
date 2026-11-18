# Reqestra 3.3 — Plan de pruebas funcionales, de seguridad y accesibilidad

## Criterios de aceptación

1. Los dos clientes conservan sus funciones existentes de composición HTTP.
2. Los límites de tamaño, tasa y alcance de descubrimiento se cumplen.
3. Los secretos no aparecen en el historial, cURL exportado ni backup web en texto plano.
4. No hay acceso a PayPal hasta configurar una ID pública válida; todas las funciones son accesibles sin donar.
5. No se consideran verificadas las compilaciones hasta obtener resultados reproducibles de Gradle/Node/navegador.

## 1. Pruebas automatizadas Web

Requisitos: Node 20 o posterior, sin dependencias externas.

    cd web
    npm run check
    npm test

Archivos: web/tests/core.test.mjs y web/tests/vault.test.mjs. Cubren expansión de variables, query, errores de validación, importación OpenAPI, ruta de mismo origen, métodos mutantes desactivados, heurística pasiva, cURL redactado, assertions y cifrado de repositorio WebCrypto.

**Nota:** estas pruebas de unidad no ejercitan automáticamente el DOM real, CORS ni los permisos de navegador.

## 2. Pruebas automatizadas Android

    cd ~/ReqestraMobileHTTP
    ./gradlew --no-daemon testDebugUnitTest assembleDebug --stacktrace

Además de las pruebas existentes sobre HTTP, sesiones, cURL, descubrimiento, colecciones y validación, PayPalDonationsTest comprueba la construcción segura de un enlace HTTPS a www.paypal.com y el rechazo de IDs maliciosas/vacías.

Históricamente el workflow de GitHub Actions ha fallado incluso antes de asignarse un runner; no tomes un commit subido como confirmación de compilación correcta. Compila localmente y conserva el log de error.

## 3. Matriz manual de funciones

| ID | Componente | Prueba | Resultado esperado |
|---|---|---|---|
| WEB-01 | Estudio | GET a API con CORS autorizado | HTTP/status/body/latencia |
| WEB-02 | URL | URL con usuario:contraseña | Rechazo |
| WEB-03 | HTTP | Desactivar allowCleartext y enviar http | Rechazo |
| WEB-04 | Headers | Intentar Content-Length manual | Rechazo |
| WEB-05 | Cookies | Cookie manual con motor navegador | Mensaje de limitación |
| WEB-06 | Query | existing=1 y filtro con espacios | Se conserva existing y se codifica filtro |
| WEB-07 | Variables | Variable {{token}} no definida | Error antes de enviar |
| WEB-08 | Variables | token con valor sensible | No aparece en historial ni exportación cURL |
| WEB-09 | Body | JSON inválido + botón formatear | Mensaje sin pérdida de texto |
| WEB-10 | cURL | Importación cURL simple | Draft cargado y editable |
| WEB-11 | cURL | cURL con -k | Rechazo de TLS inseguro |
| WEB-12 | OpenAPI | Documento 3.x con GET/POST | Importa 2 operaciones |
| WEB-13 | OpenAPI | YAML o servidor relativo | Rechazo documentado |
| WEB-14 | Vault | Crear, cerrar, desbloquear | Datos cifrados recuperados |
| WEB-15 | Vault | Contraseña incorrecta | Rechazo sin exponer datos |
| WEB-16 | Vault | Exportar e importar backup | Se solicita misma passphrase |
| WEB-17 | Colecciones | Guardar y recuperar draft | Campos conservados |
| WEB-18 | Colecciones | Eliminar una petición | Requiere confirmación |
| WEB-19 | Entornos | Cambiar Local a QA | Las variables cambian |
| WEB-20 | Pruebas | Colección 2 solicitudes | PASS/FAIL y progreso |
| WEB-21 | Pruebas | Cancelación durante test | Interrupción sin nuevas solicitudes |
| WEB-22 | Discovery | /health con mismo origen | Resultado de ruta |
| WEB-23 | Discovery | Intentar URL externa como ruta | Rechazo |
| WEB-24 | Discovery | 9 rutas | Rechazo por límite |
| WEB-25 | Discovery | DELETE sin habilitar | Bloqueo |
| WEB-26 | Discovery | DELETE con activación + confirmación | Envío intencional |
| WEB-27 | Discovery | Parámetro extra | Diferencia de status/tamaño si existe |
| WEB-28 | Preview | HTML con script e imagen remota | No ejecuta scripts ni carga recursos externos |
| WEB-29 | Historial | 51 peticiones | Máximo 50 filas |
| WEB-30 | Gateway | Sin allowed origins | 403 y gateway desactivado |
| WEB-31 | Gateway | Origen distinto de allowlist | Bloqueado |
| WEB-32 | Gateway | 127.0.0.1 sin opt-in | Bloqueado |
| WEB-33 | Gateway | Allow private targets explícito en laboratorio | Acceso solo al origen autorizado |
| WEB-34 | Gateway | Redirección 302 | No sigue Location |
| WEB-35 | PayPal | ID vacía | Botón desactivado |
| WEB-36 | PayPal | ID pública válida | Enlace solo a www.paypal.com |
| WEB-37 | PWA | Instalación y modo offline | Solo shell disponible; no respuestas HTTP cacheadas |
| WEB-38 | CSP | HTML inyectado en respuesta/historial | Se muestra como texto, no ejecuta scripts |
| MOB-01 | Android | Compilar con JDK17/SDK36 | BUILD SUCCESSFUL |
| MOB-02 | Android | Navegación en 4 pestañas + menú | Acceso a todas las pantallas |
| MOB-03 | Android | GET/POST/PUT/PATCH/DELETE/HEAD/OPTIONS | Respuestas esperadas |
| MOB-04 | Android | SessionMode NONE/MEMORY/PERSISTENT | Semántica según origen |
| MOB-05 | Android | RequestParameterizer variables+query | Resolución, límites, encoding |
| MOB-06 | Android | Discovery Planner + Explorer | Límite 24, mismo origen |
| MOB-07 | Android | PayPal sin ID | Botón inactivo |
| MOB-08 | Android | PayPal con ID pública | Browser externo al enlace PayPal |
| MOB-09 | Android | TalkBack / rotación / texto ampliado | Campos accesibles y legibles |

## 4. Verificación de accesibilidad

- Navegación por teclado y foco visible en Web.
- Labels vinculados a inputs; botones con nombre textual; avisos con role=status/alert.
- Áreas táctiles de 44 px Web y 48 dp Android donde corresponda.
- Modo claro y oscuro, contraste de textos críticos.
- Texto al 200 %, orientación horizontal, móvil 320-420 px, desktop 1280-1920 px.
- Preferencias de reducir movimiento en Web.
- Foco accesible tras cambiar de sección y abrir submenú móvil.
- PayPal se abre en un contexto nuevo sólo tras interacción explícita.

## 5. Dispositivos y navegadores

Pruebas sugeridas: Chrome Android, Firefox Android, Chrome/Edge/Firefox desktop y Safari moderno (macOS/iOS) con HTTPS o localhost. No supongas que todas las plataformas permiten mismo acceso a almacenamiento, WebCrypto, headers o cookies. En Android prueba al menos dispositivo API 26 y otro API 35/36 si están disponibles.

## 6. Prueba de seguridad del gateway (local solamente)

Sirve una API propia en localhost:8080 y activa explícitamente:

    REQESTRA_ALLOWED_ORIGINS=http://127.0.0.1:8080 REQESTRA_ALLOW_PRIVATE_TARGETS=1 node server.mjs

Comprueba que URLs fuera de origen se rechazan, que redirects no se siguen, que Host y Content-Length no se aceptan manualmente, que el proceso no escucha públicamente cuando hay allowlist y que no existen logs con tokens. No realices escaneos externos no autorizados.

## 7. Antes de publicar

- [ ] WEB: npm run check y npm test sin errores.
- [ ] MOB: testDebugUnitTest y assembleDebug sin errores.
- [ ] PayPal: cuenta real y beneficiario confirmados; botones desactivados sin ID.
- [ ] Política de Google Play revisada; ninguna contraprestación digital a donantes.
- [ ] Gateway privado no publicado sin autenticación.
- [ ] Sitio web HTTPS con CSP, sin secretos en código cliente.
- [ ] Pruebas manuales de CORS y cookies documentadas.
- [ ] Documentación y licencia revisadas.
- [ ] Capturas y datos ficticios; no introducir credenciales reales de prueba en repositorio.
- [ ] Comprobar seguridad, privacidad y uso autorizado.

## 8. Reportar un fallo

Incluye versión del commit, entorno (Android/Termux/browser), forma de ejecutar, pasos mínimos, comportamiento esperado/real y extracto de error **sin tokens, cookies, claves ni URLs internas sensibles**. Evita publicar respuestas completas de APIs privadas.
