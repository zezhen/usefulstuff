
import pytz, datetime
import urllib2
import json,sys

min_shift=0
local = pytz.timezone(sys.argv[1])
naive = datetime.datetime(2015, 11, 20, 0, 0, 0, 0)
local_dt = local.localize(naive, is_dst=None)
print local_dt 
time = local_dt.astimezone(pytz.utc).strftime("%Y%m%d%H%M%S")
print time
