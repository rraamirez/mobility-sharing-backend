ALTER TABLE users
    ADD COLUMN rating INT;

ALTER TABLE users
    ADD CONSTRAINT chk_rating_range CHECK (rating BETWEEN 1 AND 5 OR rating IS NULL);
