CREATE TABLE tag (
     id bigint NOT NULL AUTO_INCREMENT,
     name varchar(255) NOT NULL,
     PRIMARY KEY (id),
     UNIQUE INDEX idx_unique_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE posttag (
     id bigint NOT NULL AUTO_INCREMENT,
     post_id BIGINT NOT NULL,
     tag_id BIGINT NOT NULL,
     PRIMARY KEY (id),
     UNIQUE INDEX uniquePostTagPair (post_id, tag_id),
     CONSTRAINT FK_posttag_post FOREIGN KEY (post_id) REFERENCES post (id) ,
     CONSTRAINT FK_posttag_tag FOREIGN KEY (tag_id) REFERENCES tag (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;