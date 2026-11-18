import test from "node:test";
import assert from "node:assert/strict";
import {
  expand,blankDraft,parametrizeDraft,parseQueryRows,parseHeaders,validateDraft,
  parseOpenApi,planDiscovery,passiveRoutes,importCurl,exportCurl,evaluate,
} from "../lib/core.mjs";

test("environment variables expand in URL, headers, body, query and Content-Type",()=>{
  const resolved=parametrizeDraft({
    ...blankDraft(),url:"{{base}}/v1/users?existing=1",
    queryParamsText:"q={{search}}\ntag=x&y\nnote=x=y",
    headersText:"Authorization: Bearer {{token}}",
    body:"{{search}}",bodyMode:"RAW",contentType:"text/{{type}}",
  },{base:"https://api.example.com",search:"hola mundo",token:"secret",type:"plain"});
  const u=new URL(resolved.url);
  assert.equal(u.searchParams.get("existing"),"1");
  assert.equal(u.searchParams.get("q"),"hola mundo");
  assert.equal(u.searchParams.get("tag"),"x&y");
  assert.equal(u.searchParams.get("note"),"x=y");
  assert.equal(resolved.headersText,"Authorization: Bearer secret");
  assert.equal(resolved.body,"hola mundo");
  assert.equal(resolved.contentType,"text/plain");
  assert.equal(resolved.queryParamsText,"");
});
test("rejects missing variables and invalid query/header syntax",()=>{
  assert.throws(()=>expand("{{missing}}",{}),/Falta la variable/);
  assert.throws(()=>parseQueryRows("not-a-pair"),/nombre=valor/);
  assert.throws(()=>parseHeaders("Content-Length: 7"),/administrado/);
  assert.throws(()=>parseHeaders("Cookie: abc"),/navegador/);
});
test("validates safe body and browser limitations",()=>{
  const get=blankDraft();
  assert.equal(validateDraft(get).method,"GET");
  assert.throws(()=>validateDraft({...get,bodyMode:"JSON",body:"{}"}),/no admite body/);
  assert.throws(()=>validateDraft({...get,url:"http://api.example.com"}),/HTTP necesita/);
  assert.throws(()=>validateDraft({...get,url:"https://u:p@example.com"}),/URL HTTP/);
  assert.throws(()=>validateDraft({...get,cookiesText:"x=y"}),/navegador/);
  assert.equal(validateDraft({...get,cookiesText:"x=y"},{gateway:true}).method,"GET");
});
test("OpenAPI JSON imports methods without remote references",()=>{
  const spec={openapi:"3.0.0",servers:[{url:"https://api.example.com"}],
    paths:{"/v1/users":{get:{operationId:"listUsers"},post:{summary:"Crear"}}}};
  const requests=parseOpenApi(JSON.stringify(spec));
  assert.equal(requests.length,2);
  assert.equal(requests[0].draft.url,"https://api.example.com/v1/users");
  assert.deepEqual(requests.map(r=>r.draft.method),["GET","POST"]);
});
test("discovery limits and origin constraints",()=>{
  const base="https://api.example.com:8443/v1?existing=1";
  const plan=planDiscovery(base,"/health\n/api/v1","page\ninclude",["GET","HEAD","OPTIONS"]);
  assert.equal(plan.length,7);
  assert(plan.every(x=>new URL(x.url).origin===new URL(base).origin));
  assert.throws(()=>planDiscovery(base,"/../admin","",["GET"]),/Ruta no válida/);
  assert.throws(()=>planDiscovery(base,"https://other.example/","",["GET"]),/Ruta no válida/);
  assert.throws(()=>planDiscovery(base,"","",["POST"]),/Métodos no autorizados/);
  assert.equal(planDiscovery(base,"","",["POST"],true).length,2);
  assert.throws(()=>planDiscovery(base,Array.from({length:9},(_,i)=>"/"+i).join("\n"),"",["GET"]),/Máximo 8/);
});
test("passive hint extraction refuses external routes",()=>{
  const result=passiveRoutes({
    body:'{"href":"/v1/me","endpoint":"https://foreign.example/admin"} <a href="/health">health</a>',
    headers:[["link","</other>; rel=\"next\""]],
  },"https://api.example.com");
  assert.deepEqual(result,["/v1/me","/health","/other"]);
});
test("cURL import never executes shell and export redacts query and Authorization",()=>{
  const draft=importCurl("curl --request GET --url 'https://example.com/user?token=secret' -H 'Authorization: Bearer abc'");
  assert.equal(draft.method,"GET");
  const exported=exportCurl(draft,{});
  assert(exported.includes("REDACTED"));
  assert(!exported.includes("secret"));
  assert(!exported.includes("abc"));
  assert.throws(()=>importCurl("curl -k https://example.com"),/TLS inseguro/);
});
test("assertion results are deterministic",()=>{
  assert.equal(evaluate({status:200,durationMs:50},200,100).passed,true);
  assert.equal(evaluate({status:500,durationMs:150},200,100).passed,false);
});
