ALTER TABLE travel
    ADD COLUMN status ENUM ('ACTIVE', 'COMPLETED', 'CANCELED') DEFAULT 'ACTIVE';
