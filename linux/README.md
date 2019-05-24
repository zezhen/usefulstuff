Useful Linux Stuff
---

### cron job

    */1 * * * * echo `date` >> ~/date

you can use `crontab -e` to open crontab setting, there are two parts of cron configuration: timer and command

#####1. timer
timer is consist of five units: minute, hour, day, week and month. For example, the first unit is minute, the valid number is 0 - 59, when specified as `*` or `*/1`, it means the timer pass every minute on this unit. that means `echo` command will be invoke every minutes.

    0 * * * * echo `date` >> ~/date

while if you set first one as 0, it menas the command will be executed at 0th minute, which also means this command is executed once per hour.

#####2. step

we can also specified step for command, e.g. I want to run one command once per 5 minutes as below example. `*` is equal to `*/1` as the minimum step is 1 for each unit, below we set the step or run frequency as 5 minutes, the running time will always be 0th, 5th, 10th, ... , 50th and 55th minutes. 

    */5 * * * * echo `date` >> ~/date

#####3. offset

if you want to run command every odd minute, you can set as below, which we can set the start, end timer in numerator part and set frequency at denominator part. You might already know that, `*/5` is equals to `0-59/5`.

    1-59/2 * * * * echo `date` >> ~/date

### disk


`du <directory>`: check disk usage of files and directories on a machine

    -s  summary
    -h  human readble format
    -a  all files under folder
    -k  kilo byte unit
    -m  mega byte unit
    -c  provide a grand total usage
    --exclude="*.txt"  exclude txt files
    --time  show modify time

`df`: displays the information of device name, total blocks, total disk space, used disk space, available disk space and mount points on a file system

    -s, -h, -a, -k, -m  same as du
    -i  number of used inodes and their percentage for the file system
    -T  show filesystem type, such as ext3, ext4
    -t <value>  show certain file system type
    -x <value>  exclude certain file system type

`fdisk`: manage linux disks partitions ->  [fdisk](http://www.tecmint.com/fdisk-commands-to-manage-linux-disk-partitions/)

### git

Exclude a directory from git diff
`git diff previous_release..current_release -- . ':!spec'`


### cannot send email
	mkfifo /var/spool/postfix/public/pickup
	ps aux | grep mail
	kill
	sudo /etc/init.d/postfix restart

### maven unit test
	mvn -T 4 clean install # Builds with 4 threads
	mvn -T 1C clean install # 1 thread per cpu core
	mvn -T 1.5C clean install # 1.5 thread per cpu core

### number sum
	seq 10 | paste -sd+ - | bc
