ALTER TABLE posttag
    MODIFY COLUMN type ENUM('Highlight', 'System', 'Normal') DEFAULT NULL;