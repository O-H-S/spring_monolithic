ALTER TABLE posttag
    ADD COLUMN type enum('Highlight','System') DEFAULT NULL;