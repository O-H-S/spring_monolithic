CREATE TABLE problem (
  id bigint NOT NULL AUTO_INCREMENT,
  platform varchar(64) NOT NULL,
  platform_id varchar(255) NOT NULL,
  title varchar(255) NOT NULL,
  difficulty varchar(64) DEFAULT NULL,
  link varchar(255) NOT NULL,
  found_date datetime(6) NOT NULL,
  collector_version int NOT NULL,
  CONSTRAINT uniqueInPlatform UNIQUE (platform, platform_id),
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;