#! /bin/sh

export PATH=/home/y/bin:/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin

day=`date "+%Y%m%d"`

if [ $# -ge 1 ]; then
    day=$1
fi

PPAR=p`date "+%Y%m%d" -d "$day -30 day"`
PAR=p`date "+%Y%m%d" -d "$day"`
TOMO=`date "+%Y%m%d" -d "$day +1 day"`

MYSQL='/home/y/bin64/mysql -u root pmdb -e '

NUM=$(( ($(date --date="$TOMO" +%s) - $(date --date="00000101" +%s) )/(3600) ))

function update_table {
   TABLE=$1
   echo "update table $TABLE"

   echo "remove old partiton $PPAR";
   $MYSQL "ALTER TABLE $TABLE DROP PARTITION $PPAR;"
   $MYSQL "ALTER TABLE $TABLE DROP PARTITION future;"

   echo "remove data from partition"
   #$MYSQL "ALTER TABLE $TABLE TRUNCATE PARTITION $PPAR;"
   #$MYSQL "ALTER TABLE $TABLE TRUNCATE PARTITION all;"

   echo "add new partition $PAR less that $NUM"
   $MYSQL "ALTER TABLE $TABLE ADD PARTITION (PARTITION $PAR VALUES LESS THAN ($NUM) ENGINE = InnoDB);"
   $MYSQL "ALTER TABLE $TABLE ADD PARTITION (PARTITION future VALUES LESS THAN MAXVALUE ENGINE = InnoDB);"
}

if [ $# -ge 2 ]; then
   update_table $2
   exit $?
fi
