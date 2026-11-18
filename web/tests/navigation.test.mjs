import test from "node:test";
import assert from "node:assert/strict";
import {readFileSync} from "node:fs";

const html=readFileSync(new URL("../index.html",import.meta.url),"utf8");
const app=readFileSync(new URL("../app.mjs",import.meta.url),"utf8");
const android=readFileSync(
  new URL("../../app/src/main/java/com/imr/reqestra/presentation/ReqestraApp.kt",import.meta.url),"utf8");
const sheet=readFileSync(
  new URL("../../app/src/main/java/com/imr/reqestra/presentation/MoreToolsSheet.kt",import.meta.url),"utf8");
const activity=readFileSync(
  new URL("../../app/src/main/java/com/imr/reqestra/MainActivity.kt",import.meta.url),"utf8");
const manifest=readFileSync(
  new URL("../manifest.webmanifest",import.meta.url),"utf8");
const mobileManifest=readFileSync(
  new URL("../../app/src/main/AndroidManifest.xml",import.meta.url),"utf8");

test("Web Donate stays visible in the topbar and routes to Support",()=>{
  const header=html.match(/<header class="topbar">([\s\S]*?)<\/header>/)?.[1];
  assert.ok(header,"Topbar must exist");
  assert.match(header,/id="donateNav"[\s\S]*?data-section="support"/);
  assert.match(header,/class="donate-cta"/);
  assert.match(header,/id="languageSelect"/);
  assert.match(app,/button\.addEventListener\("click",\(\)=>go\(button\.dataset\.section\)\)/);
});
test("Web mobile nav keeps five accessible destinations",()=>{
  const nav=html.match(/<nav class="mobile-nav"[\s\S]*?<\/nav>/)?.[0];
  assert.ok(nav);
  assert.equal((nav.match(/<button\b/g)||[]).length,5);
  for(const id of ["studio","collections","tests","discovery"])
    assert.match(nav,new RegExp('data-section="'+id+'"'));
  assert.match(nav,/id="moreNav"/);
});
test("Android provides exactly one main menu entry: bottom More",()=>{
  const topbar=android.slice(android.indexOf("topBar = {"),android.indexOf("bottomBar = {"));
  assert.doesNotMatch(topbar,/openMenu = true/);
  assert.doesNotMatch(topbar,/Icons\.Outlined\.MoreVert/);
  assert.equal((android.match(/openMenu = true/g)||[]).length,1);
  assert.match(android,/label = tr\("Más"\)/);
  assert.match(android,/MoreToolsSheet\(/);
  for(const section of ["HISTORY","RESPONSE","PREVIEW","SESSION","PLUGINS"]){
    assert.match(sheet,new RegExp("AppSection\\."+section+"\\)"));
  }
  assert.doesNotMatch(sheet,/AppSection\.SUPPORT/);
});
test("Android appearance has a persistent, accessible light/dark toggle",()=>{
  const topbar=android.slice(android.indexOf("topBar = {"),android.indexOf("bottomBar = {"));
  assert.match(topbar,/onClick = onToggleTheme/);
  assert.match(topbar,/Icons\.Outlined\.LightMode/);
  assert.match(topbar,/Icons\.Outlined\.DarkMode/);
  assert.match(activity,/UiThemePreference\.isDark/);
  assert.match(activity,/putString\(UiThemePreference\.KEY, next\)/);
  assert.match(activity,/ReqestraTheme\(darkTheme = darkTheme\)/);
});
test("Menu colors and press feedback are bounded, without excessive ripples",()=>{
  assert.match(sheet,/containerColor = colors\.background/);
  assert.match(sheet,/\.background\(if \(pressed\) palette\.surfaceVariant else palette\.surface\)/);
  assert.match(sheet,/indication = null/);
  assert.match(android,/collectIsPressedAsState/);
  assert.match(android,/indication = null/);
});
test("The same PNG is included in Android and the Web manifest",()=>{
  const webPng=readFileSync(new URL("../icon.png",import.meta.url));
  const androidPng=readFileSync(
    new URL("../../app/src/main/res/drawable-nodpi/reqestra_brand.png",import.meta.url));
  assert.deepEqual(androidPng,webPng);
  assert.deepEqual([...webPng.subarray(0,8)],[137,80,78,71,13,10,26,10]);
  assert.equal(JSON.parse(manifest).icons[0].src,"icon.png");
  assert.match(mobileManifest,/android:icon="@drawable\/reqestra_brand"/);
});
