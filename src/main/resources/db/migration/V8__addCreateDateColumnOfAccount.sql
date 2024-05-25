ALTER TABLE account
    ADD COLUMN create_date datetime(6) DEFAULT NULL;

UPDATE account
SET create_date = CURRENT_TIMESTAMP(6);



