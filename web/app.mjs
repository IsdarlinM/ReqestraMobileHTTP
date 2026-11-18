import {
  blankDraft,defaultWorkspace,expand,parametrizeDraft,validateDraft,
  redactUrl,redactHeaders,parseHeaders,parseOpenApi,planDiscovery,passiveRoutes,
  importCurl,exportCurl,evaluate,SAFE_PROBE_METHODS,MUTATING_PROBE_METHODS,
} from "./lib/core.mjs";
import {execute} from "./lib/transport.mjs";
import {
  hasVault,createVault,saveVault,unlockVault,getEncryptedBackup,
  importEncryptedBackup,deleteVault,
} from "./lib/vault.mjs";
import * as ui from "./lib/ui.mjs";

const $=id=>document.getElementById(id);
const sleep=(ms,signal)=>new Promise((resolve,reject)=>{
  if(signal?.aborted)return reject(Error("Cancelado."));
  const timer=setTimeout(()=>{signal?.removeEventListener("abort",abort);resolve();},ms);
  function abort(){clearTimeout(timer);signal?.removeEventListener("abort",abort);reject(Error("Cancelado."));}
  signal?.addEventListener("abort",abort,{once:true});
});
const DEFAULT_TYPES={
  NONE:"",RAW:"text/plain; charset=utf-8",JSON:"application/json; charset=utf-8",
  HTML:"text/html; charset=utf-8",XML:"application/xml; charset=utf-8",
  FORM_URLENCODED:"application/x-www-form-urlencoded; charset=utf-8",
  GRAPHQL:"application/json; charset=utf-8",
  BASE64_BINARY:"application/octet-stream",
};
let state={draft:blankDraft(),workspace:defaultWorkspace(),history:[],response:null};
let vaultKey=null,activeController=null,jobActive=false,pendingSave=Promise.resolve();
const memorySessions={};
const fields={
  url:"url",query:"queryParamsText",method:"method",customMethod:"customMethod",
  headers:"headersText",cookies:"cookiesText",body:"body",bodyMode:"bodyMode",
  contentType:"contentType",timeout:"timeoutSeconds",followRedirects:"followRedirects",
  allowCleartext:"allowCleartext",forceHttp1:"forceHttp1",
};
let gatewayAvailable=false;
let activeSection="studio";

function info(message){ui.notice(message,false);}
function fail(err){console.warn("Reqestra:",String(err?.message||err));ui.notice(String(err?.message||err),true);}
function origins(){
  try{return new URL(parametrizeDraft(state.draft,activeVariables()).url).origin;}
  catch{return null;}
}
function activeVariables(){
  const env=state.workspace.environments.find(x=>x.name===state.workspace.activeEnvironment);
  return env?.variables||{};
}
function snapshot(){
  return {draft:structuredClone(state.draft),workspace:structuredClone(state.workspace),
    history:structuredClone(state.history)};
}
function persist(){
  if(!vaultKey)return;
  const data=snapshot();
  pendingSave=pendingSave.catch(()=>{}).then(()=>saveVault(vaultKey,data));
  pendingSave.catch(fail);
}
function updateVaultLabel(){
  $("vaultStatus").textContent=vaultKey?"Cifrado · desbloqueado":
    hasVault()?"Cifrado · bloqueado":"Modo temporal";
}
function renderDraft(){
  for(const [id,key] of Object.entries(fields)){
    const field=$(id);
    if(field.type==="checkbox")field.checked=!!state.draft[key];
    else field.value=String(state.draft[key]??"");
  }
  $("customMethodField").hidden=state.draft.method!=="CUSTOM";
  $("sessionMode").value=state.draft.sessionMode||"NONE";
  ui.preview(state.draft);
}
function renderAll(){
  renderDraft();ui.responseSummary(state.response);ui.inspector(state.response);
  ui.environments(state.workspace,{removeVariable});ui.collections(state.workspace,{load:loadSaved,delete:deleteSaved});
  ui.history(state.history,{reuse:reuseHistory});ui.renderPlugins();ui.support(window.REQESTRA_CONFIG||{});
  updateVaultLabel();
}
function syncDraft(id){
  const field=$(id),key=fields[id];
  state.draft[key]=field.type==="checkbox"?field.checked:field.value;
  if(id==="timeout")state.draft.timeoutSeconds=Number(field.value)||0;
  if(id==="method"){
    $("customMethodField").hidden=state.draft.method!=="CUSTOM";
    if(["GET","HEAD"].includes(state.draft.method)){
      state.draft.bodyMode="NONE";state.draft.contentType="";
      $("bodyMode").value="NONE";$("contentType").value="";
    }
  }
  if(id==="bodyMode"){
    state.draft.contentType=DEFAULT_TYPES[state.draft.bodyMode]||"";
    $("contentType").value=state.draft.contentType;
  }
  if(id==="body"||id==="bodyMode")ui.preview(state.draft);
  persist();
}
function go(id){
  if(!$(id))return;
  activeSection=id;ui.nav(id);
  if(id==="session")renderSession();
  if(id==="preview")ui.preview(state.draft);
}
for(const button of document.querySelectorAll("[data-section]"))
  button.addEventListener("click",()=>go(button.dataset.section));
ui.mobileMenu(go);
for(const id of Object.keys(fields)){
  const field=$(id);
  field.addEventListener(field.tagName==="SELECT"||field.type==="checkbox"?"change":"input",()=>syncDraft(id));
}
$("themeToggle").addEventListener("click",()=>{
  const next=document.documentElement.dataset.theme==="light"?"dark":"light";
  document.documentElement.dataset.theme=next;
  localStorage.setItem("reqestra.theme",next);
});
const preferred=localStorage.getItem("reqestra.theme");
if(["light","dark"].includes(preferred))document.documentElement.dataset.theme=preferred;
else document.documentElement.dataset.theme=matchMedia("(prefers-color-scheme:light)").matches?"light":"dark";
function sessionHeadersForOrigin(draft=state.draft){
  let origin;
  try {origin=new URL(parametrizeDraft(draft,activeVariables()).url).origin;}
  catch {origin=null;}
  if(!origin || state.draft.sessionMode==="NONE")return "";
  const saved=state.draft.sessionMode==="PERSISTENT"?
    state.workspace.sessions?.[origin] : memorySessions[origin];
  return saved||"";
}
function renderSession(){
  const origin=origins();$("sessionOrigin").textContent=origin||"URL inválida";
  $("sessionMode").value=state.draft.sessionMode;
  $("sessionHeaders").value=sessionHeadersForOrigin();
}
$("sessionMode").addEventListener("change",()=>{
  state.draft.sessionMode=$("sessionMode").value;renderSession();persist();
});
$("saveSession").addEventListener("click",()=>{
  try{
    const origin=origins();if(!origin)throw Error("Configura una URL válida en Estudio.");
    const value=$("sessionHeaders").value.trim();
    parseHeaders(value,{gateway:$("transportMode").value==="gateway"});
    if(state.draft.sessionMode==="NONE")throw Error("Selecciona un modo de sesión.");
    if(state.draft.sessionMode==="PERSISTENT"){
      if(!vaultKey)throw Error("Para persistir headers, primero crea o desbloquea el almacén cifrado.");
      state.workspace.sessions??={};state.workspace.sessions[origin]=value;
    }else memorySessions[origin]=value;
    persist();info("Headers guardados exclusivamente para "+origin);
  }catch(e){fail(e);}
});
$("clearSession").addEventListener("click",()=>{
  const origin=origins();if(!origin)return;
  delete memorySessions[origin];if(state.workspace.sessions)delete state.workspace.sessions[origin];
  $("sessionHeaders").value="";persist();info("Headers de este origen limpiados. Las cookies del navegador se gestionan fuera de Reqestra.");
});
function appendHistory(response){
  state.history.push({
    id:crypto.randomUUID(),url:response.request.url,method:response.request.method,
    status:response.status,durationMs:response.durationMs,at:new Date().toISOString(),
  });
  state.history=state.history.slice(-50);ui.history(state.history,{reuse:reuseHistory});persist();
}
function reuseHistory(entry){
  state.draft.url=entry.url;renderDraft();go("studio");
  info("Se cargó la URL redactada; revisa la query antes de enviar.");persist();
}
function startJob(buttonIds){
  if(jobActive)throw Error("Ya hay otra operación activa.");
  jobActive=true;activeController=new AbortController();
  for(const id of buttonIds)$(id).disabled=true;
  return activeController.signal;
}
function endJob(buttonIds){
  jobActive=false;activeController=null;
  for(const id of buttonIds)$(id).disabled=false;
}
for(const id of ["cancel","cancelTests","cancelDiscovery"])
  $(id).addEventListener("click",()=>activeController?.abort());
async function send(){
  const controls=["send","runTests","runDiscovery"];
  try{
    const signal=startJob(controls);
    $("cancel").disabled=false;
    const result=await execute(state.draft,{
      variables:activeVariables(),sessionHeadersText:expand(sessionHeadersForOrigin(),activeVariables()),
      mode:state.draft.sessionMode,gateway:$("transportMode").value==="gateway",signal,
    });
    state.response=result;ui.responseSummary(result);ui.inspector(result);appendHistory(result);
    info("Petición completada. HTTP "+result.status+" · "+result.durationMs+" ms");
  }catch(e){fail(e);}
  finally{$("cancel").disabled=true;endJob(controls);}
}
$("send").addEventListener("click",send);
$("formatJson").addEventListener("click",()=>{
  try{
    if(state.draft.bodyMode!=="JSON")throw Error("Selecciona el tipo de cuerpo JSON.");
    state.draft.body=JSON.stringify(JSON.parse(state.draft.body),null,2);
    $("body").value=state.draft.body;ui.preview(state.draft);persist();info("JSON formateado.");
  }catch(e){fail(e);}
});
$("importCurl").addEventListener("click",()=>{
  try{
    state.draft={...blankDraft(),...importCurl($("curlInput").value)};
    renderDraft();persist();info("cURL importado. Revisa los datos antes de enviar.");
  }catch(e){fail(e);}
});
$("exportCurl").addEventListener("click",async()=>{
  try{
    const text=exportCurl(state.draft,activeVariables());
    await navigator.clipboard.writeText(text);
    info("Comando cURL redactado copiado.");
  }catch(e){fail(e);}
});
$("copyResponse").addEventListener("click",async()=>{
  try{
    if(!state.response)throw Error("No existe respuesta.");
    await navigator.clipboard.writeText(state.response.body);info("Cuerpo copiado al portapapeles.");
  }catch(e){fail(e);}
});
function mutateWorkspace(fn){
  fn();
  ui.environments(state.workspace,{removeVariable});
  ui.collections(state.workspace,{load:loadSaved,delete:deleteSaved});persist();
}
function removeVariable(name){
  if(!confirm("¿Eliminar la variable "+name+"?"))return;
  mutateWorkspace(()=>{
    const env=state.workspace.environments.find(x=>x.name===state.workspace.activeEnvironment);
    delete env.variables[name];
  });
}
$("environmentSelect").addEventListener("change",()=>{
  state.workspace.activeEnvironment=$("environmentSelect").value;
  ui.environments(state.workspace,{removeVariable});persist();
});
$("createEnvironment").addEventListener("click",()=>{
  try{
    const name=$("environmentName").value.trim();
    if(!name||name.length>40)throw Error("El entorno necesita nombre de 1 a 40 caracteres.");
    if(state.workspace.environments.length>=12)throw Error("Máximo 12 entornos.");
    if(state.workspace.environments.some(x=>x.name===name))throw Error("El entorno ya existe.");
    mutateWorkspace(()=>{
      state.workspace.environments.push({name,variables:{}});state.workspace.activeEnvironment=name;
    });
    $("environmentName").value="";info("Entorno creado.");
  }catch(e){fail(e);}
});
$("saveVariable").addEventListener("click",()=>{
  try{
    const name=$("variableName").value.trim(),value=$("variableValue").value;
    if(!/^[A-Za-z][A-Za-z0-9_]{0,63}$/.test(name))throw Error("Nombre de variable inválido.");
    if(value.length>4096)throw Error("Variable demasiado larga.");
    mutateWorkspace(()=>{
      const env=state.workspace.environments.find(x=>x.name===state.workspace.activeEnvironment);
      env.variables[name]=value;
    });
    $("variableValue").value="";info("Variable guardada. Su valor no se muestra.");
  }catch(e){fail(e);}
});
$("saveRequest").addEventListener("click",()=>{
  try{
    const name=$("requestName").value.trim(),collection=$("collectionName").value.trim();
    if(!name||name.length>80||!collection||collection.length>60)throw Error("Nombre o colección no válidos.");
    if(state.workspace.requests.length>=120)throw Error("Máximo 120 solicitudes guardadas.");
    mutateWorkspace(()=>state.workspace.requests.push({
      id:crypto.randomUUID(),name,collection,draft:structuredClone(state.draft),
    }));
    $("requestName").value="";info("Petición guardada en "+collection);
  }catch(e){fail(e);}
});
function loadSaved(id){
  const entry=state.workspace.requests.find(x=>x.id===id);if(!entry)return;
  const mode=state.draft.sessionMode;
  state.draft={...blankDraft(),...structuredClone(entry.draft),sessionMode:mode};
  renderDraft();go("studio");persist();info("Petición cargada desde "+entry.collection);
}
function deleteSaved(id){
  if(!confirm("¿Borrar la petición guardada?"))return;
  mutateWorkspace(()=>state.workspace.requests=state.workspace.requests.filter(x=>x.id!==id));
}
$("importOpenApi").addEventListener("click",()=>{
  try{
    const items=parseOpenApi($("openapiJson").value);
    if(state.workspace.requests.length+items.length>120)throw Error("Importación supera 120 solicitudes.");
    mutateWorkspace(()=>state.workspace.requests.push(...items));
    $("openapiJson").value="";info("Importadas "+items.length+" operaciones OpenAPI.");
  }catch(e){fail(e);}
});
$("clearHistory").addEventListener("click",()=>{
  if(!confirm("¿Eliminar el historial local?"))return;
  state.history=[];ui.history(state.history,{reuse:reuseHistory});persist();info("Historial eliminado.");
});
async function runTests(){
  const controls=["send","runTests","runDiscovery"];
  try{
    const collection=$("testCollection").value;
    const items=state.workspace.requests.filter(x=>x.collection===collection);
    if(items.length<1||items.length>20)throw Error("Selecciona una colección de 1 a 20 solicitudes.");
    const status=Number($("expectedStatus").value),limit=Number($("maxLatency").value);
    if(!Number.isInteger(status)||status<100||status>599||!Number.isInteger(limit)||limit<1||limit>120000)
      throw Error("Assertions de estado o latencia inválidas.");
    if(!confirm("Se enviarán "+items.length+" peticiones reales que podrían modificar datos. ¿Continuar?"))return;
    const signal=startJob(controls);$("cancelTests").disabled=false;
    const checks=[];
    for(let i=0;i<items.length;i++){
      if(signal.aborted)throw Error("Pruebas canceladas.");
      if(i>0)await sleep(1000,signal);
      const entry=items[i];
      try{
        const result=await execute(entry.draft,{
          variables:activeVariables(),mode:state.draft.sessionMode,
          sessionHeadersText:expand(sessionHeadersForOrigin(entry.draft),activeVariables()),
          gateway:$("transportMode").value==="gateway",signal,
        });
        const verdict=evaluate(result,status,limit);
        checks.push({name:entry.name,status:result.status,durationMs:result.durationMs,
          passed:verdict.passed,checks:verdict.checks});
      }catch(error){
        if(signal.aborted)throw error;
        checks.push({name:entry.name,passed:false,error:error.message});
      }
      $("testProgress").textContent=(i+1)+" / "+items.length;
      ui.reportChecks(checks);
    }
    info("Pruebas terminadas: "+checks.filter(c=>c.passed).length+" correctas / "+checks.length);
  }catch(e){fail(e);}
  finally{$("cancelTests").disabled=true;endJob(controls);}
}
$("runTests").addEventListener("click",runTests);
$("enableMutating").addEventListener("change",()=>{
  const enabled=$("enableMutating").checked;
  $("mutatingMethods").hidden=!enabled;
  if(!enabled)for(const element of document.querySelectorAll(".probe-method"))
    if(MUTATING_PROBE_METHODS.includes(element.value))element.checked=false;
});
$("addHints").addEventListener("click",()=>{
  try{
    if(!state.response)throw Error("Primero ejecuta una petición en Estudio.");
    const base=state.response.request.url;
    const hints=passiveRoutes(state.response,base);
    if(!hints.length)throw Error("No se encontraron rutas sugeridas del mismo origen.");
    $("routes").value=[...new Set([...$("routes").value.split(/\r?\n/).filter(Boolean),...hints])].slice(0,8).join("\n");
    info("Se agregaron "+hints.length+" rutas pasivas para revisión.");
  }catch(e){fail(e);}
});
async function runDiscovery(){
  const controls=["send","runTests","runDiscovery"];
  try{
    if(!$("confirmAuthorization").checked)throw Error("Confirma autorización para este origen.");
    const methods=[...document.querySelectorAll(".probe-method:checked")].map(x=>x.value);
    const resolved=parametrizeDraft(state.draft,activeVariables());
    const targets=planDiscovery(resolved.url,
      expand($("routes").value,activeVariables()),expand($("parameterCandidates").value,activeVariables()),
      methods,$("enableMutating").checked);
    if(!confirm("¿Enviar "+targets.length+" peticiones al mismo origen, una por segundo? Los métodos mutantes pueden cambiar datos."))
      return;
    const signal=startJob(controls);$("cancelDiscovery").disabled=false;
    const findings=[];let baseline=null;
    const probeBase={...resolved,queryParamsText:"",method:"GET",bodyMode:"NONE",
      body:"",contentType:"",cookiesText:"",sessionMode:"NONE",
      followRedirects:false,timeoutSeconds:Math.min(15,Number(state.draft.timeoutSeconds)||15)};
    for(let i=0;i<targets.length;i++){
      if(signal.aborted)throw Error("Exploración cancelada.");
      if(i>0)await sleep(1000,signal);
      const target=targets[i];
      try{
        const result=await execute({...probeBase,url:target.url,method:target.method},{
          variables:{},mode:"NONE",gateway:$("transportMode").value==="gateway",signal,
        });
        if(target.kind==="BASELINE")baseline={status:result.status,size:result.size};
        findings.push({...target,status:result.status,size:result.size,
          durationMs:result.durationMs,
          allow:result.headers.find(([name])=>name.toLowerCase()==="allow")?.[1]||"",
          changed:target.kind==="PARAMETER"&&baseline!=null&&
            (baseline.status!==result.status||baseline.size!==result.size)});
      }catch(error){
        if(signal.aborted)throw error;
        findings.push({...target,error:error.message});
      }
      $("discoveryProgress").textContent=(i+1)+" / "+targets.length;
      ui.reportDiscovery(findings);
    }
    info("Exploración terminada. Los cambios de respuesta son indicios, no vulnerabilidades confirmadas.");
  }catch(e){fail(e);}
  finally{$("cancelDiscovery").disabled=true;endJob(controls);}
}
$("runDiscovery").addEventListener("click",runDiscovery);
$("vaultCreate").addEventListener("click",async()=>{
  try{
    vaultKey=await createVault($("vaultPassword").value,snapshot());
    $("vaultPassword").value="";updateVaultLabel();info("Espacio cifrado creado. Guarda tu contraseña.");
  }catch(e){fail(e);}
});
$("vaultUnlock").addEventListener("click",async()=>{
  try{
    await pendingSave;
    const result=await unlockVault($("vaultPassword").value);
    vaultKey=result.key;
    state={draft:{...blankDraft(),...result.data.draft},
      workspace:{...defaultWorkspace(),...result.data.workspace},
      history:result.data.history||[],response:null};
    $("vaultPassword").value="";renderAll();info("Espacio cifrado desbloqueado.");
  }catch(e){fail(e);}
});
$("vaultLock").addEventListener("click",async()=>{
  try{
    await pendingSave;
    vaultKey=null;
    state={draft:blankDraft(),workspace:defaultWorkspace(),history:[],response:null};
    renderAll();info("Espacio bloqueado. La memoria temporal se reinició.");
  }catch(e){fail(e);}
});
$("vaultExport").addEventListener("click",()=>{
  try{
    const backup=getEncryptedBackup();
    const blob=new Blob([backup],{type:"application/json"});
    const link=document.createElement("a");link.href=URL.createObjectURL(blob);
    link.download="reqestra-web-vault.enc.json";link.click();
    setTimeout(()=>URL.revokeObjectURL(link.href),5000);
    info("Respaldo cifrado descargado.");
  }catch(e){fail(e);}
});
$("vaultImportFile").addEventListener("change",async()=>{
  try{
    const file=$("vaultImportFile").files?.[0];if(!file)return;
    if(hasVault()&&!confirm("Importar reemplazará el espacio cifrado de este navegador. ¿Continuar?"))return;
    const raw=await file.text();
    importEncryptedBackup(raw);
    vaultKey=null;state={draft:blankDraft(),workspace:defaultWorkspace(),history:[],response:null};
    renderAll();info("Respaldo importado. Desbloquéalo con su contraseña original.");
  }catch(e){fail(e);}
  finally{$("vaultImportFile").value="";}
});
$("transportMode").addEventListener("change",()=>{
  if($("transportMode").value==="gateway"&&!gatewayAvailable)
    info("El gateway requiere ejecutar web/server.mjs y configurar REQESTRA_ALLOWED_ORIGINS.");
});
async function checkGateway(){
  try{
    const response=await fetch("/api/health",{cache:"no-store"});
    if(!response.ok)return;
    const data=await response.json();
    gatewayAvailable=!!data.gateway;
    $("engineStatus").textContent=gatewayAvailable?"Gateway disponible":"Motor navegador";
  }catch{/* static deployments have no backend */}
}
renderAll();
ui.nav("studio");
checkGateway();
if("serviceWorker" in navigator && (location.protocol==="https:"||location.hostname==="localhost"))
  navigator.serviceWorker.register("./sw.js").catch(()=>{});
