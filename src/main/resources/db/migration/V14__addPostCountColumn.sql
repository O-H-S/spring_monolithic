ALTER TABLE problem
    ADD COLUMN post_count int NOT NULL;

UPDATE problem
SET post_count = 0;