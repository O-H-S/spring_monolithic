
CREATE TABLE IF NOT EXISTS account_notifications (
    id         BIGINT   NOT NULL AUTO_INCREMENT PRIMARY KEY,
    timestamp  datetime(6) NOT NULL,
    viewed     BOOLEAN  NOT NULL,
    account_id BIGINT   NOT NULL,
    FOREIGN KEY (account_id) REFERENCES account (id)
);

CREATE TABLE IF NOT EXISTS problem_post_notifications (
    id        BIGINT   NOT NULL PRIMARY KEY,
    problem_id BIGINT   NOT NULL,
    post_id   BIGINT   NOT NULL,
    valid     BOOLEAN  NOT NULL,
    FOREIGN KEY (id) REFERENCES account_notifications (id),
    FOREIGN KEY (problem_id) REFERENCES problem (id),
    FOREIGN KEY (post_id) REFERENCES post (id)
);