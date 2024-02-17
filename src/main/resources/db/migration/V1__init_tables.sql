
CREATE TABLE account (
                           id bigint NOT NULL AUTO_INCREMENT,
                           email varchar(255) DEFAULT NULL,
                           password varchar(255) DEFAULT NULL,
                           provider varchar(255) DEFAULT NULL,
                           provider_id varchar(255) DEFAULT NULL,
                           role enum('ADMIN','USER') DEFAULT NULL,
                           username varchar(255) DEFAULT NULL,
                           PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE board (
                         id int NOT NULL AUTO_INCREMENT,
                         create_date datetime(6) DEFAULT NULL,
                         deleted bit(1) NOT NULL,
                         description text,
                         pagination_type enum('Cursor','Hybrid','Offset','Offset_CountCache','Offset_CountCache_CoveringIndex') DEFAULT NULL,
                         post_count bigint DEFAULT NULL,
                         threshold_for_cursor bigint DEFAULT NULL,
                         title varchar(250) DEFAULT NULL,
                         PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


CREATE TABLE post (
                        id bigint NOT NULL AUTO_INCREMENT,
                        comment_count int DEFAULT NULL,
                        content text,
                        create_date datetime(6) DEFAULT NULL,
                        deleted bit(1) DEFAULT NULL,
                        like_count bigint DEFAULT NULL,
                        modify_date datetime(6) DEFAULT NULL,
                        title varchar(200) DEFAULT NULL,
                        view_count bigint DEFAULT NULL,
                        author_id bigint DEFAULT NULL,
                        board_id int DEFAULT NULL,
                        PRIMARY KEY (id),
                        KEY idx_post_deleted_board_id_create_date (deleted,board_id,create_date DESC),
                        KEY idx_post_deleted_author_id_create_date (deleted,author_id,create_date DESC),
                        KEY FKnbtjnppdvrrcy9pijwbv7bpp5 (author_id),
                        KEY FK2t7katxxymxif93a9osshl0ns (board_id),
                        CONSTRAINT FK2t7katxxymxif93a9osshl0ns FOREIGN KEY (board_id) REFERENCES board (id),
                        CONSTRAINT FKnbtjnppdvrrcy9pijwbv7bpp5 FOREIGN KEY (author_id) REFERENCES account (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


CREATE TABLE postlike (
                            id bigint NOT NULL AUTO_INCREMENT,
                            create_date datetime(6) DEFAULT NULL,
                            update_date datetime(6) DEFAULT NULL,
                            valid bit(1) DEFAULT NULL,
                            post_id bigint DEFAULT NULL,
                            member_id bigint DEFAULT NULL,
                            PRIMARY KEY (id),
                            KEY idx_postlike_post_id_member_id (post_id,member_id),
                            KEY FK9105g2hh9cg4vuv9patasx6i (member_id),
                            CONSTRAINT FK3srt471ix9w98mcbbghlryvgd FOREIGN KEY (post_id) REFERENCES post (id),
                            CONSTRAINT FK9105g2hh9cg4vuv9patasx6i FOREIGN KEY (member_id) REFERENCES account (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE postview (
                            id bigint NOT NULL AUTO_INCREMENT,
                            count int DEFAULT NULL,
                            create_date datetime(6) DEFAULT NULL,
                            update_date datetime(6) DEFAULT NULL,
                            post_id bigint DEFAULT NULL,
                            member_id bigint DEFAULT NULL,
                            PRIMARY KEY (id),
                            KEY idx_postview_post_id_member_id (post_id,member_id),
                            KEY FKm6qj4upscaybwlmhol7vr5gci (member_id),
                            CONSTRAINT FK28xijwasxioi98s7aud0a73eb FOREIGN KEY (post_id) REFERENCES post (id),
                            CONSTRAINT FKm6qj4upscaybwlmhol7vr5gci FOREIGN KEY (member_id) REFERENCES account (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


CREATE TABLE comment (
                           id bigint NOT NULL AUTO_INCREMENT,
                           content text,
                           create_date datetime(6) DEFAULT NULL,
                           deleted bit(1) NOT NULL,
                           like_count bigint DEFAULT NULL,
                           modify_date datetime(6) DEFAULT NULL,
                           author_id bigint DEFAULT NULL,
                           post_id bigint DEFAULT NULL,
                           PRIMARY KEY (id),
                           KEY idx_comment_deleted_post_id_create_date (deleted,post_id,create_date DESC),
                           KEY FKrv3ioi1asorsx190lfsdnkw19 (author_id),
                           KEY FKs1slvnkuemjsq2kj4h3vhx7i1 (post_id),
                           CONSTRAINT FKrv3ioi1asorsx190lfsdnkw19 FOREIGN KEY (author_id) REFERENCES account (id),
                           CONSTRAINT FKs1slvnkuemjsq2kj4h3vhx7i1 FOREIGN KEY (post_id) REFERENCES post (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE commentlike (
                               id bigint NOT NULL AUTO_INCREMENT,
                               create_date datetime(6) DEFAULT NULL,
                               update_date datetime(6) DEFAULT NULL,
                               valid bit(1) DEFAULT NULL,
                               comment_id bigint DEFAULT NULL,
                               member_id bigint DEFAULT NULL,
                               PRIMARY KEY (id),
                               KEY idx_commentlike_comment_id_member_id (comment_id,member_id),
                               KEY FK6t1lu93xpmae0xdnoshj5rq6q (member_id),
                               CONSTRAINT FK6t1lu93xpmae0xdnoshj5rq6q FOREIGN KEY (member_id) REFERENCES account (id),
                               CONSTRAINT FKl0btybtvrr3s7egcb7r7jybdb FOREIGN KEY (comment_id) REFERENCES comment (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

