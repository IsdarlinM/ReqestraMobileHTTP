// Reqestra 3.3 web core — no dependencies, no network side effects.
export const METHODS = ["GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS"];
export const BODY_MODES = ["NONE", "RAW", "JSON", "HTML", "XML", "FORM_URLENCODED", "GRAPHQL", "BASE64_BINARY"];
export const SAFE_PROBE_METHODS = ["GET", "HEAD", "OPTIONS"];
export const MUTATING_PROBE_METHODS = ["POST", "PUT", "PATCH", "DELETE"];
export const MAX_REQUEST_BYTES = 5 * 1024 * 1024;
export const MAX_RESPONSE_BYTES = 4 * 1024 * 1024;
const variableName = /^[A-Za-z][A-Za-z0-9_]{0,63}$/;
const headerToken = /^[!#$%&'*+.^_\u0060|~0-9A-Za-z-]+$/;
const paramName = /^[A-Za-z_][A-Za-z0-9_.-]{0,79}$/;
const forbidden = new Set([
  "connection", "content-length", "cookie", "host", "origin",
  "transfer-encoding", "upgrade", "te", "proxy-connection",
  "proxy-authorization", "accept-encoding", "referer", "user-agent",
]);
export function blankDraft() {
  return {
    url:"https://example.com", queryParamsText:"", method:"GET", customMethod:"PROPFIND",
    headersText:"Accept: */*", cookiesText:"", bodyMode:"NONE",
    contentType:"", body:"", timeoutSeconds:30, followRedirects:false,
    allowCleartext:false, forceHttp1:false, sessionMode:"NONE", sessionHeadersText:"",
  };
}
export function defaultWorkspace() {
  return {requests:[], environments:[{name:"Local",variables:{}}], activeEnvironment:"Local", sessions:{}};
}
export function expand(text, vars={}) {
  if (typeof text !== "string" || text.length > 6*1024*1024) throw Error("Plantilla demasiado larga.");
  const out = text.replace(/\{\{([^{}]+)\}\}/g, (_whole, raw) => {
    const name = raw.trim();
    if (!variableName.test(name)) throw Error("Variable no válida: "+name);
    if (!Object.hasOwn(vars,name)) throw Error("Falta la variable: "+name);
    return String(vars[name]);
  });
  if (out.length > 6*1024*1024) throw Error("Expansión demasiado larga.");
  return out;
}
export function parseQueryRows(text) {
  if (text.length > 12000) throw Error("Query demasiado grande.");
  const lines=text.split(/\r?\n/).map(x=>x.trim()).filter(x=>x && !x.startsWith("#"));
  if(lines.length>40) throw Error("Máximo 40 parámetros Query.");
  return lines.map((line,index)=>{
    const at=line.indexOf("=");
    if(at<1) throw Error("Query línea "+(index+1)+": usa nombre=valor.");
    const name=line.slice(0,at).trim(), value=line.slice(at+1);
    if(!paramName.test(name) || value.length>2048 || /[\x00-\x1F]/.test(value))
      throw Error("Query inválida en línea "+(index+1));
    return [name,value];
  });
}
export function parseHeaders(text, opts={}) {
  if(text.includes("\r")) throw Error("Headers: solo saltos de línea LF.");
  const lines=text.split("\n").map(x=>x.trim()).filter(x=>x && !x.startsWith("#"));
  if(lines.length>100) throw Error("Máximo 100 headers.");
  return lines.map((line,i)=>{
    const j=line.indexOf(":");
    if(j<1) throw Error("Header línea "+(i+1)+": usa Nombre: valor.");
    const name=line.slice(0,j).trim(),value=line.slice(j+1).trim();
    if(!headerToken.test(name)||value.length>16384||/[\x00-\x1F]/.test(value))
      throw Error("Header no válido: "+name);
    if(["connection","content-length","transfer-encoding","upgrade","te","proxy-connection"].includes(name.toLowerCase()))
      throw Error(name+" está administrado por el motor de red.");
    if(forbidden.has(name.toLowerCase()) && !opts.gateway)
      throw Error("El navegador no permite establecer el header "+name+". Usa el gateway local para headers restringidos.");
    return [name,value];
  });
}
export function redactUrl(url) {
  try {
    const parsed=new URL(url);
    parsed.username="";parsed.password="";
    for(const key of [...parsed.searchParams.keys()]) parsed.searchParams.set(key,"REDACTED");
    return parsed.toString();
  } catch {return "[URL no válida]";}
}
export function redactHeaders(headers) {
  const safe=new Set(["accept","accept-language","content-type","user-agent"]);
  return headers.map(([name,value])=>[name,safe.has(name.toLowerCase())?value:"[REDACTED]"]);
}
export function parametrizeDraft(draft,variables={}) {
  const urlString=expand(draft.url,variables);
  const url=new URL(urlString);
  if(!["https:","http:"].includes(url.protocol)||!url.hostname||url.username||url.password||url.hash)
    throw Error("Usa una URL HTTP(S) completa, sin credenciales ni fragmentos.");
  parseQueryRows(expand(draft.queryParamsText||"",variables)).forEach(([n,v])=>url.searchParams.append(n,v));
  return {
    ...draft,url:url.toString(),queryParamsText:"",
    headersText:expand(draft.headersText||"",variables),
    cookiesText:expand(draft.cookiesText||"",variables),
    body:expand(draft.body||"",variables),
    contentType:expand(draft.contentType||"",variables),
  };
}
export function validateDraft(draft,opts={}) {
  const url=new URL(draft.url);
  if(!["https:","http:"].includes(url.protocol)||url.username||url.password||url.hash)
    throw Error("URL HTTP(S) no válida, o contiene credenciales/fragmento.");
  if(url.href.length>8192) throw Error("URL demasiado larga.");
  if(url.protocol==="http:"&&!draft.allowCleartext)
    throw Error("HTTP necesita aprobación explícita de laboratorio.");
  let method=draft.method==="CUSTOM" ? draft.customMethod.toUpperCase().trim() : draft.method;
  if(!/^[A-Z][A-Z0-9-]{0,30}$/.test(method)) throw Error("Método HTTP no válido.");
  const bodyMode=draft.bodyMode||"NONE";
  if(!BODY_MODES.includes(bodyMode)) throw Error("Tipo de cuerpo no válido.");
  if(["GET","HEAD"].includes(method)&&bodyMode!=="NONE") throw Error(method+" no admite body en Reqestra.");
  if(!Number.isInteger(Number(draft.timeoutSeconds)) || Number(draft.timeoutSeconds)<1 || Number(draft.timeoutSeconds)>120)
    throw Error("Timeout fuera de rango 1-120.");
  const headers=parseHeaders(draft.headersText||"",opts);
  if(draft.contentType&&bodyMode!=="NONE")headers.push(["Content-Type",draft.contentType]);
  let body=null;
  if(bodyMode!=="NONE"){
    if(bodyMode==="BASE64_BINARY"){
      const normalized=(draft.body||"").replace(/\s/g,"");
      if(!/^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$/.test(normalized))
        throw Error("Base64 no válido.");
      const binary=atob(normalized);
      body=Uint8Array.from(binary,c=>c.charCodeAt(0));
    }else body=draft.body||"";
    const bytes=typeof body==="string"?new TextEncoder().encode(body).length:body.length;
    if(bytes>MAX_REQUEST_BYTES)throw Error("Cuerpo mayor a 5 MiB.");
  }
  if(draft.cookiesText&&!opts.gateway)
    throw Error("El navegador no permite enviar Cookie manual. Usa gateway local o cookies gestionadas por el navegador.");
  return {url:url.toString(),method,headers,body};
}
export function statusTone(code) {
  return code>=200&&code<300?"success":code>=300&&code<400?"warning":code>=400?"error":"info";
}
export function evaluate(response, expectedStatus, maxMs) {
  const checks=[
    {label:"Código HTTP "+expectedStatus, passed:response.status===expectedStatus},
    {label:"Latencia ≤ "+maxMs+" ms",passed:response.durationMs<=maxMs},
  ];
  return {passed:checks.every(x=>x.passed),checks};
}
export function parseOpenApi(json) {
  if(json.length>256*1024)throw Error("OpenAPI excede 256 KiB.");
  const spec=JSON.parse(json);
  if(typeof spec.openapi!=="string"||!spec.openapi.startsWith("3."))
    throw Error("Solo OpenAPI 3.x JSON.");
  const server=spec.servers?.[0]?.url;
  if(!server||!/^https?:\/\//.test(server))throw Error("OpenAPI necesita server absoluto HTTP(S).");
  const items=[];
  for(const [path,op] of Object.entries(spec.paths||{})){
    if(!path.startsWith("/")) continue;
    for(const method of METHODS){
      const operation=op[method.toLowerCase()];
      if(!operation)continue;
      const url=new URL(path,server);
      const draft={...blankDraft(),url:url.toString(),method,
        bodyMode:["POST","PUT","PATCH"].includes(method)?"RAW":"NONE"};
      items.push({id:crypto.randomUUID(),name:String(operation.summary||operation.operationId||method+" "+path).slice(0,80),
        collection:"OpenAPI",draft});
      if(items.length>50)throw Error("OpenAPI excede 50 operaciones.");
    }
  }
  if(!items.length)throw Error("No hay operaciones compatibles.");
  return items;
}
export function planDiscovery(baseUrl,routeText,paramText,methods=["GET","HEAD","OPTIONS"],allowMutating=false) {
  const base=new URL(baseUrl);
  if(!["http:","https:"].includes(base.protocol)||base.username||base.password)throw Error("Origen no válido.");
  if(routeText.length>2400||paramText.length>1500)throw Error("Candidatos demasiado grandes.");
  const lines=s=>[...new Set(s.split(/\r?\n/).map(x=>x.trim()).filter(x=>x&&!x.startsWith("#")))];
  const paths=lines(routeText),params=lines(paramText), allowed=new Set([...SAFE_PROBE_METHODS,...(allowMutating?MUTATING_PROBE_METHODS:[])]);
  if(paths.length>8||params.length>8)throw Error("Máximo 8 rutas y 8 parámetros.");
  if(!methods.length||methods.some(m=>!allowed.has(m)))throw Error("Métodos no autorizados.");
  const tests=[{kind:"BASELINE",label:"Referencia",method:"GET",url:base.toString()}];
  for(const path of paths){
    if(!/^\/[A-Za-z0-9._~/-]{0,180}$/.test(path)||path.includes("//")||path.split("/").some(x=>x===".."||x==="."))
      throw Error("Ruta no válida: "+path);
    const target=new URL(base);target.pathname=path;target.search="";
    tests.push({kind:"ROUTE",label:path,method:"GET",url:target.toString()});
  }
  for(const p of params){
    if(!/^[A-Za-z_][A-Za-z0-9_.-]{0,63}$/.test(p))throw Error("Parámetro candidato no válido.");
    const target=new URL(base);target.searchParams.append(p,"reqestra_probe");
    tests.push({kind:"PARAMETER",label:p,method:"GET",url:target.toString()});
  }
  for(const method of methods)tests.push({kind:"METHOD",label:method,method,url:base.toString()});
  // Preserve the initial GET baseline when GET is also selected as a method.
  const seen=new Set();
  const unique=tests.filter(t=>{
    const key=t.method+" "+t.url;
    if(seen.has(key))return false;
    seen.add(key);return true;
  });
  if(unique.length>24)throw Error("Máximo 24 solicitudes.");
  if(unique.some(t=>new URL(t.url).origin!==base.origin))throw Error("No se permite otro origen.");
  return unique;
}
export function passiveRoutes(response,baseUrl) {
  if(!response)return [];
  const raw=[],body=String(response.body||"").slice(0,64000);
  for(const re of [
    /"(?:href|url|path|endpoint)"\s*:\s*"([^"]{1,250})"/gi,
    /href\s*=\s*["']([^"']{1,250})["']/gi,
  ]) for(const m of body.matchAll(re))raw.push(m[1].replaceAll("\\/","/"));
  for(const [name,value] of response.headers||[])if(name.toLowerCase()==="link")
    for(const m of String(value).matchAll(/<([^>]{1,250})>/g))raw.push(m[1]);
  const base=new URL(baseUrl);
  const paths=raw.flatMap(s=>{
    try {const u=new URL(s,base);return u.origin===base.origin&&u.pathname!=="/"?[u.pathname]:[];}catch{return [];}
  });
  return [...new Set(paths)].slice(0,8);
}
export function importCurl(raw) {
  if(raw.length>200000)throw Error("cURL demasiado largo.");
  const tokens=[],re=/(?:[^\s'"\\]+|\\.|'[^']*'|"[^"]*")+/g;
  for(const m of raw.matchAll(re))tokens.push(m[0].replace(/^['"]|['"]$/g,""));
  if(tokens[0]!=="curl"||tokens.length>256)throw Error("Pega un comando cURL simple.");
  let url="",method="GET",body="",bodyMode="NONE",redirect=false,headers=[];
  for(let i=1;i<tokens.length;i++){
    const flag=tokens[i],value=()=>{if(i+1>=tokens.length)throw Error("Falta argumento "+flag);return tokens[++i];};
    if(["-X","--request"].includes(flag))method=value().toUpperCase();
    else if(["-H","--header"].includes(flag))headers.push(value());
    else if(["-d","--data","--data-raw"].includes(flag)){body=value();bodyMode="RAW";if(method==="GET")method="POST";}
    else if(["--url"].includes(flag))url=value();
    else if(flag==="-L"||flag==="--location")redirect=true;
    else if(flag==="-k"||flag==="--insecure")throw Error("No se importa TLS inseguro.");
    else if(flag.startsWith("-"))throw Error("Opción cURL no compatible: "+flag);
    else if(!url)url=flag;
    else throw Error("Argumento cURL no reconocido.");
  }
  if(!url)throw Error("Falta URL.");
  return {...blankDraft(),url,method:METHODS.includes(method)?method:"CUSTOM",
    customMethod:METHODS.includes(method)?"PROPFIND":method,
    headersText:headers.join("\n"),body,bodyMode,followRedirects:redirect};
}
export function exportCurl(draft,variables={}) {
  const expanded=parametrizeDraft(draft,variables),valid=validateDraft(expanded,{gateway:true});
  const quote=x=>"'"+String(x).replaceAll("'","'\"'\"'")+"'";
  const safe=redactUrl(valid.url);
  const parts=["curl","--request",quote(valid.method),"--url",quote(safe)];
  for(const [name,value] of redactHeaders(valid.headers))parts.push("--header",quote(name+": "+value));
  if(valid.body!==null)parts.push("--data-raw",quote("[REDACTED BODY]"));
  return parts.join(" ");
}
