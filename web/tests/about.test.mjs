import test from "node:test";
import assert from "node:assert/strict";
import {readFileSync} from "node:fs";
import {LOCALES,t,translationCoverage} from "../lib/i18n.mjs";

const load=path=>readFileSync(new URL(path,import.meta.url),"utf8");
const html=load("../index.html");
const css=load("../styles.css");
const mobile=load("../../app/src/main/java/com/imr/reqestra/presentation/ReqestraApp.kt");
const sheet=load("../../app/src/main/java/com/imr/reqestra/presentation/MoreToolsSheet.kt");
const viewModel=load("../../app/src/main/java/com/imr/reqestra/presentation/MainViewModel.kt");
const aboutAndroid=load("../../app/src/main/java/com/imr/reqestra/presentation/about/AboutScreen.kt");

test("About & Help is a real navigable page in desktop and mobile Web",()=>{
  assert.match(html,/<button data-section="about">/);
  assert.match(html,/<section id="about" class="page" aria-labelledby="aboutTitle">/);
  assert.match(html,/id="aboutTitle"/);
  assert.match(load("../lib/ui.mjs"),/\["about","About & Help"\]/);
  assert.match(html,/data-section="studio"[^>]*>Open Studio<\/button>/);
  assert.match(html,/data-section="plugins"[^>]*>View extensions<\/button>/);
  assert.match(css,/\.about-grid\{/);
  assert.match(css,/@media\(max-width:450px\)/);
});

test("Android About & Help is available only through the existing More menu",()=>{
  assert.match(viewModel,/AppSection \{[^}]*ABOUT \}/);
  assert.match(mobile,/AppSection\.ABOUT -> AboutScreen\(\)/);
  assert.match(sheet,/onSelect\(AppSection\.ABOUT\)/);
  assert.match(aboutAndroid,/BuildConfig\.VERSION_NAME/);
  assert.match(aboutAndroid,/R\.drawable\.reqestra_brand/);
  assert.match(aboutAndroid,/GUIA_DE_USO\.md/);
  assert.match(aboutAndroid,/https:\/\/github\.com\/IsdarlinM\/ReqestraMobileHTTP\/issues/);
});

test("About texts are available in all six app languages",()=>{
  assert.deepEqual(LOCALES.map(x=>x.code),["en","es","ru","zh","pt","fr"]);
  for(const locale of LOCALES){
    for(const label of ["Acerca de y ayuda","Guía rápida","Privacidad y seguridad",
                        "Documentación","Reportar un problema"]){
      const translated=t(label,locale.code);
      assert.ok(translated.trim(),locale.code+" missing "+label);
    }
  }
  assert.equal(t("About & Help","es"),"Acerca de y ayuda");
  assert.equal(t("About & Help","ru"),"О приложении и помощь");
  assert.equal(t("Quick start","zh"),"快速入门");
  for(const [locale,result] of Object.entries(translationCoverage())){
    assert.deepEqual(result.missing,[],locale+" has empty catalog translations");
  }
});

test("About help links use known HTTPS repository pages without tracking",()=>{
  assert.match(html,/docs\/GUIA_DE_USO\.md/);
  assert.match(html,/docs\/WEB_SETUP\.md/);
  assert.match(html,/ReqestraMobileHTTP\/issues/);
  assert.match(html,/rel="noopener noreferrer"/);
  assert.doesNotMatch(html,/mailto:|analytics\/collect/);
  assert.ok(load("../../docs/GUIA_DE_USO.md").includes("Guía de uso de Reqestra"));
});

test("About section uses the 3.5.1 release version",()=>{
  const gradle=load("../../app/build.gradle.kts");
  const pkg=JSON.parse(load("../package.json"));
  assert.equal(pkg.version,"3.5.1");
  assert.match(gradle,/versionName = "3\.5\.1"/);
  assert.match(aboutAndroid,/BuildConfig\.VERSION_NAME/);
});
