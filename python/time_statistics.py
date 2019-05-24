from collections import namedtuple
import re


f=open('data')

def identify_item(line):
	for index, c in enumerate(line):
		if c == '<' or '0'<=c<='9':
			return line[:index]

def parse_time(line):
	hour = minute = 0
	matchObj = re.search('(?P<hour>\d+)h', line)
	if matchObj:
		hour = matchObj.group('hour')
	matchObj = re.search('(?P<minute>\d+)m', line)
	if matchObj:
		minute = matchObj.group('minute')
	return int(hour), int(minute)


# Time = recordtype('Time', 'hour, minute')
item_time_dict = {}
item='none'
for line in f:
	if not line.startswith(item):
		item = identify_item(line)
		item_time_dict[item] = {'hour':0, 'minute':0}
	hour, minute = parse_time(line)
	time = item_time_dict.get(item)
	tmp = time['minute'] + minute
	time['minute'] = tmp % 60
	time['hour'] += hour + tmp / 60

lst = []
for name in item_time_dict:
	time = item_time_dict[name]
	lst.append((time['hour'], time['minute'], name))
lst.sort()
lst.reverse()
for l in lst:
	print l[2],str(l[0])+'h'+str(l[1])+'m'
# print item_time_dict