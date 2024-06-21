CREATE TABLE problembookmark
(
    id            bigint NOT NULL AUTO_INCREMENT,
    account_id    BIGINT NOT NULL,
    problem_id    BIGINT NOT NULL,
    bookmark_type enum('Unsolved','FailedCompletely','TooLong','Success','SuccessDifferently') DEFAULT NULL,
    PRIMARY KEY (id),
    INDEX idx_problembookmark_account_id_problem_id (account_id, problem_id),
    CONSTRAINT FK_problembookmark_account FOREIGN KEY (account_id) REFERENCES account (id),
    CONSTRAINT FK_problembookmark_problem FOREIGN KEY (problem_id) REFERENCES problem (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE problemposthistory
(
    id            bigint NOT NULL AUTO_INCREMENT,
    problem_id    BIGINT NOT NULL,
    post_id    BIGINT NOT NULL,
    valid         bit(1) NOT NULL,
    create_date datetime(6) DEFAULT NULL,


    PRIMARY KEY (id),
    INDEX idx_problemposthistory_problem_id_create_date (problem_id, create_date),
    CONSTRAINT FK_problemposthistory_problem FOREIGN KEY (problem_id) REFERENCES problem (id),
    CONSTRAINT FK_problemposthistory_post FOREIGN KEY (post_id) REFERENCES post (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;