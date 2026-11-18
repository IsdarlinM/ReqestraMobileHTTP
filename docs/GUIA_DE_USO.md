# Guía de uso de Reqestra

Esta guía explica cómo utilizar **Reqestra HTTP Studio** en Android y en la Web.
La aplicación está dirigida a trabajar con solicitudes HTTP(S), colecciones,
variables, pruebas y exploración de servicios **para los que tienes autorización**.

> La interfaz se ofrece en inglés, español, ruso, mandarín simplificado,
> portugués y francés. Esta documentación se mantiene en español.

## 1. Primera solicitud

1. Abre **Estudio / Studio**.
2. Escribe la URL completa de un servicio HTTP(S), por ejemplo
   `https://example.com`.
3. Selecciona el método HTTP (GET, POST, PUT, PATCH, DELETE, etc.).
4. Añade parámetros de consulta, encabezados, cookies o cuerpo si corresponde.
5. Pulsa **Enviar / Send** y espera al resultado.
6. Abre **Inspector / Response** para revisar estado, encabezados y contenido.
   **Vista previa / Preview** muestra el cuerpo en un entorno limitado.

**Precaución:** los métodos POST, PUT, PATCH y DELETE pueden cambiar o eliminar
datos del servidor. No los uses sin comprender sus consecuencias.

## 2. Colecciones, entornos y variables

En **Colecciones / Collections** puedes guardar solicitudes y organizar
entornos. En un valor se pueden usar referencias como `{{baseUrl}}`.
Define previamente estas variables en el entorno apropiado y evita guardar
secretos en texto plano fuera del espacio cifrado.

En Android los datos de sesión pueden mantenerse en memoria o cifrarse,
según el modo seleccionado. En Web, el espacio persistente puede cifrarse
localmente mediante una contraseña; el modo temporal no conserva el
contenido tras recargar.

No existe sincronización automática del espacio de trabajo entre Android
y la Web.

## 3. Pruebas y exploración

**Pruebas / Tests** permite ejecutar secuencialmente solicitudes guardadas
y comprobar estado HTTP y latencia. Las pruebas ejecutan solicitudes reales.

**Explorar / Explore** identifica rutas candidatas y compara respuestas con
límites, confirmación explícita y comprobaciones de origen. Las diferencias
son indicios que requieren revisión manual: no prueban por sí solas la
existencia de una vulnerabilidad.

## 4. Restricciones de Web y Android

| Característica | Android | Web |
| --- | --- | --- |
| Solicitudes HTTP(S) | Cliente nativo | Cliente de navegador, sujeto a CORS |
| Encabezados restringidos | Controlados por el motor de red | Algunos no pueden configurarse desde JavaScript |
| Cookies | Modos de sesión locales | El navegador puede gestionar cookies HttpOnly |
| Guardado de trabajo | Local, según la función | Temporal o cifrado local, según el modo |
| Complementos externos | No se ejecutan paquetes remotos | No se ejecuta código remoto de plugins |

Para utilizar el **gateway local** en Web, configura
`REQESTRA_ALLOWED_ORIGINS` en el servidor. Consulta
[la configuración de Web](WEB_SETUP.md). No se recomienda desactivar
protecciones del navegador para evitar CORS.

## 5. Solución de problemas

**«URL no válida»**

Verifica que la dirección empieza por `http://` o `https://`
y que no contiene credenciales incrustadas.

**El navegador informa de CORS**

El servidor de destino debe permitir solicitudes del origen de tu sitio.
El gateway opcional requiere la lista explícita de orígenes autorizados.
Android no usa la misma política CORS del navegador.

**La solicitud agota el tiempo de espera**

Comprueba conectividad, DNS, certificado TLS y la duración configurada.
No desactives TLS por defecto.

**No se sustituyen las variables**

Comprueba el nombre exacto de la variable, el entorno activo y
las referencias `{{nombre}}`.

**No aparecen mis solicitudes guardadas**

En Web, comprueba si utilizaste modo temporal o el espacio cifrado.
Sin la contraseña original no es posible recuperar los datos cifrados.
Revisa también que estás utilizando el mismo navegador y origen.

**El botón de donación está deshabilitado**

El administrador debe configurar un identificador público válido del
botón de donación de PayPal. Las donaciones son voluntarias y nunca
desbloquean funcionalidades.

## 6. Idiomas, apariencia y menú

- Utiliza 🌐 para elegir el idioma. El elegido queda resaltado y se conserva
  en la configuración local.
- Cambia entre tema claro y oscuro desde el control de apariencia.
- En Android, pulsa **Más / More** en la barra inferior para abrir Historial,
  Inspector, Vista previa, Sesiones, Extensiones y **Acerca de y ayuda**.
- En Web, **About & Help** está en el menú lateral de escritorio y en
  **More** del menú inferior en pantallas pequeñas.

## 7. Ayuda y código fuente

- [Repositorio de Reqestra](https://github.com/IsdarlinM/ReqestraMobileHTTP)
- [Informar de un error o pedir ayuda](https://github.com/IsdarlinM/ReqestraMobileHTTP/issues)
- [Documentación de instalación web](WEB_SETUP.md)
- [Idiomas e interfaz](INTERNACIONALIZACION_Y_EXTENSIONS.md)

No pegues claves API, contraseñas, cookies de sesión ni datos personales en
reportes públicos. Si necesitas compartir un ejemplo de solicitud, redacta
antes los campos sensibles.
