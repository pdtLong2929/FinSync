"""Static integrity checks for the Data & Logic specification and PNG exports."""
import json
import re
import struct
import zlib
from pathlib import Path

here = Path(__file__).resolve().parent
root = here.parents[1]
doc_path = root / 'use-cases' / 'D_usecase.md'
doc = doc_path.read_text(encoding='utf-8')
data = json.loads((here / 'screens.json').read_text(encoding='utf-8-sig'))
errors = []
expected = {f'UC-DL-{n:02d}' for n in [2, 5, 6, 7, 8, 12, 13, 14]}
actual = {c['id'] for c in data['cases'] if c['id'].startswith('UC-')}
if actual != expected:
    errors.append('Use-case catalogue mismatch')
for path in re.findall(r'\]\(([^)]+)\)', doc):
    if path.startswith(('http:', 'https:', '#')):
        continue
    target = path.split('#')[0]
    if not (doc_path.parent / target).exists():
        errors.append('Missing link: ' + target)
lines = doc.splitlines()
for i, line in enumerate(lines):
    if re.match(r'^#{1,6} ', line):
        if i + 1 >= len(lines) or not lines[i+1].startswith('> *Performed by:'):
            errors.append('Missing attribution: ' + line)
if 'TODO' in doc:
    errors.append('Unresolved TODO in generated specification')
if doc.count('```') % 2:
    errors.append('Unbalanced code fences')
screen_count = 0
board_count = 0
section = 1
for c in data['cases']:
    if c['id'].startswith('UC-'):
        section += 1
        for part in range(1, 6):
            if f'### D.{section}.{part}.' not in doc:
                errors.append(f'Missing template subsection {c["id"]}.{part}')
        if c['id'] not in re.findall(r'u\d+\(\["(UC-DL-\d+):', doc):
            errors.append('Use case absent from Mermaid: ' + c['id'])
    screens = {s['id'] for s in c['screens']}
    if len(screens) != len(c['screens']):
        errors.append('Duplicate screen ID: ' + c['id'])
    mapped = []
    for board in c['boards']:
        board_count += 1
        mapped.extend(board['screen_ids'])
        path = root / 'screenshots/prototypes/data-logic' / board['filename']
        raw = path.read_bytes()
        if raw[:8] != b'\x89PNG\r\n\x1a\n':
            errors.append('Invalid PNG signature: ' + board['filename'])
            continue
        width, height = struct.unpack('>II', raw[16:24])
        offset = 8
        compressed = bytearray()
        while offset < len(raw):
            size = struct.unpack('>I', raw[offset:offset+4])[0]
            kind = raw[offset+4:offset+8]
            payload = raw[offset+8:offset+8+size]
            crc = struct.unpack('>I', raw[offset+8+size:offset+12+size])[0]
            if zlib.crc32(kind + payload) & 0xffffffff != crc:
                errors.append('PNG CRC failure: ' + board['filename'])
            if kind == b'IDAT':
                compressed.extend(payload)
            offset += 12 + size
        pixels = zlib.decompress(compressed)
        if width != 1440 or height < 800 or len(set(pixels)) < 10:
            errors.append('Unexpected or blank PNG: ' + board['filename'])
        if f'](../screenshots/prototypes/data-logic/{board["filename"]})' not in doc:
            errors.append('Board not embedded: ' + board['filename'])
    if set(mapped) != screens or len(mapped) != len(screens):
        errors.append('Screen/board coverage mismatch: ' + c['id'])
    if c['id'].startswith('UC-'):
        from build_compact import BRANCHES
        block = doc.split(f'## D.{section}. ')[1].split('\n## ')[0]
        for i, branch in enumerate(BRANCHES[c['n']], 1):
            if f'AF-{i:02d}' not in block:
                errors.append('Missing domain branch: ' + c['id'] + str(i))
        for screen in ['S01', 'S02', 'S03', 'S04', 'S05', 'S06']:
            if screen not in block:
                errors.append('Missing frame reference: ' + c['id'] + screen)
    screen_count += len(screens)
result = {'cases': len(actual), 'prototype_states': screen_count,
          'png_boards': board_count, 'errors': errors,
          'scope': 'Static document/link/prototype integrity only; no backend or browser scenario tests.'}
(here / 'validation.json').write_text(json.dumps(result, indent=2), encoding='utf-8')
print(json.dumps(result, indent=2))
raise SystemExit(bool(errors))
