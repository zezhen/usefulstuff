Curveball
---

### datastore

    /home/y/bin64/broker_lookup -p broker -s 'amazon' -w 23424977 -e -b -r -l 3 --canon-form CANONFORM_VARIANT
  
run this command in match broker host, mock the query to match broker, `-s` specify the bidterm to lookup in multiple query servers (all partitions), `-l 3` to print all detail trace, `--canon-form CANONFORM_VARIANT` explictly ask to query variant database, by default is phrase db, every db contains three match types (exact, phrase, broad) advertisers bidded.

    /home/y/bin64/datastore_lookup -d phrase -s amazon -p -t

run this command in match broker host, send query to all query servers (all partitions), `-d` specify the database, can be phrase or variant, `-p` to print the partition number

    cbtool_fetch.sh phrase 18764922482791005

run this in query server host, `phrase` is the database, `18764922482791005` is the hash code of term amazon, the partition should be align, e.g. partition 22 contains term amazon.
`cbtool_fetch.sh advertiser|campaign|adgroup|term|termmacro|targeting|creative|phrase|exact|forex|cache|adextension id`
     
    cbtool_dump.sh phrase 22

run in query server host, dump the whole data from one partition
cbtool_dump.sh advertiser|campaign|adgroup|term|termmacro|targeting|creative|phrase|exact|forex|cache|adextension|offerid|offer partition_num [-d dump to proto bin]

    CheckDatasetVersionStatus.pl
    /home/y/var/ydisc_service_gdsdataservice/consumer-history.pl

run in gds hosts, check transporter fetch status and history

    ycdb_stat  -p . -d superquad_p0.ycdb

check ycdb record information

    GetOqServiceProviders.pl -s 9173 -z szk1.cbs.cb.gq1.yahoo.com:2181,szk2.cbs.cb.gq1.yahoo.com:2181,szk3.cbs.cb.gq1.yahoo.com:2181,szk4.cbs.cb.gq1.yahoo.com:2181,szk5.cbs.cb.gq1.yahoo.com:2181 -t

get targeting hosts of service id, zookeeper hosts is from `curveball_ad_server.oq_discovery_zk`, `/home/y/conf/curveball_superquad_gds/p10/DiscoveryTcpServerEP.conf` is the source of truth for service id
