"""Fetch the question numbers of LeetCode's curated study plans (Top Interview 150, LeetCode 75). Cached; never refetches.
Usage: python3 tools/lc-coverage/fetch_lists.py tools/lc-coverage/data/lists.json
Delete the output file first to force a refetch (make fetch-lists does this).
"""
import datetime, json, os, sys, time, urllib.request

out = sys.argv[1]
if os.path.exists(out):
    print('cached:', out); sys.exit(0)

Q = "query s($slug:String!){studyPlanV2Detail(planSlug:$slug){slug name planSubGroups{questions{questionFrontendId}}}}"
PLANS = ['top-interview-150', 'leetcode-75']


def plan(slug):
    body = json.dumps({'query': Q, 'variables': {'slug': slug}}).encode()
    req = urllib.request.Request('https://leetcode.com/graphql', data=body, headers={
        'Content-Type': 'application/json', 'User-Agent': 'Mozilla/5.0',
        'Referer': 'https://leetcode.com/studyplan/'})
    for attempt in range(4):
        try:
            with urllib.request.urlopen(req, timeout=30) as r:
                return json.load(r)['data']['studyPlanV2Detail']
        except Exception as e:
            print(f'  retry {attempt + 1} for {slug}: {e}'); time.sleep(5 * (attempt + 1))
    raise SystemExit(f'failed for {slug}')


result = {}
for i, slug in enumerate(PLANS):
    if i:
        time.sleep(1)
    d = plan(slug)
    ids = sorted({int(q['questionFrontendId']) for g in d['planSubGroups'] for q in g['questions']})
    result[slug] = {'name': d['name'], 'slug': d['slug'], 'ids': ids}
    print(f'{slug}: {d["name"]}, {len(ids)} ids')
result['fetched'] = datetime.date.today().isoformat()
with open(out, 'w') as fh:
    json.dump(result, fh, indent=1)
    fh.write('\n')
