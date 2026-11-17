const test = require("node:test");
const assert = require("node:assert/strict");
const { matchesSidebar, rank } = require("../assets/search.js");

test("exact number", () => {
  assert.equal(matchesSidebar("70 Climbing Stairs 1070 Foo", "70"), true);
  assert.equal(matchesSidebar("1070 Foo 170 Bar", "70"), false);
  assert.equal(matchesSidebar("1 Two Sum", "1"), true);
  assert.equal(matchesSidebar("11 Container", "1"), false);
});

test("words", () => {
  assert.equal(matchesSidebar("70 Climbing Stairs", "70 climbing"), true);
  assert.equal(matchesSidebar("70 Climbing Stairs", "stairs 70"), true);
  assert.equal(matchesSidebar("70 Climbing Stairs", "70 coins"), false);
  assert.equal(matchesSidebar("Union-Find", "UNION"), true);
});

test("rank puts exact problem number first", () => {
  const idx = [
    ["1743 Restore", "", "v", "a", "problem"],
    ["743 Network Delay Time", "", "v", "b", "problem"],
  ];
  assert.equal(rank(idx, "743")[0][0], "743 Network Delay Time");
});

test("rank number 1 does not match 11 or 1070", () => {
  const idx = [["11 Container", "", "v", "a", "problem"], ["1 Two Sum", "", "v", "b", "problem"]];
  const out = rank(idx, "1");
  assert.equal(out.length, 1);
  assert.equal(out[0][0], "1 Two Sum");
});

test("rank prefers views and templates on ties", () => {
  const idx = [
    ["200 DSU Island", "x", "v", "a", "problem"],
    ["DSU#union", "Merge sets?", "v", "b", "template"],
    ["Union-Find (DSU)", "Graphs", "v", "", "view"],
  ];
  const out = rank(idx, "dsu").map((e) => e[4]);
  assert.deepEqual(out, ["template", "view", "problem"]);
});
test("rank score order: starts-with before word-start before substring", () => {
  const idx = [
    ["xxdijkstra", "", "v", "", "view"],
    ["Shortest dijkstra", "", "v", "", "view"],
    ["Dijkstra", "", "v", "", "view"],
  ];
  assert.deepEqual(rank(idx, "dijk").map((e) => e[0]), ["Dijkstra", "Shortest dijkstra", "xxdijkstra"]);
});

test("rank limit", () => {
  const idx = [];
  for (let i = 0; i < 30; i++) idx.push(["Graph " + i, "", "v", "", "template"]);
  assert.equal(rank(idx, "graph").length, 12);
  assert.equal(rank(idx, "graph", 5).length, 5);
});

test("rank empty query returns nothing", () => {
  assert.equal(rank([["a", "", "v", "", "view"]], "  ").length, 0);
});

test("rank equal scores order by kind then label", () => {
  const idx = [
    ["Graph problem", "", "v", "", "problem"],
    ["Graph B", "", "v", "", "template"],
    ["Graph A", "", "v", "", "template"],
    ["Graph view", "", "v", "", "view"],
  ];
  assert.deepEqual(rank(idx, "graph").map((e) => e[0]), ["Graph view", "Graph A", "Graph B", "Graph problem"]);
});

test("expand rebuilds problem entries from the compact form", () => {
  const { expand } = require("../assets/search.js");
  const data = {
    e: [["Union-Find", "Graphs", "v", "", "view"]],
    t: [["DSU#find", "v", "g-DSU-find"]],
    p: [[684, "Redundant Connection", 0], [547, "Provinces", 0]],
  };
  assert.deepEqual(expand(data), [
    ["Union-Find", "Graphs", "v", "", "view"],
    ["684 Redundant Connection", "DSU#find", "v", "g-DSU-find", "problem"],
    ["547 Provinces", "DSU#find", "v", "g-DSU-find", "problem"],
  ]);
  assert.deepEqual(expand([["a", "", "v", "", "view"]]), [["a", "", "v", "", "view"]]);
  assert.equal(rank(expand(data), "684")[0][0], "684 Redundant Connection");
});
