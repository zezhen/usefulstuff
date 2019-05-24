Useful Mysql Stuff
---

### query
#####1. data size, etc meta info
	select * from information_schema.TABLES where information_schema.TABLES.TABLE_SCHEMA='[databasename]' and information_schema.TABLES.TABLE_NAME='[tablename]'
total size = DATA_LENGTH + INDEX_LENGTH, the unit is **byte**

### load 
##### 1. load local file
	mysql -h[db_host] -u[db_user] [db_schema] -e"load data local infile '[file_path]' into table [table_name] FIELDS TERMINATED BY '[separator]'"

#####2. insert from other table
	insert into table_dest select * from table_src

### event scheduler

MySQL events are executed by a special event scheduler thread. It’s disabled by default so use the following MySQL command can determine whether it’s running:
	
	SHOW PROCESSLIST;

If the scheduler is running, at least two rows will be shown and one will have its user field set to "event_scheduler". If only one row is returned, the scheduler is disabled and events will not run, you can start the scheduler from the MySQL command line:

	SET GLOBAL event_scheduler = ON;

### partition

##### 1. create/drop partions or remove partition data

refer to update_circle_time_partition.sh

##### 2. partitions

    show create table [table_name];
    show table status like '[table_name]';
    select * from information_schema.KEY_COLUMN_USAGE where table_name='[table_name]'; // check index
    select * from information_schema.partitions where table_name='[table_name]'; // check partitions
    explain partitions select * from [table_name] where [conditions];   // check data and partitions

##### 3. reorganize/optimize partitions

    alter table [table_name] reorganize partition p1,p3,p4 into (partition pm1 values less than(2006), partition pm2 values less than(2011));
    alter table [table_name] optimize partition pm1/all; # drop and reinsert data, fix disk fragmentation

### specified topic

#### 1. [How to select the first/least/max row per group in SQL](http://www.xaprb.com/blog/2006/12/07/how-to-select-the-firstleastmax-row-per-group-in-sql)

This post explains how to select the top rows per group gradually, it's very impressive that he can describe the problem very clearly and dive into the solutions step by step. First is to get max row per group using self join, then enumerate multiple ways to select top k rows per group, a elegant way is still self-join but has a counter limitation, union seems useful but it's hard to maintain. The last is use variable in mysql, the main idea is to add row numbers and select top k rows.


### misc

[Is it safe to delete mysql-bin files?](http://dba.stackexchange.com/questions/41050/is-it-safe-to-delete-mysql-bin-files)

[Date and Time Functions](https://dev.mysql.com/doc/refman/5.5/en/date-and-time-functions.html#function_date-format)
