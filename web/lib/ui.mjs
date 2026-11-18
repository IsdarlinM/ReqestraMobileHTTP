import {redactUrl,statusTone,passiveRoutes} from "./core.mjs";
import {t,translateDom,getLocale} from "./i18n.mjs";
const $=id=>document.getElementById(id);
function node(tag,text,cls){
  const result=document.createElement(tag);
  if(text!==undefined && text!==null) result.textContent=String(text);
  if(cls)result.className=cls;
  return result;
}
function tag(text,tone="info"){return node("span",text,"tag "+tone);}
function action(label,fn,danger=false){
  const button=node("button",label,"tiny-button"+(danger?" danger":""));
  button.type="button";button.addEventListener("click",fn);return button;
}
function card(title){
  const c=node("article",null,"result-card");c.append(node("h3",title));return c;
}
export function notice(message,isError=false) {
  const box=$("notice");box.textContent=message;box.hidden=!message;
  box.className="notice"+(isError?" error":"");box.setAttribute("role",isError?"alert":"status");
  translateDom(box);
}
export function nav(section) {
  for(const page of document.querySelectorAll(".page"))page.classList.toggle("active",page.id===section);
  for(const button of document.querySelectorAll("[data-section]")){
    const active=button.dataset.section===section;button.classList.toggle("active",active);
    if(active)button.setAttribute("aria-current","page");else button.removeAttribute("aria-current");
  }
  const more=$("moreNav");
  if(more){
    const active=!["studio","collections","tests","discovery"].includes(section);
    more.classList.toggle("active",active);
    if(active)more.setAttribute("aria-current","page");else more.removeAttribute("aria-current");
  }
  window.scrollTo({top:0,behavior:"auto"});
}
export function responseSummary(response) {
  const box=$("studioResponse");box.replaceChildren();
  if(!response){box.append(node("strong","Esperando una petición"),node("p","El estado HTTP y la latencia aparecerán aquí."));return;}
  const code=node("div","HTTP "+response.status,"status-code "+statusTone(response.status));box.append(code);
  const stats=node("div",null,"stats");
  stats.append(tag(response.durationMs+" ms"),tag(response.size+" bytes"),tag(response.protocol||"Fetch"));
  if(response.bodyTruncated)stats.append(tag("Body truncado","warning"));
  box.append(stats,node("p",response.statusText||"Respuesta recibida"));
  const snippet=node("pre",response.body?.slice(0,1800)||"(sin cuerpo)","code-block");
  box.append(snippet);
}
export function inspector(response) {
  if(!response){
    $("inspectorRequest").textContent=t("No request yet.");
    $("inspectorMeta").textContent=t("No response yet.");
    $("inspectorBody").textContent=t("No response yet.");return;
  }
  const request=response.request;
  $("inspectorRequest").textContent=[
    request.method+" "+request.url,
    ...request.headers.map(([name,value])=>name+": "+value),
    "",
    request.body||"(sin cuerpo)",
  ].join("\n");
  $("inspectorMeta").textContent=[
    "HTTP "+response.status+" "+response.statusText,
    t("Protocolo: ")+response.protocol,
    t("Duration: ")+response.durationMs+" ms",
    t("Bytes: ")+response.size+(response.bodyTruncated?t(" (truncado)"):""),
    ...response.headers.map(([name,value])=>name+": "+value),
  ].join("\n");
  $("inspectorBody").textContent=response.body||"(sin cuerpo)";
}
export function environments(workspace,callbacks){
  const select=$("environmentSelect");select.replaceChildren();
  for(const env of workspace.environments){
    const option=node("option",env.name);option.value=env.name;option.dataset.noTranslate="true";select.append(option);
  }
  select.value=workspace.activeEnvironment;
  $("activeEnvTag").textContent=workspace.activeEnvironment;
  const active=workspace.environments.find(e=>e.name===workspace.activeEnvironment);
  const list=$("variablesList");list.replaceChildren();
  const entries=Object.keys(active?.variables||{}).sort();
  if(!entries.length){list.append(node("p","Sin variables definidas.","help"));return;}
  for(const name of entries){
    const line=node("div",null,"list-item"),label=node("div");
    label.append(node("strong","{{"+name+"}}"),node("small","Valor protegido · no se muestra"));
    line.append(label,action("Eliminar",()=>callbacks.removeVariable(name),true));list.append(line);
  }
}
export function collections(workspace,callbacks){
  $("savedCount").textContent=workspace.requests.length;
  const list=$("savedRequests");list.replaceChildren();
  if(!workspace.requests.length){list.append(node("p","No hay peticiones guardadas.","help"));}
  for(const entry of workspace.requests){
    const line=node("div",null,"list-item"),desc=node("div"),actions=node("div",null,"list-actions");
    const savedName=node("strong",entry.name);savedName.dataset.noTranslate="true";
    desc.append(savedName,node("small",entry.collection+" · "+entry.draft.method),
      node("small",redactUrl(entry.draft.url)));
    actions.append(action("Abrir",()=>callbacks.load(entry.id)),
      action("Borrar",()=>callbacks.delete(entry.id),true));
    line.append(desc,actions);list.append(line);
  }
  const selected=$("testCollection"),old=selected.value;selected.replaceChildren();
  const names=[...new Set(workspace.requests.map(r=>r.collection))].sort();
  for(const name of names){const opt=node("option",name);opt.value=name;selected.append(opt);}
  selected.value=names.includes(old)?old:(names[0]||"");
}
export function history(entries,callbacks){
  const container=$("historyList");container.replaceChildren();
  if(!entries.length){container.append(node("p","History is empty.","help"));return;}
  for(const entry of [...entries].reverse()){
    const element=card(entry.method+" · HTTP "+entry.status);
    element.append(tag(entry.durationMs+" ms"),node("p",entry.url),
      node("p",new Date(entry.at).toLocaleString(getLocale()==="zh"?"zh-CN":getLocale())));
    element.append(action("Reutilizar URL redactada",()=>callbacks.reuse(entry)));
    container.append(element);
  }
}
export function reportChecks(checks) {
  const el=$("testResults");el.replaceChildren();
  for(const item of checks){
    const c=card(item.name);
    c.append(tag(item.error?"ERROR":item.passed?"PASS":"FAIL",item.passed?"success":"error"));
    if(item.status!==undefined)c.append(tag("HTTP "+item.status,statusTone(item.status)),tag(item.durationMs+" ms"));
    if(item.error)c.append(node("p",item.error));
    for(const check of item.checks||[])c.append(node("p",(check.passed?"✓ ":"✗ ")+check.label));
    el.append(c);
  }
}
export function reportDiscovery(findings) {
  const el=$("discoveryResults");el.replaceChildren();
  for(const finding of findings){
    const c=card(finding.method+" · "+finding.label);
    if(finding.error){c.append(tag("ERROR","error"),node("p",finding.error));}
    else{
      c.append(tag("HTTP "+finding.status,statusTone(finding.status)),
        tag(finding.durationMs+" ms"),tag(finding.size+" bytes"));
      if(finding.allow)c.append(node("p","Allow: "+finding.allow));
      if(finding.changed)c.append(tag("Diferencia observada; verificar","info"));
    }
    el.append(c);
  }
}
export function renderPlugins(onOpen=()=>{}) {
  // Integrated modules are real navigation actions, not downloadable plug-ins.
  // Unsupported capabilities are explicitly disabled.
  const plugins=[
    ["HTTP Studio", "Create HTTP requests, set headers and edit bodies.", "studio"],
    ["Environment variables", "Manage local environments and query templates.", "collections"],
    ["Encrypted collections", "Save and restore requests inside an encrypted vault.", "collections"],
    ["Test assertions", "Run saved collections with status and latency checks.", "tests"],
    ["Endpoint explorer", "Explore authorized routes and candidate parameters.", "discovery"],
    ["cURL tools", "Import and export redacted requests.", "studio"],
    ["OpenAPI importer", "Import OpenAPI 3.x JSON operations.", "collections"],
    ["Sessions", "Manage origin-specific headers and browser cookies.", "session"],
    ["Private gateway", "Opt-in local gateway for authorized origins.", "studio"],
    ["GraphQL schema explorer", "Planned; no implementation yet.", null],
    ["External proxy connector", "Planned; no implementation yet.", null],
    ["Third-party plug-ins", "Not supported; no remote code runs.", null],
  ];
  const el=$("pluginsList");el.replaceChildren();
  for(const [name,description,section] of plugins){
    const c=card(t(name));
    c.append(tag(section?t("Integrado / opcional"):t("No disponible"),
      section?"success":"warning"),node("p",t(description)));
    if(section){
      const open=action(t("Abrir extensión"),()=>onOpen(section));
      open.setAttribute("aria-label",t("Abrir")+" "+name);c.append(open);
    }
    el.append(c);
  }
  translateDom(el);
}
export function preview(draft) {
  const frame=$("htmlPreview"),code=$("textPreview");
  if(draft.bodyMode==="HTML"){
    frame.hidden=false;code.hidden=true;
    // Both sandbox and CSP block scripts, navigation and remote resources.
    frame.srcdoc="<meta http-equiv=\"Content-Security-Policy\" content=\"default-src 'none'; style-src 'unsafe-inline'; img-src data:; form-action 'none'\">"+draft.body;
  }else{
    frame.hidden=true;frame.removeAttribute("srcdoc");code.hidden=false;
    code.textContent=draft.body||t("(sin contenido)");
  }
}
export function support(config){
  const id=String(config?.paypalHostedButtonId||"").trim(),button=$("donateLink"),help=$("donateHelp");
  if(/^[A-Za-z0-9]{6,64}$/.test(id)){
    button.href="https://www.paypal.com/donate/?hosted_button_id="+encodeURIComponent(id);
    button.removeAttribute("aria-disabled");
    help.textContent="You will be taken to PayPal's official site. No payments are processed within Reqestra.";
  }else{
    button.setAttribute("aria-disabled","true");
    button.removeAttribute("href");
    help.textContent="Donaciones desactivadas: configura paypalHostedButtonId en web/config.js.";
  }
}
export function mobileMenu(openSection) {
  const button=$("moreNav");
  button.addEventListener("click",()=>{
    const existing=$("mobileMoreMenu");
    if(existing){existing.remove();return;}
    const menu=node("div",null,"mobile-more");menu.id="mobileMoreMenu";
    for(const [id,title] of [
      ["history","Historial"],["inspector","Inspector"],["preview","Vista previa"],
      ["session","Sesiones"],["plugins","Extensiones"],["about","About & Help"],["support","Donar / Ayuda"]
    ]){
      const b=node("button",title);b.addEventListener("click",()=>{menu.remove();openSection(id);});
      menu.append(b);
    }
    document.body.append(menu);
  });
}
