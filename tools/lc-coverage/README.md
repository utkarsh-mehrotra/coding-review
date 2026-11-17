# LeetCode coverage

Maps every LeetCode algorithm problem to the template in this repo that solves it.
The site shows the result under each template (tiers A and B only).

| Step | Command | Network |
|---|---|---|
| Remap after editing templates or `topic.json` | `make problems` | no |
| Refresh the LeetCode problem list | `make fetch-lc` | about 42 requests, 1 per second |
| Refresh the Top Interview 150 and LeetCode 75 lists | `make fetch-lists` | 2 requests, 1 per second |

## Files
| Path | Contents |
|---|---|
| `inventory.py` | reads every template member and its `// LC nnn` comments, plus practice maps and decision entries |
| `fetch_lc.py` | fetches LeetCode's public problem list (number, title, difficulty, premium flag, tags, acceptance) |
| `fetch_lists.py` | fetches the question numbers of LeetCode's Top Interview 150 and LeetCode 75 study plans |
| `match.py` | assigns each problem a template and a tier; the rules are an ordered list, most specific first |
| `data/lc_all.json` | cached LeetCode list, so `make problems` runs offline |
| `data/lists.json` | cached list membership (question numbers, fetch date); `match.py` adds a `lists` field to each problem |
| `data/problems.json` | the result, one problem per line; read by `site/build.py` |

## Tiers
- **A, verified**: the problem number appears in a template comment or a practice map.
- **B, tag match**: a specific LeetCode tag or title keyword points at one template (`union-find` → `DSU#union`).
- **C, likely**: only a broad tag matched (dynamic programming, greedy, hash table). Not shown on the site.
- **U, not covered**: no rule matched.

To move a problem from B to A, add `// LC nnn` above the template method that solves it and run `make problems`.
