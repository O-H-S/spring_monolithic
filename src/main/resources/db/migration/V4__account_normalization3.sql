ALTER TABLE account
    ADD COLUMN authentication_type enum('Local','OAuth2') DEFAULT NULL,
    ADD COLUMN nickname varchar(255) DEFAULT NULL,
    DROP COLUMN password,
    DROP COLUMN provider,
    DROP COLUMN provider_id,
    DROP COLUMN username;

