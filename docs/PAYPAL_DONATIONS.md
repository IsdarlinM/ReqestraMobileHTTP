# Donaciones voluntarias en Reqestra — PayPal

**Versión:** Reqestra 3.3 (Android y Web). La app **no procesa pagos ni verifica transacciones**: abre una página de donación alojada directamente en paypal.com.

## Configuración del botón aportado por el propietario

El propietario facilitó el enlace de administración `https://www.paypal.com/donate/buttons/manage/DZW75X5SML2RJ`.
El segmento final `DZW75X5SML2RJ` se ha configurado como candidato a `hosted_button_id`
en el cliente Web y como valor público predeterminado para Android, de acuerdo con
el formato de botones alojados por PayPal. Una propiedad Gradle local
`paypalHostedButtonId` no vacía prevalece sobre ese valor predeterminado.

**Verificación pendiente:** el enlace administrativo NO es para los donantes y no prueba
que el identificador de administración sea igual a la ID pública del botón. El
propietario debe abrir `https://www.paypal.com/donate/?hosted_button_id=DZW75X5SML2RJ`
en sesión privada, confirmar que aparece una página activa de donaciones de Reqestra
y comprobar que el beneficiario es su cuenta antes de publicar o distribuir.

Si PayPal muestra una ID distinta dentro del código generado de **Compartir / Integrar**,
hay que reemplazar el valor en `web/config.js` y el predeterminado de
`app/build.gradle.kts`, o ejecutar `scripts/configure-paypal.sh` para
actualizar localmente Android y Web antes de compilar.

## 1. Qué está integrado

- Android: opción **Apoyar proyecto · PayPal** en el menú de herramientas; pantalla explicativa y botón externo.
- Web: sección **Donar / Ayuda** con botón externo.
- Ambos validan un identificador **público** alfanumérico de botón alojado y generan una URL fija al host www.paypal.com.
- Si falta la ID, la acción de pago se mantiene desactivada y muestra una explicación.
- Las donaciones no desbloquean funcionalidades, cuentas, insignias, suscripciones, servicios ni contenido.
- No existe infraestructura en Reqestra para capturar números de tarjeta, contraseñas PayPal, correos de donantes ni justificantes de pago.

## 2. Crear el botón con tu propia cuenta

1. Entra en https://www.paypal.com/donate/buttons con la cuenta PayPal receptora.
2. Crea una página o botón de donaciones, si tu cuenta/país son aptos. Las opciones, comisiones y elegibilidad varían según región y categoría de cuenta.
3. Comprueba cuidadosamente el nombre, moneda, descripción y cuenta beneficiaria en la interfaz de PayPal.
4. Copia el valor **hosted_button_id** del enlace generado. Es una ID pública de enlace, no una llave privada.
5. Prueba manualmente que PayPal muestra la cuenta correcta antes de activar el botón en Reqestra.

Enlace oficial: https://developer.paypal.com/sdk/donate/

Si PayPal genera otro tipo de enlace incompatible, no lo fuerces: adapta la integración tras revisar el enlace oficial y validarlo. Esta versión admite únicamente la modalidad hospedada donate/?hosted_button_id.

## Configuración conjunta desde Termux

Tras crear tu botón de donación en PayPal, configura Android y Web con **un solo comando**:

    cd ~/ReqestraMobileHTTP
    git pull --ff-only origin main
    bash scripts/configure-paypal.sh

El asistente te pedirá únicamente el valor **público** `hosted_button_id` de tu propio botón, comprobará su formato y actualizará `~/.gradle/gradle.properties` y `web/config.js`. No solicita contraseña, API key ni secretos.

Comprueba que el enlace generado abre la página **de tu cuenta receptora** en paypal.com. Después puedes subir solo `web/config.js` a GitHub si deseas que el sitio web público ofrezca donaciones. Este script no publica un sitio ni compila la APK por sí solo.

## 3. Configurar el proyecto Android

En la máquina de compilación o Termux, abre el archivo local de propiedades Gradle del usuario:

    mkdir -p ~/.gradle
    nano ~/.gradle/gradle.properties

Añade (usa tu ID real, NO el texto de ejemplo):

    paypalHostedButtonId=TU_ID_PUBLICA

Luego compila:

    cd ~/ReqestraMobileHTTP
    ./gradlew --no-daemon testDebugUnitTest assembleDebug

Android genera el enlace usando BuildConfig.PAYPAL_HOSTED_BUTTON_ID. No necesita PayPal SDK, client secret ni permisos Android adicionales, porque el pago se abre en el navegador del sistema.

**Advertencia:** BuildConfig es parte del APK distribuido y puede leerse públicamente. Debe contener solamente la ID pública; **nunca** claves privadas, tokens OAuth o API credentials. No se recomienda incluir la ID en un archivo fuente versionado si el propietario no desea mostrarla en GitHub.

Si no se configura, se compila con el botón desactivado.

## 4. Configurar Web

Edita web/config.js:

    window.REQESTRA_CONFIG = Object.freeze({
      paypalHostedButtonId: "TU_ID_PUBLICA",
    });

Publica de nuevo web/config.js. La ID se entrega a cada navegador, ya que es pública. El botón abre una nueva pestaña al dominio oficial PayPal con rel=noopener noreferrer.

Si está sin configurar, no se abre un checkout genérico ni se solicita información financiera en Reqestra.

## 5. Experiencia para el donante

1. Abre la sección de donaciones desde la app o la web.
2. Lee el aviso de voluntariedad.
3. Si hay ID configurada, pulsa **Donar con PayPal**.
4. Verifica el dominio oficial paypal.com, el beneficiario y las condiciones.
5. Completa o cancela el aporte en PayPal. Reqestra no conoce ni guarda su resultado.

No mostrar nunca «Pago completado» únicamente porque se haya pulsado el botón. Para validar transacciones y emitir recibos haría falta una integración oficial adicional en un servidor seguro, que esta versión **no** incluye.

## 6. Política de tiendas y consideraciones legales

Las contribuciones a un creador sin contraprestaciones digitales pueden quedar fuera de Google Play Billing si se cumplen las condiciones publicadas por Google, pero Google Play puede evaluar la implementación concreta y sus normas pueden cambiar. Verifica las reglas actuales antes de distribuir la aplicación:

- https://support.google.com/googleplay/android-developer/answer/10281818
- https://support.google.com/googleplay/android-developer/answer/9858738

**No ofrezcas beneficios dentro de la app a cambio de donar** (por ejemplo, funcionalidades, badges o suscripciones); ello podría cambiar el tratamiento de la operación y requerir facturación de la tienda.

No llames «donación benéfica» ni prometas deducciones fiscales si no existe acreditación aplicable. PayPal determina sus propias condiciones de elegibilidad para donaciones y el receptor debe verificar obligaciones fiscales, reglas locales y política de la tienda aplicable. Este documento es una guía técnica, no asesoría tributaria o legal.

## 7. Seguridad y pruebas

- [ ] URL comienza exactamente por https://www.paypal.com/donate/.
- [ ] La ID contiene solo letras o números, 6 a 64 caracteres.
- [ ] No se puede introducir una URL externa arbitraria desde la interfaz.
- [ ] Sin ID, el botón queda desactivado en Android y Web.
- [ ] El botón no solicita credenciales o tarjetas en Reqestra.
- [ ] La web usa target=_blank con rel=noopener noreferrer.
- [ ] El modo sin donaciones conserva todas las características.
- [ ] La cuenta receptora se comprueba visualmente en PayPal antes de distribuir.
- [ ] Se verifican las políticas actuales de Google Play y PayPal para el mercado objetivo.

## 8. Enlaces de referencia

- PayPal, creación de botones: https://www.paypal.com/donate/buttons
- PayPal Donate SDK: https://developer.paypal.com/sdk/donate/
- Google Play, aclaraciones sobre contribuciones: https://support.google.com/googleplay/android-developer/answer/10281818

No subas credenciales PayPal ni intentes integrarlas con una URL de botón alojado.
