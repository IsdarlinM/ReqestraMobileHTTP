# Reqestra User Guide

This guide explains how to use **Reqestra HTTP Studio** on Android and the web.
The application is intended for HTTP(S) requests, collections, variables, tests, and service discovery **on systems you are authorized to access**.

> The interface is available in English, Spanish, Russian, Simplified Mandarin Chinese, Portuguese, and French. This documentation is maintained in English.

## 1. Your first request

1. Open **Studio**.
2. Enter the complete HTTP(S) URL of a service, for example `https://example.com`.
3. Select the HTTP method (GET, POST, PUT, PATCH, DELETE, etc.).
4. Add query parameters, headers, cookies, or a body as needed.
5. Select **Send** and review the result.
6. Open **Inspector / Response** to inspect status, headers, and content. **Preview** displays the body in a restricted environment.

**Caution:** POST, PUT, PATCH, and DELETE may change or delete server data. Do not use them without understanding their effects.

## 2. Collections, environments, and variables

Under **Collections**, save requests and manage environments. Values can reference variables such as `{{baseUrl}}`. Define these variables in the relevant environment first, and avoid storing secrets as plaintext outside the encrypted workspace.

On Android, session data can be held in memory or encrypted, depending on the selected mode. On the web, a persistent workspace can be encrypted locally with a password; temporary mode does not retain content after a reload.

There is no automatic workspace synchronization between Android and the web.

## 3. Tests and discovery

**Tests** runs saved requests sequentially and checks HTTP status and latency. These tests issue real network requests.

**Explore** identifies candidate paths and compares responses with limits, explicit confirmation, and origin checks. Differences are clues requiring manual review; they do not by themselves establish a vulnerability.

## 4. Web and Android constraints

| Feature | Android | Web |
| --- | --- | --- |
| HTTP(S) requests | Native client | Browser client subject to CORS |
| Restricted headers | Controlled by the networking engine | Some cannot be set from JavaScript |
| Cookies | Local session modes | Browser may manage HttpOnly cookies |
| Saving work | Local, according to feature | Temporary or locally encrypted, depending on mode |
| External add-ons | Remote packages are not executed | Remote plugin code is not executed |

To use the **local gateway** on the web, configure `REQESTRA_ALLOWED_ORIGINS` on the server; see [Web setup](WEB_SETUP.md). Disabling browser security to bypass CORS is not recommended.

## 5. Troubleshooting

**"Invalid URL"**

Make sure the address begins with `http://` or `https://` and contains no embedded credentials.

**Browser reports a CORS error**

The destination server must allow requests from your site's origin. The optional gateway requires an explicit allowlist of authorized origins. Android is not subject to the same browser CORS policy.

**Request times out**

Check connectivity, DNS, the TLS certificate, and the configured timeout. Do not disable TLS by default.

**Variables are not substituted**

Check the exact variable name, active environment, and references such as `{{name}}`.

**Saved requests do not appear**

On the web, check whether you used temporary mode or the encrypted workspace. Encrypted data cannot be recovered without the original password. Also verify that you are using the same browser and origin.

**Donation button is disabled**

The maintainer must configure a valid public PayPal donation-button identifier. Donations are voluntary and never unlock features.

## 6. Languages, appearance, and navigation

- Use 🌐 to select a language. The selected option is highlighted and saved locally.
- Use the appearance control to switch between light and dark themes.
- On Android, select **More** in the bottom bar to access History, Inspector, Preview, Sessions, Extensions, and **About & Help**.
- On the web, **About & Help** appears in the desktop sidebar and under **More** on smaller screens.

## 7. Help and source code

- [Reqestra repository](https://github.com/IsdarlinM/ReqestraMobileHTTP)
- [Report an issue or request help](https://github.com/IsdarlinM/ReqestraMobileHTTP/issues)
- [Web installation documentation](WEB_SETUP.md)
- [Languages and interface](INTERNACIONALIZACION_Y_EXTENSIONS.md)

Never paste API keys, passwords, session cookies, or personal data into public issue reports. Redact sensitive fields before sharing sample requests.
