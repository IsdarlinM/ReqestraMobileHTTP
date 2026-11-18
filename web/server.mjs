// Optional private gateway for cross-origin requests. Not designed as a public proxy.
// Run: REQESTRA_ALLOWED_ORIGINS=https://api.example.com node web/server.mjs
import http from "node:http";
import https from "node:https";
import dns from "node:dns/promises";
import net from "node:net";
import {readFile} from "node:fs/promises";
import path from "node:path";
import {fileURLToPath} from "node:url";
import {MAX_RESPONSE_BYTES,MAX_REQUEST_BYTES} from "./lib/core.mjs";

const directory=path.dirname(fileURLToPath(import.meta.url));
const BIND=process.env.REQESTRA_BIND_HOST||"127.0.0.1";
const PORT=Number(process.env.PORT||5173);
const ALLOWED=new Set((process.env.REQESTRA_ALLOWED_ORIGINS||"").split(",").map(s=>s.trim())
  .filter(Boolean).map(s=>new URL(s).origin));
const PRIVATE=process.env.REQESTRA_ALLOW_PRIVATE_TARGETS==="1";
if(ALLOWED.size && !["127.0.0.1","::1","localhost"].includes(BIND)) {
  throw Error("El gateway no puede exponerse fuera del loopback sin controles de autenticación. Usa 127.0.0.1.");
}
const MIME={".html":"text/html; charset=utf-8",".css":"text/css; charset=utf-8",
  ".js":"text/javascript; charset=utf-8",".mjs":"text/javascript; charset=utf-8",
  ".png":"image/png",
  ".json":"application/json; charset=utf-8",".webmanifest":"application/manifest+json",".svg":"image/svg+xml"};
const rate=new Map();let active=0;
function json(res,status,object){
  res.writeHead(status,{"Content-Type":"application/json; charset=utf-8","Cache-Control":"no-store"});
  res.end(JSON.stringify(object));
}
function deniedAddress(ip){
  if(PRIVATE)return false;
  const addr=String(ip).toLowerCase();
  if(net.isIP(addr)===6){
    if(addr==="::1"||addr==="::"||addr.startsWith("fe80:")||addr.startsWith("fc")||
       addr.startsWith("fd")||addr.startsWith("ff")||addr.startsWith("::ffff:"))return true;
    return false;
  }
  if(net.isIP(addr)!==4)return true;
  const p=addr.split(".").map(Number);
  return p[0]===0||p[0]===10||p[0]===127||p[0]>=224||
    p[0]===169&&p[1]===254||p[0]===172&&p[1]>=16&&p[1]<=31||
    p[0]===192&&p[1]===168||p[0]===100&&p[1]>=64&&p[1]<=127||
    p[0]===192&&p[1]===0&&p[2]===0||p[0]===198&&p[1]===18||
    p[0]===198&&p[1]===19||p[0]===192&&p[1]===0&&p[2]===2||
    p[0]===198&&p[1]===51&&p[2]===100||
    p[0]===203&&p[1]===0&&p[2]===113;
}
export async function resolveTarget(raw){
  const url=new URL(raw);
  if(!["http:","https:"].includes(url.protocol)||url.username||url.password||url.hash)
    throw Error("Destino HTTP(S) inválido.");
  if(!ALLOWED.has(url.origin))throw Error("Origen no permitido por REQESTRA_ALLOWED_ORIGINS.");
  if(url.href.length>8192)throw Error("URL demasiado larga.");
  const entries=await dns.lookup(url.hostname,{all:true,verbatim:true});
  const addresses=entries.filter(x=>net.isIP(x.address)!==0&&!deniedAddress(x.address));
  if(!addresses.length)throw Error("El destino resuelve a direcciones privadas o bloqueadas.");
  return {url,address:addresses[0]};
}
async function readIncoming(req,limit=7*1024*1024){
  const parts=[];let length=0;
  for await(const chunk of req){
    length+=chunk.length;if(length>limit)throw Error("Solicitud demasiado grande.");
    parts.push(chunk);
  }
  return JSON.parse(Buffer.concat(parts).toString("utf8"));
}
function normalizedHeaders(items){
  if(!Array.isArray(items)||items.length>100)throw Error("Headers inválidos.");
  const headers={};
  for(const pair of items){
    if(!Array.isArray(pair)||pair.length!==2)throw Error("Header inválido.");
    const [name,value]=pair;
    if(typeof name!=="string"||typeof value!=="string"||
      !/^[!#$%&'*+.^_\u0060|~0-9A-Za-z-]+$/.test(name)||
      /[\r\n]/.test(value)||value.length>16384)throw Error("Header inválido.");
    if(["connection","content-length","transfer-encoding","proxy-connection","upgrade","te","host"].includes(name.toLowerCase()))
      throw Error("Header reservado: "+name);
    headers[name]=value;
  }
  return headers;
}
async function execute(payload){
  const {url,address}=await resolveTarget(payload.url);
  const method=String(payload.method||"").toUpperCase();
  if(!/^(GET|HEAD|OPTIONS|POST|PUT|PATCH|DELETE)$/.test(method))
    throw Error("Método no permitido en gateway.");
  const headers=normalizedHeaders(payload.headers||[]);
  let body=null;
  if(typeof payload.bodyBase64==="string"){
    if(payload.bodyBase64.length>7*1024*1024||!/^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$/.test(payload.bodyBase64))
      throw Error("Base64 no válido.");
    body=Buffer.from(payload.bodyBase64,"base64");
  }else if(typeof payload.body==="string")body=Buffer.from(payload.body,"utf8");
  if(body && body.length>MAX_REQUEST_BYTES)throw Error("Body supera 5 MiB.");
  if(body&&["GET","HEAD"].includes(method))throw Error("Método no admite body.");
  const timeout=Math.min(120,Math.max(1,Number(payload.timeoutSeconds)||30))*1000;
  return await new Promise((resolve,reject)=>{
    const agent=url.protocol==="https:"?https:http;
    const request=agent.request(url,{
      method,headers,timeout,agent:false,
      // Pin the address resolved and checked above to prevent DNS rebinding.
      lookup:(_hostname,_opts,callback)=>callback(null,address.address,address.family),
    },async response=>{
      const chunks=[];let size=0,truncated=false;
      try{
        for await(const part of response){
          if(size+part.length>MAX_RESPONSE_BYTES){
            const remaining=MAX_RESPONSE_BYTES-size;
            if(remaining>0)chunks.push(part.subarray(0,remaining));
            size+=remaining;truncated=true;response.destroy();break;
          }
          chunks.push(part);size+=part.length;
        }
        resolve({
          status:response.statusCode,statusText:response.statusMessage,protocol:"HTTP",
          headers:Object.entries(response.headers).flatMap(([k,v])=>
            Array.isArray(v)?v.map(x=>[k,String(x)]):v===undefined?[]:[[k,String(v)]]),
          body:Buffer.concat(chunks).toString("utf8"),size,truncated,
        });
      }catch(e){if(truncated)resolve({status:response.statusCode,statusText:response.statusMessage,
        protocol:"HTTP",headers:[],body:Buffer.concat(chunks).toString("utf8"),size,truncated});
        else reject(e);}
    });
    request.on("timeout",()=>request.destroy(new Error("Timeout del gateway.")));
    request.on("error",reject);
    if(body)request.write(body);
    request.end();
  });
}
function rateLimit(ip){
  const now=Date.now(),item=rate.get(ip)||{count:0,until:now+60000};
  if(now>item.until){item.count=0;item.until=now+60000;}
  item.count++;rate.set(ip,item);return item.count<=60;
}
async function serve(req,res){
  // Prevent DNS rebinding through attacker-controlled Host headers on loopback gateway.
  if(ALLOWED.size&&!/^(?:localhost|127\.0\.0\.1|\[::1\])(?::[0-9]{1,5})?$/.test(req.headers.host||"")) {
    return json(res,403,{error:"Host de interfaz no permitido."});
  }
  res.setHeader("X-Content-Type-Options","nosniff");
  res.setHeader("Referrer-Policy","no-referrer");
  res.setHeader("X-Frame-Options","DENY");
  res.setHeader("Content-Security-Policy",
    "default-src 'none'; script-src 'self'; style-src 'self'; img-src 'self' data:; frame-src 'self' about:; connect-src 'self' https: http:; base-uri 'none'; form-action 'none'; frame-ancestors 'none'");
  const url=new URL(req.url,"http://localhost");
  if(req.headers.origin && req.headers.origin!=="http://"+req.headers.host &&
    req.headers.origin!=="https://"+req.headers.host){
    return json(res,403,{error:"Origen de interfaz no permitido."});
  }
  if(url.pathname==="/api/health" && req.method==="GET")
    return json(res,200,{gateway:ALLOWED.size>0,mode:"private"});
  if(url.pathname==="/api/execute" && req.method==="POST"){
    if(!ALLOWED.size)return json(res,403,{error:"Configura REQESTRA_ALLOWED_ORIGINS antes de usar el gateway."});
    if(!rateLimit(req.socket.remoteAddress||"unknown"))return json(res,429,{error:"Límite de 60 solicitudes/minuto."});
    if(active>=2)return json(res,429,{error:"Gateway ocupado; máximo dos solicitudes concurrentes."});
    active++;
    try{return json(res,200,await execute(await readIncoming(req)));}
    catch(error){return json(res,400,{error:error.message||"Error de gateway."});}
    finally{active--;}
  }
  if(req.method!=="GET"&&req.method!=="HEAD")return json(res,405,{error:"Método no permitido."});
  let pathname;
  try{pathname=decodeURIComponent(url.pathname);}catch{return json(res,400,{error:"URL no válida."});}
  if(pathname==="/")pathname="/index.html";
  const file=path.resolve(directory,"."+pathname);
  if(!(file===directory||file.startsWith(directory+path.sep))||path.basename(file).startsWith(".")||
      file.endsWith("server.mjs")||file.includes("/tests/")||file.includes("/node_modules/"))
    return json(res,403,{error:"Archivo bloqueado."});
  try{
    const bytes=await readFile(file);
    res.writeHead(200,{"Content-Type":MIME[path.extname(file)]||"application/octet-stream",
      "Cache-Control":"no-store"});
    res.end(req.method==="HEAD"?null:bytes);
  }catch{return json(res,404,{error:"Archivo no encontrado."});}
}
if(process.argv[1] && path.resolve(process.argv[1])===fileURLToPath(import.meta.url)){
  http.createServer((req,res)=>{serve(req,res).catch(()=>json(res,500,{error:"Error interno."}));})
    .listen(PORT,BIND,()=>{
      console.log("Reqestra Web: http://"+BIND+":"+PORT);
      console.log("Gateway: "+(ALLOWED.size?"restringido a "+ALLOWED.size+" origen(es)":"desactivado"));
    });
}
