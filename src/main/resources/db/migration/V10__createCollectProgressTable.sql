CREATE TABLE collect_progress (
    id int NOT NULL AUTO_INCREMENT,
    platform varchar(64) NOT NULL,
    collector_version int NOT NULL,
    last_window int NOT NULL,
    start_date datetime(6) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;