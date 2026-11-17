"""Outline of a Java template file: the depth-1 members (methods and nested classes) of its public class.

Brace depth is counted after stripping comments, string literals and char literals, the same
approach as tools/lc-coverage/inventory.py.
"""
import re
from collections import namedtuple

Member = namedtuple("Member", "name kind start end private doc_start")

DECL = re.compile(
    r"^\s*(?:@\w+\s+)*(?:(?:public|private|protected|static|final|abstract|synchronized)\s+)*"
    r"(?:(?P<kind>class|interface|record|enum)\s+(?P<cname>\w+)"
    r"|[\w<>\[\],.? ]+?\s+(?P<mname>\w+)\s*\()"
)
NOT_NAMES = {"if", "for", "while", "switch", "return", "new", "catch", "else", "do", "try", "throw", "synchronized"}


def _strip(line, in_block):
    """Return (code, in_block): line with comments, strings and chars removed."""
    out, i, n = [], 0, len(line)
    while i < n:
        if in_block:
            j = line.find("*/", i)
            if j < 0:
                return "".join(out), True
            i, in_block = j + 2, False
            continue
        c = line[i]
        if line.startswith("//", i):
            break
        if line.startswith("/*", i):
            in_block = True
            i += 2
        elif c in "\"'":
            j = i + 1
            while j < n and line[j] != c:
                j += 2 if line[j] == "\\" else 1
            out.append(c + c)
            i = j + 1
        else:
            out.append(c)
            i += 1
    return "".join(out), in_block


def outline(src):
    lines = src.split("\n")
    members, depth, in_block = [], 0, False
    cur = None  # [name, kind, start, private]
    for no, raw in enumerate(lines, 1):
        code, was_block = _strip(raw, in_block)
        starts_in_comment = in_block
        in_block = was_block
        if cur is None and depth == 1 and not starts_in_comment:
            m = DECL.match(code)
            if m:
                name = m.group("cname") or m.group("mname")
                head = code.split("(")[0]
                if name and name not in NOT_NAMES and "=" not in head:
                    cur = [name, "class" if m.group("kind") else "method", no,
                           re.search(r"\bprivate\b", code) is not None, depth]
        depth += code.count("{") - code.count("}")
        if cur is not None:
            if "{" in code and len(cur) == 5:
                cur.append(True)  # body entered
            if depth <= cur[4] and (len(cur) > 5 or ";" in code):
                if len(cur) > 5:
                    members.append(_member(lines, cur, no))
                cur = None
    return members


def _member(lines, cur, end):
    name, kind, start, private = cur[:4]
    doc = start
    while doc > 1 and lines[doc - 2].strip().startswith("//"):
        doc -= 1
    return Member(name, kind, start, end, private, doc)


def anchor_id(file_stem, member):
    return "m-" + file_stem + "-" + member


def group_id(file_stem, member):
    return "lc-" + file_stem + "-" + member
