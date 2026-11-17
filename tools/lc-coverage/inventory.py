"""Step 1: inventory templates, LC references in code, practice maps, decisions.
Usage: python3 tools/lc-coverage/inventory.py <repo_root> <out.json>
"""
import json, re, sys, glob, os

root, out = sys.argv[1], sys.argv[2]
LC = re.compile(r'LC ?(\d+)\b((?:\s*[/,]\s*\d+\b(?![A-Za-z]))*)')
MEMBER = re.compile(r'^\s*(?:public |private |protected )?(?:static )?(?:final )?'
                    r'(?:(class|interface|record|enum)\s+(\w+)|[\w<>\[\],.? ]+?\s+(\w+)\s*\()')
KEYWORDS = {'if', 'for', 'while', 'switch', 'return', 'new', 'catch', 'else', 'do', 'try'}


def ids_in(line):
    res = []
    for m in LC.finditer(line):
        res.append(int(m.group(1)))
        res += [int(x) for x in re.findall(r'\d+', m.group(2))]
    return res


templates = {}   # "File#member" -> {topic, file, member, lc:set, line}
refs = []        # (id, template, how)
for path in sorted(glob.glob(os.path.join(root, 'topics/*/*.java'))):
    topic = path.split(os.sep)[-2]
    fname = os.path.basename(path)[:-5]
    pending, current, depth, member_depth = [], None, 0, None
    for no, line in enumerate(open(path, encoding='utf-8'), 1):
        stripped = line.strip()
        # top-level members live at depth 1 (inside the public class)
        m = MEMBER.match(line) if depth == 1 and not stripped.startswith('//') else None
        if m:
            name = m.group(2) or m.group(3)
            if name and name not in KEYWORDS and name != fname:
                key = f'{fname}#{name}'
                templates.setdefault(key, {'topic': topic, 'file': fname, 'member': name,
                                           'line': no, 'lc': set()})
                current = key
                for i in pending:
                    templates[key]['lc'].add(i); refs.append((i, key, 'comment-above'))
                pending = []
        found = ids_in(line)
        if found:
            if stripped.startswith('//') and depth == 1:
                pending += found               # comment block above the next member
            elif current and depth >= 2:
                for i in found:                # comment inside a member body
                    templates[current]['lc'].add(i); refs.append((i, current, 'in-body'))
            elif current and m:
                for i in found:                # trailing comment on the signature line
                    templates[current]['lc'].add(i); refs.append((i, current, 'signature'))
            else:
                pending += found
        code = re.sub(r'//.*', '', line)
        code = re.sub(r'"(\\.|[^"\\])*"', '""', code)
        code = re.sub(r"'(\\.|[^'\\])'", "''", code)
        depth += code.count('{') - code.count('}')

practice, decisions, topics = [], [], {}
for path in sorted(glob.glob(os.path.join(root, 'topics/*/topic.json'))):
    d = json.load(open(path, encoding='utf-8'))
    tid = path.split(os.sep)[-2]
    topics[tid] = d['title']
    for group, items in d.get('practice', []):
        for it in items:
            m = re.match(r'\s*(\d+)\s+(.*)', it)
            if m:
                practice.append({'id': int(m.group(1)), 'label': m.group(2), 'topic': tid, 'group': group})
    for dec in d.get('decisions', []):
        decisions.append({'topic': tid, 'q': re.sub('<[^>]+>', '', dec['q']), 'a': dec.get('a', ''),
                          'template': dec.get('template', '')})

for t in templates.values():
    t['lc'] = sorted(t['lc'])
json.dump({'topics': topics, 'templates': templates, 'refs': refs,
           'practice': practice, 'decisions': decisions}, open(out, 'w'), indent=1)

code_ids = {i for i, _, _ in refs}
prac_ids = {p['id'] for p in practice}
print(f'templates/members: {len(templates)} in {len({t["file"] for t in templates.values()})} files')
print(f'members with LC refs: {sum(1 for t in templates.values() if t["lc"])}')
print(f'distinct LC ids in code: {len(code_ids)}; in practice maps: {len(prac_ids)}; union: {len(code_ids | prac_ids)}')
