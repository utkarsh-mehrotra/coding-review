const test = require("node:test");
const assert = require("node:assert/strict");
const { loadCoverage, wire, _reset } = require("../assets/coverage.js");

function fakeFetch() {
  const calls = [];
  const fn = (url) => { calls.push(url); return Promise.resolve({ ok: true, json: () => Promise.resolve({ rows: [] }) }); };
  fn.calls = calls;
  return fn;
}
function fakeDoc() {
  const h = {};
  return { addEventListener: (t, f) => { h[t] = f; }, fire: (name) => h["cr:view"]({ detail: { name } }) };
}
const settle = () => new Promise((r) => setImmediate(r));

test("coverage loads on initial hash", async () => {
  _reset();
  const f = fakeFetch(), got = [];
  wire(fakeDoc(), { hash: "#coverage" }, f, (d) => got.push(d), () => {});
  await settle();
  assert.deepEqual(f.calls, ["coverage.json"]);
  assert.equal(got.length, 1);
});

test("other views do not fetch", async () => {
  _reset();
  const f = fakeFetch(), doc = fakeDoc();
  wire(doc, { hash: "#overview" }, f, () => {}, () => {});
  doc.fire("overview");
  await settle();
  assert.equal(f.calls.length, 0);
});

test("loads once", async () => {
  _reset();
  const f = fakeFetch(), doc = fakeDoc(), got = [];
  wire(doc, { hash: "" }, f, (d) => got.push(d), () => {});
  doc.fire("coverage");
  doc.fire("coverage");
  await settle();
  assert.equal(f.calls.length, 1);
  assert.equal(got.length, 1);
});

test("fetch failure reports an error", async () => {
  _reset();
  let err = 0;
  loadCoverage(() => Promise.reject(new Error("blocked")), () => {}, () => { err++; });
  await settle();
  assert.equal(err, 1);
});
