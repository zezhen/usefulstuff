
# time function

today=`date "+%Y%m%d" -d '-10 minute'`
yesterday=`date "+%Y%m%d" -d '$today -1 day'`


# file system
tmpfile=`mktemp /tmp/abc-script.XXXXXX`

fullfile=$0
basename $fullfile
filename=`basename "$fullfile"`
extension=`${filename##*.}`
filename=`${filename%.*}`
filename=`${fullfile##*/}`

# parameter
verbose=off
while getopts vd:f: opt
do
    case "$opt" in
      v)  verbose=on;;
      d)  today="$OPTARG";;
	  f)  tmpfile="$OPTARG";;
      \?)       # unknown flag
          echo >&2 \
      "usage: $0 [-v] [-d day] [-f file ...]"
      exit 1;;
    esac
done
shift `expr $OPTIND - 1`

# for-loop
day='20160911'
end='20160921'
while [ "$day" -lt "$end" ] ; 
do 
    day=`date +"%Y%m%d" -d "$day + 1 day"`; 
    echo $day
done

# execute python
bc_whitelist=`python -c "
import sys
from datetime import datetime, timedelta

standard=datetime.strptime(sys.argv[1], '%Y%m%d%H%M%S')
min = timedelta(days=10000)
min_file = ''

for file in sys.argv[2:]:
    time=file.strip().split('-')[1]
    dtime = datetime.strptime(time, '%Y%m%d%H%M%S')
    if dtime > standard + timedelta(hours=8):
        continue

    gap = abs(dtime - standard)
    if gap < min:
        min = gap
        min_file = file

print min_file
" 201609110000 [files]`

# send email
from="sender email"
to="recipients email"
subject="Gemini ODA Report $endDate"
cat $tmpfile | formail -I "To: $to" -I "From: $from" -I "MIME-Version:1.0" -I "Content-type:text/html;charset=iso-8859-1" -I "Subject: $subject" | /usr/sbin/sendmail -t

