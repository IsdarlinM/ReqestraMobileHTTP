import test from "node:test";
import assert from "node:assert/strict";
import {readFileSync} from "node:fs";

const html=readFileSync(new URL("../index.html",import.meta.url),"utf8");
const app=readFileSync(new URL("../app.mjs",import.meta.url),"utf8");
const android=readFileSync(
  new URL("../../app/src/main/java/com/imr/reqestra/presentation/ReqestraApp.kt",import.meta.url),"utf8");
const sheet=readFileSync(
  new URL("../../app/src/main/java/com/imr/reqestra/presentation/MoreToolsSheet.kt",import.meta.url),"utf8");

test("Web Donate action is visible in the top header and routes to Support",()=>{
  const header=html.match(/<header class="topbar">([\s\S]*?)<\/header>/)?.[1];
  assert.ok(header,"Topbar must exist");
  assert.match(header,/id="donateNav"[\s\S]*?data-section="support"/);
  assert.match(header,/class="donate-cta"/);
  assert.match(header,/id="languageSelect"/);
  assert.match(app,/button\.addEventListener\("click",\(\)=>go\(button\.dataset\.section\)\)/);
});
test("Web primary mobile nav remains five items",()=>{
  const nav=html.match(/<nav class="mobile-nav"[\s\S]*?<\/nav>/)?.[0];
  assert.ok(nav);
  assert.equal((nav.match(/<button\b/g)||[]).length,5);
  for(const id of ["studio","collections","tests","discovery"])assert.match(nav,new RegExp('data-section="'+id+'"'));
  assert.match(nav,/id="moreNav"/);
});
test("Android Donate action appears before overflow and new menu preserves secondary screens",()=>{
  const topbar=android.slice(android.indexOf("topBar = {"),android.indexOf("bottomBar = {"));
  const donate=topbar.indexOf("selectSection(AppSection.SUPPORT)");
  const overflow=topbar.indexOf("Icon(Icons.Outlined.MoreVert");
  assert.ok(donate>0&&overflow>donate,"Donation action must precede overflow menu");
  assert.match(android,/MoreToolsSheet\(/);
  assert.match(android,/label = tr\("Más"\)/);
  for(const section of ["HISTORY","RESPONSE","PREVIEW","SESSION","PLUGINS","SUPPORT"]){
    assert.match(sheet,new RegExp("AppSection\\."+section+"\\)"));
  }
});
