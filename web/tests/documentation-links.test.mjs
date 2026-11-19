import test from "node:test";
import assert from "node:assert/strict";
import {existsSync, readFileSync, readdirSync} from "node:fs";
import {dirname, join, resolve} from "node:path";
import {fileURLToPath} from "node:url";

const root = fileURLToPath(new URL("../../", import.meta.url));
const read = (path) => readFileSync(join(root, path), "utf8");
const docFiles = [
  "README.md", "SECURITY.md", "TEST_REPORT.md", "web/README.md",
  ...readdirSync(join(root, "docs"))
    .filter((name) => name.endsWith(".md"))
    .map((name) => "docs/" + name),
];
const legacyNames = [
  "GUIA_DE_USO.md",
  "INTERFAZ_RESPONSIVA.md",
  "INTERNACIONALIZACION_Y_EXTENSIONS.md",
  "PLAN_VERSION_3.5.1.md",
];

test("Markdown documentation has no links to deleted or renamed files", () => {
  for (const file of docFiles) {
    const content = read(file);
    for (const oldName of legacyNames) {
      assert.ok(!content.includes(oldName), file + " refers to " + oldName);
    }
    for (const match of content.matchAll(/\[[^\]\n]*\]\(([^)]+)\)/g)) {
      const href = match[1].trim().replace(/^<|>$/g, "").split(/\s+["']/)[0];
      if (!href || /^(?:[a-z][a-z0-9+.-]*:|#|\/\/)/i.test(href)) continue;
      const localPath = decodeURIComponent(href.split("#", 1)[0]);
      if (!localPath) continue;
      const resolved = resolve(root, dirname(file), localPath);
      assert.ok(existsSync(resolved), file + " has broken Markdown link " + href);
    }
  }
});

test("Android and web help pages use the English documentation filename", () => {
  const mobile = read("app/src/main/java/com/imr/reqestra/presentation/about/AboutScreen.kt");
  const web = read("web/index.html");
  const aboutTest = read("web/tests/about.test.mjs");
  for (const [name, source] of [["Android help", mobile], ["Web help", web], ["Web help test", aboutTest]]) {
    assert.ok(source.includes("USER_GUIDE"), name + " must reference USER_GUIDE.md");
    assert.ok(!source.includes("GUIA_DE_USO"), name + " still uses the old filename");
  }
  assert.match(read("docs/USER_GUIDE.md"), /^# Reqestra User Guide/m);
});

test("Documentation-only renames do not change Android or web versions", () => {
  const gradle = read("app/build.gradle.kts");
  const pkg = JSON.parse(read("web/package.json"));
  assert.match(gradle, /versionName\s*=\s*"3\.5\.1"/);
  assert.match(gradle, /versionCode\s*=\s*10/);
  assert.equal(pkg.version, "3.5.1");
});
