
# sudo pip install tabulate

from tabulate import tabulate
import sys

hasHead = sys.argv[1].lower() if len(sys.argv) > 1 else 'false'
sep = sys.argv[2] if len(sys.argv) > 2 else '\t'

res = []
_header = []
for line in sys.stdin:
    arr = line.strip().split(sep)
    if hasHead == 'true':
        _header = arr
        hasHead = 'False'
        continue
    res.append(arr)

print tabulate(res, headers=_header, tablefmt='orgtbl')
