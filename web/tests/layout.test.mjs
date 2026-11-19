import test from "node:test";
import assert from "node:assert/strict";
import {readFileSync} from "node:fs";

const read = path => readFileSync(new URL(path,import.meta.url),"utf8");
const css=read("../styles.css");
const android=read("../../app/src/main/java/com/imr/reqestra/presentation/ReqestraApp.kt");
const workspace=read("../../app/src/main/java/com/imr/reqestra/presentation/workspace/WorkspaceScreen.kt");
const response=read("../../app/src/main/java/com/imr/reqestra/presentation/response/ResponseScreen.kt");
const session=read("../../app/src/main/java/com/imr/reqestra/presentation/session/SessionScreen.kt");
const history=read("../../app/src/main/java/com/imr/reqestra/presentation/history/HistoryScreen.kt");
const collections=read("../../app/src/main/java/com/imr/reqestra/presentation/collections/CollectionsScreen.kt");
const automation=read("../../app/src/main/java/com/imr/reqestra/presentation/automation/AutomationScreen.kt");
const discovery=read("../../app/src/main/java/com/imr/reqestra/presentation/discovery/DiscoveryScreen.kt");

test("Mobile Web header prioritizes three visible 44px actions over branding",()=>{
  assert.match(css,/\.topbar-right>\*\{flex-shrink:0\}/);
  assert.match(css,/@media\(max-width:480px\)/);
  assert.match(css,/\.topbar-brand>span\{display:none\}/);
  assert.match(css,/\.topbar-right \.icon-button,[\s\S]*?\.topbar-right \.language-trigger\{width:44px;height:44px;min-width:44px\}/);
  assert.match(css,/\.topbar-right \.donate-cta-label\{display:none\}/);
  assert.match(css,/\.language-menu\{max-width:calc\(100vw - 24px\)/);
});

test("Mobile Web forms, menus, actions and lists wrap instead of overlapping",()=>{
  assert.match(css,/\.actions\{display:grid;grid-template-columns:repeat\(2,minmax\(0,1fr\)\)/);
  assert.match(css,/\.actions\{grid-template-columns:minmax\(0,1fr\)\}/);
  assert.match(css,/\.list-item\{flex-wrap:wrap/);
  assert.match(css,/\.list-actions\{flex:0 1 auto;flex-wrap:wrap/);
  assert.match(css,/\.panel-heading,\.page-heading\{min-width:0;flex-wrap:wrap\}/);
  assert.match(css,/\.mobile-more\{max-height:min\(65dvh,450px\);overflow-y:auto/);
  assert.match(css,/main\{padding-bottom:calc\(130px \+ env\(safe-area-inset-bottom\)\)\}/);
  assert.match(css,/@media\(max-width:340px\)/);
});

test("Android top bar hides secondary text before it can displace the controls",()=>{
  assert.match(android,/val compactLayout = screenWidthDp < 600/);
  assert.match(android,/val iconOnlyBrand = screenWidthDp < 390/);
  assert.match(android,/if \(!iconOnlyBrand\) Column\(modifier = Modifier.weight\(1f\)\)/);
  assert.match(android,/modifier = Modifier.size\(44.dp\)/);
  assert.match(android,/screenWidthDp < 360/);
  for(const essential of ["Icons.Outlined.Language","AppSection.SUPPORT","onClick = onToggleTheme"])
    assert.ok(android.includes(essential),essential);
});

test("Android places potentially conflicting buttons on separate lines",()=>{
  assert.match(workspace,/Column\(verticalArrangement = Arrangement.spacedBy\(8.dp\)\) \{\s*Button\(/);
  assert.match(workspace,/FlowRow\(\s*modifier = Modifier.fillMaxWidth\(\),\s*horizontalArrangement = Arrangement.spacedBy\(10.dp\)/);
  assert.match(session,/Column\(verticalArrangement = Arrangement.spacedBy\(8.dp\)\) \{\s*Button\(/);
  assert.match(history,/modifier = Modifier.align\(Alignment.End\)/);
  assert.match(response,/maxLines = 3,\s*overflow = TextOverflow.Ellipsis/);
  assert.match(response,/modifier = Modifier.align\(Alignment.End\)/);
  assert.match(collections,/modifier = Modifier.align\(Alignment.End\)/);
  assert.match(automation,/maxLines = 2, overflow = TextOverflow.Ellipsis/);
  assert.match(discovery,/modifier = Modifier.weight\(1f\)/);
});

test("Layout fixes do not change the public version or remove the donation option",()=>{
  const gradle=read("../../app/build.gradle.kts");
  const pkg=JSON.parse(read("../package.json"));
  const html=read("../index.html");
  assert.match(gradle,/versionName = "3\.5\.0"/);
  assert.equal(pkg.version,"3.5.0");
  assert.match(html,/id="donateNav"/);
  assert.match(html,/id="languageTrigger"/);
  assert.match(html,/id="themeToggle"/);
});
