Hive
---

## create database

    database=budget
    hivePath=/projects/cb_budget/hive/budget
    hive -database $database -hiveconf mapred.job.queue.name=curveball_med -hivevar hivePath=$hivePath -f spgen_campaign.sql

    # spgen_campaign.sql
    DROP TABLE IF EXISTS campaign_details_daily_bak;
    CREATE TABLE IF NOT EXISTS campaign_details_daily_bak
    (
        cmpgn_id        bigint,
        currency        string,
        revenue_usd                 double
    )
    PARTITIONED BY (ds string)
    ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
    STORED as TEXTFILE
    LOCATION '/projects/cb_budget/hive/budget/campaign_details_daily_bak'
    TBLPROPERTIES ('auto.purge'='true');

## add partition

    hive -hiveconf mapred.job.queue.name=$HIVE_QUEUE -e "alter table budget.trate_increment add partition (ds='$ds') location '$DEST_DIR/$yesterday/'"
