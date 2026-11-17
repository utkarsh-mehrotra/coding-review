"""Step 2: fetch LeetCode's public problem list (metadata only). Cached; never refetches.
Usage: python3 tools/lc-coverage/fetch_lc.py tools/lc-coverage/data/lc_all.json
Delete the output file first to force a refetch (make fetch-lc does this).
"""
import json, os, sys, time, urllib.request

out = sys.argv[1]
if os.path.exists(out):
    print('cached:', out); sys.exit(0)

Q = """query q($skip:Int,$limit:Int){ questionList(categorySlug:"",limit:$limit,skip:$skip,filters:{}){
  totalNum data{ questionFrontendId title titleSlug difficulty isPaidOnly acRate categoryTitle topicTags{ slug name } } } }"""


def page(skip, limit=100):
    body = json.dumps({'query': Q, 'variables': {'skip': skip, 'limit': limit}}).encode()
    req = urllib.request.Request('https://leetcode.com/graphql', data=body, headers={
        'Content-Type': 'application/json', 'User-Agent': 'Mozilla/5.0',
        'Referer': 'https://leetcode.com/problemset/'})
    for attempt in range(4):
        try:
            with urllib.request.urlopen(req, timeout=30) as r:
                return json.load(r)['data']['questionList']
        except Exception as e:
            print(f'  retry {attempt + 1} at skip={skip}: {e}'); time.sleep(5 * (attempt + 1))
    raise SystemExit(f'failed at skip={skip}')


first = page(0)
total, rows = first['totalNum'], list(first['data'])
for skip in range(100, total, 100):
    time.sleep(1)
    rows += page(skip)['data']
    if skip % 1000 == 0:
        print(f'  {len(rows)}/{total}')
json.dump(rows, open(out, 'w'))
print(f'fetched {len(rows)} of {total}')
