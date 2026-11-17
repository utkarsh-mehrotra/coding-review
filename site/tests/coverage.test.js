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

test("onData throwing reaches onError", async () => {
  _reset();
  const errs = [];
  loadCoverage(fakeFetch(), () => { throw new Error("render"); }, (e) => errs.push(e.message));
  await settle();
  assert.deepEqual(errs, ["render"]);
});

test("retries after a failed fetch", async () => {
  _reset();
  let n = 0, errs = 0, got = 0;
  const f = (url) => { n++; return n === 1 ? Promise.reject(new Error("net")) : fakeFetch()(url); };
  await loadCoverage(f, () => got++, () => errs++);
  await loadCoverage(f, () => got++, () => errs++);
  await loadCoverage(f, () => got++, () => errs++);
  assert.deepEqual([n, errs, got], [2, 1, 1]);
});

test("non-OK response reaches onError", async () => {
  _reset();
  let err = null;
  await loadCoverage(() => Promise.resolve({ ok: false, status: 404 }), () => {}, (e) => { err = e; });
  assert.match(err.message, /404/);
});

test("fetch URL carries the version", async () => {
  _reset();
  const f = fakeFetch();
  const doc = fakeDoc();
  doc.getElementById = () => ({ getAttribute: () => "abc123def456" });
  wire(doc, { hash: "#coverage" }, f, () => {}, () => {});
  await settle();
  assert.deepEqual(f.calls, ["coverage.json?v=abc123def456"]);
});
