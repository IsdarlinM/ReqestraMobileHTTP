import {
  parametrizeDraft,validateDraft,redactUrl,redactHeaders,MAX_RESPONSE_BYTES,
} from "./core.mjs";

const sensitiveHeader=/^(authorization|proxy-authorization|cookie|set-cookie|x-api-key|api-key)$/i;
function safeHeaders(headers) {
  return headers.map(([name,value])=>[name,sensitiveHeader.test(name)?"[REDACTED]":value]);
}
function cookies(text) {
  const entries=text.split(/[\n;]/).map(s=>s.trim()).filter(Boolean);
  if(entries.length>100)throw Error("Demasiadas cookies manuales.");
  for(const entry of entries)
    if(!/^[A-Za-z0-9_!#$%&'*+.^~-]+=[^\r\n;]*$/.test(entry))
      throw Error("Cookie manual inválida.");
  return entries.join("; ");
}
function base64(bytes){
  let s="";for(let i=0;i<bytes.length;i+=8192)s+=String.fromCharCode(...bytes.slice(i,i+8192));
  return btoa(s);
}
async function readLimited(response) {
  if(!response.body)return {body:"",truncated:false,size:0};
  const reader=response.body.getReader();
  const chunks=[];let total=0,truncated=false;
  try {
    while(true){
      const {done,value}=await reader.read();if(done)break;
      if(total+value.length>MAX_RESPONSE_BYTES){
        const allowed=MAX_RESPONSE_BYTES-total;
        if(allowed>0)chunks.push(value.slice(0,allowed));
        total+=allowed;truncated=true;await reader.cancel();break;
      }
      chunks.push(value);total+=value.length;
    }
  }finally{reader.releaseLock();}
  const out=new Uint8Array(total);let offset=0;
  for(const chunk of chunks){out.set(chunk,offset);offset+=chunk.length;}
  return {body:new TextDecoder("utf-8",{fatal:false}).decode(out),truncated,size:total};
}
function withAbort(signal,seconds) {
  const controller=new AbortController();
  const abort=()=>controller.abort(signal?.reason||new DOMException("Cancelado","AbortError"));
  signal?.addEventListener("abort",abort,{once:true});
  if(signal?.aborted)abort();
  const timeout=setTimeout(()=>controller.abort(new Error("Timeout de la petición.")),seconds*1000);
  return {signal:controller.signal,close(){clearTimeout(timeout);signal?.removeEventListener("abort",abort);}};
}
export async function execute(draft,{variables={},sessionHeadersText="",mode="NONE",gateway=false,signal}={}) {
  const expanded=parametrizeDraft(draft,variables);
  const opts={gateway};
  const validated=validateDraft(expanded,opts);
  const sessionHeaders=parseSessionHeaders(sessionHeadersText,opts);
  const names=new Set(validated.headers.map(([n])=>n.toLowerCase()));
  for(const [name,value] of sessionHeaders)if(!names.has(name.toLowerCase()))
    validated.headers.push([name,value]);
  if(gateway&&expanded.cookiesText){
    validated.headers=validated.headers.filter(([name])=>name.toLowerCase()!=="cookie");
    validated.headers.push(["Cookie",cookies(expanded.cookiesText)]);
  }
  const timer=withAbort(signal,Number(expanded.timeoutSeconds));
  const started=performance.now();
  try {
    let data;
    if(gateway){
      const res=await fetch("/api/execute",{
        method:"POST",
        credentials:"same-origin",
        headers:{"Content-Type":"application/json"},
        body:JSON.stringify({
          url:validated.url,method:validated.method,headers:validated.headers,
          body:typeof validated.body==="string"?validated.body:null,
          bodyBase64:validated.body instanceof Uint8Array?base64(validated.body):null,
          timeoutSeconds:Number(expanded.timeoutSeconds),
        }),
        cache:"no-store",redirect:"error",signal:timer.signal,
      });
      const obj=await res.json();
      if(!res.ok)throw Error(obj.error||"Gateway: respuesta "+res.status);
      data={
        status:obj.status,statusText:obj.statusText||"",protocol:obj.protocol||"HTTP",
        headers:Array.isArray(obj.headers)?obj.headers:[],
        body:obj.body||"",bodyTruncated:!!obj.truncated,size:obj.size||0,
      };
    }else{
      const response=await fetch(validated.url,{
        method:validated.method,headers:new Headers(validated.headers),
        body:validated.body,redirect:expanded.followRedirects?"follow":"manual",
        credentials:mode==="NONE"?"omit":"include",cache:"no-store",signal:timer.signal,
      });
      if(response.type==="opaqueredirect")throw Error("El navegador ha bloqueado los detalles del redirect (opaqueredirect).");
      if(response.type==="opaque")throw Error("Respuesta opaca por restricciones CORS.");
      const body=await readLimited(response);
      data={
        status:response.status,statusText:response.statusText,
        protocol:"Fetch",headers:[...response.headers.entries()],
        body:body.body,bodyTruncated:body.truncated,size:body.size,
      };
    }
    return {
      ...data,durationMs:Math.round(performance.now()-started),
      receivedAt:new Date().toISOString(),
      request:{
        url:redactUrl(validated.url),method:validated.method,
        headers:redactHeaders(validated.headers),
        body:validated.body===null?"":"[contenido redactado]",
        bodyMode:draft.bodyMode,
      },
      headers:safeHeaders(data.headers),
    };
  }catch(error){
    if(error.name==="AbortError")throw Error("Petición cancelada o tiempo agotado.");
    if(error instanceof TypeError && !gateway)
      throw Error("La solicitud no pudo completarse: comprueba CORS, TLS, conectividad y los headers permitidos por el navegador.");
    throw error;
  }finally{timer.close();}
}
function parseSessionHeaders(text,opts) {
  // Session headers must follow exactly the same validation as request headers.
  if(!text)return [];
  const headerName=/^[!#$%&'*+.^_\u0060|~0-9A-Za-z-]+$/;
  const items=text.split("\n").filter(x=>x.trim());
  if(items.length>100)throw Error("Máximo 100 headers de sesión.");
  return items.map(line=>{
    const i=line.indexOf(":");if(i<=0)throw Error("Header de sesión: Nombre: valor.");
    const name=line.slice(0,i).trim(),value=line.slice(i+1).trim();
    if(!headerName.test(name)||/[\r\n]/.test(value)||value.length>16384)throw Error("Header de sesión inválido.");
    if(!opts.gateway&&["cookie","host","origin","content-length","connection","referer","accept-encoding","user-agent"].includes(name.toLowerCase()))
      throw Error("El navegador no permite el header de sesión "+name);
    return [name,value];
  });
}
