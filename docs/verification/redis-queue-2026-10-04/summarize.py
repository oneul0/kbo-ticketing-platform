"""Rebuild summary.json from the preserved JUnit XML evidence (Python stdlib only)."""
from collections import Counter, defaultdict
import json
from pathlib import Path
import re
import statistics
import xml.etree.ElementTree as ET

root_dir = Path(__file__).resolve().parent
summary = {}
for path in sorted(root_dir.glob('*.xml')):
    root = ET.parse(path).getroot()
    output = root.findtext('system-out') or ''
    rows = Counter(re.findall(
        r'QUEUE_RESULT scenario=(\S+) accepted=(\d+) rejected=(\d+) failed=(\d+) size=(\d+) counter=(\d+)', output))
    timings = defaultdict(list)
    for helper, users, requests, ms in re.findall(
            r'QUEUE_BATCH helperOnly=(\w+) distinctUsers=(\d+) requests=(\d+) elapsedMs=([\d.]+)', output):
        timings[f'helperOnly={helper},distinctUsers={users},requests={requests}'].append(float(ms))
    summary[path.stem] = {
        'suite': {key: root.get(key) for key in ['tests', 'failures', 'errors', 'skipped', 'timestamp', 'time']},
        'outcomes': [dict(zip(['scenario', 'accepted', 'rejected', 'failed', 'size', 'counter'], row), repetitions=count)
                     for row, count in sorted(rows.items())],
        'batch_ms': {key: {'samples': len(values), 'min': min(values),
                           'median': statistics.median(values), 'max': max(values)}
                     for key, values in sorted(timings.items())},
    }
(root_dir / 'summary.json').write_text(json.dumps(summary, indent=2) + '\n')
print(json.dumps(summary, indent=2))
