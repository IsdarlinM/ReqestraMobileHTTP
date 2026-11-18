import test from "node:test";
import assert from "node:assert/strict";
import {LOCALES,normalizeLocale,deviceLocale,t,translationCoverage} from "../lib/i18n.mjs";
import {readFileSync} from "node:fs";

test("six supported languages with English fallback",()=>{
  assert.deepEqual(LOCALES.map(l=>l.code),["en","es","ru","zh","pt","fr"]);
  assert.equal(normalizeLocale("zh-CN"),"zh");
  assert.equal(normalizeLocale("pt-BR"),"pt");
  assert.equal(normalizeLocale("ru-RU"),"ru");
  assert.equal(normalizeLocale("it-IT"),"en");
});
test("translations cover main navigation in all languages",()=>{
  assert.equal(t("Estudio","en"),"Studio");
  assert.equal(t("Estudio","es"),"Estudio");
  assert.equal(t("Estudio","ru"),"Студия");
  assert.equal(t("Estudio","zh"),"工作台");
  assert.equal(t("Estudio","pt"),"Estúdio");
  assert.equal(t("Estudio","fr"),"Studio");
});
test("primary device language overrides secondary accepted languages",()=>{
  const previous=globalThis.navigator;
  Object.defineProperty(globalThis,"navigator",{
    value:{language:"it-IT",languages:["it-IT","es-DO"]},
    configurable:true
  });
  try{
    assert.equal(deviceLocale(),"en");
    globalThis.navigator.languages=["fr-FR","en-US"];
    assert.equal(deviceLocale(),"fr");
  }finally{
    if(previous===undefined)delete globalThis.navigator;
    else Object.defineProperty(globalThis,"navigator",{value:previous,configurable:true});
  }
});

test("each registered interface label has a nonempty translation for all six locales",()=>{
  const audit=translationCoverage();
  assert.ok(audit.en.total>300);
  assert.deepEqual(Object.keys(audit),["en","es","ru","zh","pt","fr"]);
  for(const [locale,result] of Object.entries(audit))
    assert.deepEqual(result.missing,[],locale+" has untranslated catalog entries");
});
test("English-authored pages can be translated back to any selected language",()=>{
  assert.equal(t("Collections","es"),"Colecciones");
  assert.equal(t("Collections","ru"),"Коллекции");
  assert.equal(t("Collections","zh"),"集合");
  assert.equal(t("Collections","pt"),"Coleções");
  assert.equal(t("Collections","fr"),"Collections");
});
test("dynamic status messages interpolate without translating user-provided data",()=>{
  assert.match(t("Request completed. HTTP 201 · 15 ms","ru"),/201/);
  assert.match(t("Petición guardada en MiColeccion","fr"),/MiColeccion/);
  assert.match(t("Delete variable password?","zh"),/password/);
  assert.equal(t("Authorization: Bearer my-secret","ru"),"Authorization: Bearer my-secret");
  assert.equal(t("https://api.example.org/v1/users","zh"),"https://api.example.org/v1/users");
});
test("HTML source is in English and includes accessible language menu",()=>{
  const html=readFileSync(new URL("../index.html",import.meta.url),"utf8");
  const css=readFileSync(new URL("../styles.css",import.meta.url),"utf8");
  assert.match(html,/<html lang="en">/);
  assert.match(html,/id="languageTrigger"/);
  assert.match(html,/id="languageMenu"[^>]+role="listbox"/);
  assert.match(css,/\.language-option\[aria-selected="true"\]/);
  assert.doesNotMatch(html,/>Guardar petición actual</);
  assert.match(html,/>Save current request</);
});
