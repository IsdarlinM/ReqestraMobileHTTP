import test from "node:test";
import assert from "node:assert/strict";
import http from "node:http";
import {once} from "node:events";
import {blankDraft} from "../lib/core.mjs";
import {execute} from "../lib/transport.mjs";

test("local HTTP transport constructs query/headers, redacts results and reads body",async()=>{
  const server=http.createServer((req,res)=>{
    const url=new URL(req.url,"http://localhost");
    assert.equal(url.searchParams.get("page"),"2");
    assert.equal(req.headers.accept,"application/json");
    res.writeHead(200,{"Content-Type":"application/json","X-Test":"ok"});
    res.end('{"result":"ok"}');
  });
  server.listen(0,"127.0.0.1");
  await once(server,"listening");
  try{
    const address=server.address();
    const url="http://127.0.0.1:"+address.port+"/users?token=secret";
    const result=await execute({
      ...blankDraft(),url,queryParamsText:"page=2",
      headersText:"Accept: application/json",allowCleartext:true,
    });
    assert.equal(result.status,200);
    assert.equal(result.body,'{"result":"ok"}');
    assert(result.request.url.includes("token=REDACTED"));
    assert(!result.request.url.includes("secret"));
    assert.equal(result.size,new TextEncoder().encode(result.body).length);
  }finally{server.close();await once(server,"close");}
});
test("browser transport rejects manual cookie before network access",async()=>{
  await assert.rejects(execute({
    ...blankDraft(),url:"https://example.com",cookiesText:"sid=secret",
  }),/navegador no permite enviar Cookie manual/);
});
