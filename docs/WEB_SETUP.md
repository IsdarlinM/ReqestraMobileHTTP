# Reqestra Web 3.3 — Installation, Operation, and Deployment

## Requirements

- A modern browser with Fetch, ES modules, WebCrypto, and JavaScript enabled.
- For local hosting: Node.js 20 or later, without third-party NPM packages.
- For encrypted persistent storage: localhost or an HTTPS site (secure context).
- For publication: static HTTPS hosting. The gateway is not available on static hosting.

## 1. Start on PC/Linux/macOS

    git clone https://github.com/IsdarlinM/ReqestraMobileHTTP.git
    cd ReqestraMobileHTTP/web
    node --version
    npm test
    npm start

Open **http://127.0.0.1:5173**.

For another port:

    PORT=4173 node server.mjs

You can also serve the web client as a purely static site, without the gateway:

    cd web
    python3 -m http.server 5173 --bind 127.0.0.1

Open http://127.0.0.1:5173. The direct web transport requires the destination API to allow CORS requests from this origin. The page does not send API requests until you select a Send or Explore action.

## 2. Run on Android/Termux

From your cloned repository:

    pkg install nodejs
    cd ~/ReqestraMobileHTTP
    git pull --ff-only origin main
    cd web
    node server.mjs

Open http://127.0.0.1:5173 in a browser on the same device. Compatible browsers support WebCrypto on localhost. For the native Android app, build with the Gradle wrapper and a configured Android SDK.

## 3. Browser mode

This is the default mode and requires no Reqestra API server. Requests are sent directly through Fetch. Mandatory limitations include:

- The target server's CORS policy.
- Browser-controlled headers such as Cookie, Host, Content-Length, Connection, and Origin.
- TLS certificates must be valid for the browser.
- HttpOnly cookies and some response headers are unavailable to JavaScript.
- HTTPS pages may block HTTP requests as mixed content.
- Some manually intercepted redirect results appear as `opaqueredirect`.
- Forcing HTTP/1.1 is not available.
- Browsers may impose additional privacy, CSP, and networking policies.

If you own the API, explicitly allow the Reqestra Web origin and the required headers/methods in its CORS configuration. Do not combine wildcard origins with credentials or disable browser CORS security as a shortcut.

## 4. Optional private gateway

The gateway in `web/server.mjs` serves the interface and relays browser HTTP requests to destinations that you explicitly configure in advance.

**Run the gateway only on loopback, NOT on the public Internet.** It has no login, roles, multiuser authorization, or authentication system for remote exposure. When allowed origins are configured, the server refuses to bind outside localhost.

Example for your development API:

    cd web
    REQESTRA_ALLOWED_ORIGINS=https://api.example.com node server.mjs

Multiple exact origins (including scheme and port):

    REQESTRA_ALLOWED_ORIGINS=https://api1.example.com,https://api2.example.com:8443 node server.mjs

Open http://127.0.0.1:5173 and choose **Network Engine → Authorized Local Gateway** in Studio.

A private lab using a loopback or internal IP requires a second explicit opt-in:

    REQESTRA_ALLOWED_ORIGINS=http://127.0.0.1:8080 REQESTRA_ALLOW_PRIVATE_TARGETS=1 node server.mjs

Only use this in your own lab. Setting the option to 1 disables private-address blocking, including for hostnames that resolve to private addresses.

### Gateway restrictions

- Requires an allowlist of **exact origins** (scheme, host, and port).
- Resolves DNS and pins the validated IP during connection establishment to limit DNS rebinding.
- Blocks loopback, private, link-local, and special-purpose ranges by default.
- Does not follow HTTP redirects.
- Supports only GET, HEAD, OPTIONS, POST, PUT, PATCH, and DELETE.
- Does not permit overriding Host or framing headers.
- Request-body limit: 5 MiB; response-read limit: 4 MiB; timeout: 1–120 seconds.
- At most 60 requests per minute per client IP and 2 simultaneous requests.
- Does not persist tokens, cookies, request bodies, or results to disk.
- The operator must configure every authorized origin; never deploy a generic relay that accepts any host.
- Confidential data stays in process memory while a request is handled. Remote access would require additional TLS, authentication, auditing, and authorization controls.

The gateway works around some browser restrictions for testing your own services. It does not completely reproduce OkHttp sessions and is not a multiuser service.

## 5. Encrypted web storage

In Collections, select **Create Encrypted Vault** and enter a long password. The key is derived with PBKDF2-SHA256 (260,000 iterations), and draft requests, environments, collections, persistent headers, and redacted history are encrypted with WebCrypto AES-256-GCM. The password is not sent to a server or stored in the encrypted file.

- Without a vault, work is temporary and held in memory.
- After reloading, select **Unlock** and enter the same password.
- You can download or import an encrypted `.enc.json` backup.
- Importing replaces the current browser workspace; back up your existing data first.
- Lost passwords or damaged encrypted data cannot be recovered.
- Android Keystore-encrypted data cannot be imported automatically.
- Trust the browser, its extensions, and the device.

## 6. PayPal donations on the web

The project owner creates a hosted donation button through PayPal:

https://www.paypal.com/donate/buttons

Select the correct receiving account, complete the configuration, and find the public **hosted_button_id**. Do not invent an ID or include a secret email address or PayPal credentials.

Edit `web/config.js` and set only the public ID:

    window.REQESTRA_CONFIG = Object.freeze({
      paypalHostedButtonId: "YOUR_PUBLIC_ID",
    });

Redistribute `web/config.js`. The **Donate / Support** section then enables an external link to:

    https://www.paypal.com/donate/?hosted_button_id=YOUR_PUBLIC_ID

If the ID is missing or fails alphanumeric validation, the action stays disabled. PayPal, not the Reqestra frontend, processes the payment. See `docs/PAYPAL_DONATIONS.md` for distribution requirements.

## 7. Publish as an HTTPS site

The contents of **web/** can be deployed on GitHub Pages, Netlify, Cloudflare Pages, or another static host, with `web/` as the publication root. Adjust paths for the provider; resources are referenced relative to that folder. Use HTTPS for encrypted storage.

Static hosting supports browser mode only. Do not put credentials, PayPal secret keys, or tokens in `config.js` or the repository. The public hosted-button ID may be visible. There is no payment API, remote database, or webhook on the site.

**Important:** GitHub Pages does not run `web/server.mjs`. A public site cannot use the private gateway unless it is deployed separately behind an authorization architecture designed and audited for that purpose (not included).

Compatible browsers can install the PWA. Its service worker caches the app shell only; it does not intercept HTTP test requests or store API responses.

## 8. Verify operation

    cd web
    npm run check
    npm test
    npm start

Manually inspect Studio, Query, missing variables, URL validation, cURL import, OpenAPI, collections, origin-scoped sessions, history, tests, discovery, cancellation, preview, and configured donations. Node tests do not replace browser tests for CORS, service workers, or accessibility.

## 9. Troubleshooting

- Fetch/CORS error: check your API's Access-Control-Allow-Origin. The error does not necessarily mean the API is offline.
- TLS error: the browser rejected the certificate; no insecure mode is implemented.
- Cannot write a manual Cookie header: use the authorized local gateway or browser-managed cookies.
- Gateway disabled: set REQESTRA_ALLOWED_ORIGINS, restart the server, and select Gateway again.
- Origin not permitted: add the exact scheme/host/port to the allowlist; do not allow every destination.
- WebCrypto unavailable: use HTTPS or localhost.
- Cannot unlock: verify the password and backup; recovery is not supported.
- PayPal disabled: configure the public hosted_button_id and republish web/config.js.
- PWA shows an old version: publish every file and reload while online.
