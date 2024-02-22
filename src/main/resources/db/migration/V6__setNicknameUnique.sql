

ALTER TABLE account
    MODIFY COLUMN nickname varchar(255) NOT NULL;

UPDATE account
SET nickname = NULL
WHERE nickname IN (
    SELECT nickname
    FROM (
             SELECT nickname
             FROM account
             GROUP BY nickname
             HAVING COUNT(nickname) > 1
         ) AS subquery
) OR nickname = '' OR TRIM(nickname) = '';


ALTER TABLE account
    ADD UNIQUE (nickname);