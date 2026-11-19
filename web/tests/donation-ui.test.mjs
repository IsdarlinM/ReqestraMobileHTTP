import test from "node:test";
import assert from "node:assert/strict";
import {readFileSync} from "node:fs";
import {t} from "../lib/i18n.mjs";

const read=path=>readFileSync(new URL(path,import.meta.url),"utf8");
const html=read("../index.html");
const css=read("../styles.css");
const app=read("../app.mjs");
const ui=read("../lib/ui.mjs");

test("Donation button uses a fixed-size SVG instead of a font-dependent heart",()=>{
  const header=html.match(/<header class="topbar">([\s\S]*?)<\/header>/)?.[1];
  assert.ok(header,"Missing topbar");
  const donate=header.match(/<button id="donateNav"([\s\S]*?)<\/button>/)?.[0];
  assert.ok(donate,"Missing donation action");
  assert.match(donate,/data-section="support"/);
  assert.match(donate,/class="donate-cta-icon" viewBox="0 0 24 24"/);
  assert.match(donate,/stroke="currentColor"/);
  assert.match(donate,/aria-hidden="true"/);
  assert.doesNotMatch(donate,/<span[^>]*>♥<\/span>/);
  assert.match(css,/\.donate-cta-icon\{[\s\S]*?inline-size:20px;block-size:20px/);
  assert.match(css,/\.donate-cta\{[\s\S]*?height:44px/);
});

test("Donation button preserves its compact 44px hit area without squashing the icon",()=>{
  assert.match(css,/@media\(max-width:480px\)\{[\s\S]*?\.donate-cta\{width:44px;min-width:44px;height:44px;padding:0/);
  assert.match(css,/\.donate-cta-icon\{[\s\S]*?flex:0 0 20px/);
  assert.match(css,/\.donate-cta-label\{display:none\}/);
  assert.match(css,/@media\(min-width:751px\) and \(max-width:1150px\)/);
});

test("Theme icon uses accessible named SVG states and adapts to six locales",()=>{
  assert.match(html,/class="theme-icon theme-icon-sun"/);
  assert.match(html,/class="theme-icon theme-icon-moon"/);
  assert.match(css,/\[data-theme="light"\] \.theme-icon-moon\{display:block\}/);
  assert.match(app,/function refreshAppearanceControl\(\)/);
  assert.match(app,/button\.setAttribute\("aria-label",label\)/);
  for(const code of ["en","es","ru","zh","pt","fr"]){
    assert.ok(t("Modo claro",code).length>0);
    assert.ok(t("Modo oscuro",code).length>0);
  }
});

test("Donation opens the existing support page and only official PayPal processes payments",()=>{
  assert.match(html,/<section id="support"/);
  assert.match(ui,/www\.paypal\.com\/donate\/\?hosted_button_id=/);
  assert.match(ui,/button\.removeAttribute\("aria-disabled"\)/);
  assert.match(ui,/button\.removeAttribute\("href"\)/);
});
