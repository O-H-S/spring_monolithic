CREATE TABLE local_credential (
                                    id bigint NOT NULL AUTO_INCREMENT,
                                    password varchar(255) NOT NULL,
                                    username varchar(255) NOT NULL,
                                    account_id bigint NOT NULL,
                                    PRIMARY KEY (id),
                                    UNIQUE KEY UK_dkd5n96msirfvl1poqy0bm2tb (username),
                                    UNIQUE KEY UK_7g4dn04kock5tqcw2680a52xn (account_id),
                                    CONSTRAINT FK601nnitwub7vhyu13htsr4rsk FOREIGN KEY (account_id) REFERENCES account (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE oauth2credential (
                                    id bigint NOT NULL AUTO_INCREMENT,
                                    provider varchar(255) NOT NULL,
                                    provider_id varchar(255) NOT NULL,
                                    account_id bigint NOT NULL,
                                    PRIMARY KEY (id),
                                    UNIQUE KEY UK_82l7g4u3ayhibq8qvwek0jsvi (account_id),
                                    CONSTRAINT FKmb7xpp6rmt198808m5exk7qcw FOREIGN KEY (account_id) REFERENCES account (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


