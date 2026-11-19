# Security Policy — Reqestra

Reqestra is a manual HTTP client for development and authorized security research.

## Sessions and secrets

- Session mode is explicit: disabled, in-memory, or encrypted persistent.
- Session headers are scoped to an origin: scheme, host, and port.
- Cookies are selected according to domain, path, expiration, and the `Secure` attribute.
- Cookies received during redirects are processed before the next hop.
- Persistent mode encrypts headers and cookies using AES-GCM.
- The AES key is generated and protected by Android Keystore.
- Backups and device transfer of internal application data are disabled.
- Cookie values are masked in the session manager.
- The inspector redacts `Authorization`, `Proxy-Authorization`, `Cookie`, `Set-Cookie`, API keys, and common tokens.
- History never stores headers, cookies, or bodies.

## Transport

- HTTPS is the default.
- HTTP requires explicit per-request enablement as a laboratory option.
- There is no trust-all mode or certificate-pinning bypass.
- Release builds trust system certificate authorities only.
- Debug builds may additionally trust user-installed CAs for authorized Burp or mitmproxy testing.
- Redirects are disabled by default.
- Timeouts and size limits reduce uncontrolled resource usage.

## Validation

- CR/LF characters are blocked in header and cookie names and values.
- Credentials embedded in URLs are rejected.
- URL fragments (`#...`) are rejected because they are not part of HTTP requests.
- Custom methods must follow HTTP token grammar.
- `Content-Length`, `Transfer-Encoding`, `Connection`, `Proxy-Connection`, `Upgrade`, and `TE` cannot be edited.
- Maximum request body: 5 MiB.
- Maximum response read: 4 MiB.

## HTML Preview

The local preview disables:

- JavaScript.
- Network access.
- File access.
- Content provider access.
- DOM storage.
- WebView database.
- External navigation.

## Purpose and limitations

Reqestra does not control raw TCP bytes, HTTP/2 pseudoheaders, or manual framing; it does not intercept other applications' traffic, evade controls, or automate attacks.
