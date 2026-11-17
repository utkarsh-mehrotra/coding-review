# coding-review

Java templates and an interactive reference page for FAANG coding-interview review (SDE2 / SDE3 / Staff).
Coding rounds only: no system design or behavioral prep.

**Live site:** https://coding-review-iota.vercel.app

**Rule: every pattern has a template.** Paste-ready Java, tested, with the decision tree pointing at it.
`make check` fails if a decision-tree entry does not link to a real `File#method` in a template.

## Quick start
```
make test        # compile all templates, run all test suites
make check       # template-first rule: every decision links to a real template member
make site        # regenerate site/index.html from topics/*/topic.json + the Java files
make problems    # remap LeetCode problems to templates (offline)
make test-site   # site tests: Python (build) and Node (search, coverage)
make serve       # open http://localhost:8000 (the LeetCode Coverage view needs this, not file://); separate from make all
make all         # test, problems, check, site and test-site
```

## Layout
| Path | Contents |
|---|---|
| `topics/<topic>/*.java` | class-named template files (default package, static methods, no dependencies) |
| `topics/<topic>/topic.json` | views, decision-tree entries (each with a `template` of the form `File#member`), level calibration, practice map |
| `topics/<topic>/tests/` | known-input tests plus seeded randomized cross-checks against brute force |
| `topics/graphs/views/` | hand-written page prose for the graph views; code cards are filled in from the Java files via `{{code:File.java}}` |
| `common/Check.java` | tiny dependency-free test harness |
| `site/build.py`, `site/assets/` | page generator, shared CSS and JS |
| `site/index.html` | generated single-page reference (do not edit by hand) |
| `docs/PLAN.md` | scope, conventions, roadmap |
| `tools/lc-coverage/` | maps every LeetCode problem to a template; see its README |
| `vercel.json` | Vercel config: serves `site/` as static files, no build step (`.vercelignore` keeps tests and build scripts out of the deploy) |

## Deploy
The site is hosted on Vercel at https://coding-review-iota.vercel.app. `site/index.html` is generated, so run `make site` and commit the result before deploying.
```
npx vercel --prod   # deploy site/ to production
```

GitHub Actions runs the tests, the template check, the site tests and a stale-generated-files check on every push and pull request. Vercel deploys `main` on every push, and a red CI run does not block that deploy, so check the Actions tab before relying on a push.

## Topics (15)
Graphs, Dynamic Programming, Trees & Range Structures, Arrays & Hashing, Sliding Window & Two Pointers,
Monotonic Stack & Deque, Heaps & Top-K, Intervals & Sweep, Binary Search, Backtracking, Greedy, Linked Lists,
String Algorithms, Bits & Math, Data-Structure Design. 51 template files, 232 decision-tree entries, 52 page views.

## LeetCode coverage
2,196 of LeetCode's 3,696 algorithm problems map to a template: 522 named in template comments or practice maps,
1,674 matched by a specific LeetCode tag. Each template view lists its problems under the code, the filter box
finds a template by problem number or title, and the **LeetCode Coverage** view (`#coverage`) has the full
searchable list with gaps. `make check` fails if a listed problem points at a missing method.
Details and tiers: [`tools/lc-coverage/README.md`](tools/lc-coverage/README.md).

## Tests
`make test` runs 11 suites (about 132,000 checks): LeetCode examples for every template, plus seeded randomized
cross-checks where a brute force exists (LIS, knapsack, coin change, edit distance, Fenwick / segment / lazy tree /
sparse table, KMP / Z / Rabin-Karp / Manacher, quickselect, binary search bounds, sliding window max, histogram,
interval merge, LRU vs LinkedHashMap, and more).

## Not built yet
- drill log and spaced-review loop

## Conventions
- One public class per file, file named after the class, so every template compiles with plain `javac`.
- DP templates are top-down memoization only. Recursive templates (DP memo, SCC/bridges DFS) need a big-stack thread on inputs deeper than about 10^4 (`runWithBigStack`).
- Complexity and "RECOGNIZE" comments live next to the code they describe; pitfalls live in `topic.json`.
