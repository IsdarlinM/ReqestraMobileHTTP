// Cache the app shell only; never cache HTTP requests, responses, gateway data or credentials.
const NAME="reqestra-shell-3.5-i18n";
const SHELL=["./","./index.html","./styles.css","./config.js","./app.mjs",
  "./lib/core.mjs","./lib/ui.mjs","./lib/vault.mjs","./lib/transport.mjs",
  "./icon.png","./lib/i18n.mjs","./manifest.webmanifest"];
self.addEventListener("install",event=>{
  event.waitUntil(caches.open(NAME).then(cache=>cache.addAll(SHELL)).then(()=>self.skipWaiting()));
});
self.addEventListener("activate",event=>{
  event.waitUntil(caches.keys().then(keys=>Promise.all(
    keys.filter(k=>k.startsWith("reqestra-shell-")&&k!==NAME).map(k=>caches.delete(k))
  )).then(()=>self.clients.claim()));
});
self.addEventListener("fetch",event=>{
  const request=event.request;
  if(request.method!=="GET")return;
  const url=new URL(request.url);
  if(url.origin!==self.location.origin||url.pathname.startsWith("/api/"))return;
  if(!SHELL.some(p=>{
    const target=new URL(p,self.registration.scope);
    return target.pathname===url.pathname;
  }))return;
  event.respondWith(fetch(request).then(response=>{
    if(response.ok){const copy=response.clone();caches.open(NAME).then(cache=>cache.put(request,copy));}
    return response;
  }).catch(()=>caches.match(request)));
});
