CREATE TABLE boardpermission
(
    id       bigint       NOT NULL AUTO_INCREMENT,
    board_id int       NOT NULL,
    name     varchar(255) NOT NULL,
    value    text DEFAULT NULL,

    PRIMARY KEY (id),
    INDEX    idx_boardpermission (board_id, name),
    CONSTRAINT FK_boardpermission_board FOREIGN KEY (board_id) REFERENCES board (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;