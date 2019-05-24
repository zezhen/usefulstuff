# create mysql table by command: mysql -u [user] [database] < create_table_w_sequential_time_partition.ddl
CREATE TABLE IF NOT EXISTS spgen_output (
    time DateTime,
    interval_start DateTime,
    id int,
    valid_io_line varchar(50),
    spend_type enum('NATIVE','SEARCH','UNKOWN') NOT NULL,
    s01 float DEFAULT 0.0,
    valid bool,
    PRIMARY KEY(id, type, interval_start,time)
) ENGINE=InnoDB DEFAULT CHARACTER SET latin1 COLLATE latin1_general_ci
PARTITION BY RANGE (TO_SECONDS(time) DIV 3600)
(PARTITION zero VALUES LESS THAN (1),
PARTITION p20140915 VALUES LESS THAN (17669328) ENGINE = InnoDB,
PARTITION p20140916 VALUES LESS THAN (17669352) ENGINE = InnoDB,
PARTITION p20140917 VALUES LESS THAN (17669376) ENGINE = InnoDB,
PARTITION future VALUES LESS THAN MAXVALUE ENGINE = InnoDB);

# EVENT is built-in cron job in mysql, but in this case it will fail, need use script outside
DROP EVENT IF EXISTS clean_partition;

delimiter |

CREATE EVENT clean_partition
ON SCHEDULE EVERY 1 DAY STARTS '2015-09-19 00:00:00'
DO
BEGIN
    DECLARE PAR varchar(10);
    DECLARE PPAR varchar(10);
    DECLARE NUM varchar(8);
    SET PAR = p(DATE_FORMAT(UTC_TIMESTAMP(), '%Y%m%d'));
    SET PPAR = p(DATE_FORMAT((UTC_TIMESTAMP() - INTERVAL 30 DAY), '%Y%m%d'));
    SET NUM = (TO_SECONDS(current_date()) DIV 3600);
    ALTER TABLE spgen_output DROP PARTITION PPAR;
    ALTER TABLE spgen_output ADD PARTITION (PARTITION PAR VALUES LESS THAN (NUM) ENGINE = InnoDB);
END |

delimiter ;