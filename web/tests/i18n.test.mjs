import test from "node:test";
import assert from "node:assert/strict";
import {LOCALES,normalizeLocale,deviceLocale,t} from "../lib/i18n.mjs";

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
