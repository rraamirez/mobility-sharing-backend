CREATE TABLE eco_rank
(
    id   INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

INSERT INTO eco_rank (id, name)
VALUES (1, 'Conscious Seed'),
       (2, 'Sprout of Change'),
       (3, 'Green Advocate'),
       (4, 'Planet Guardian'),
       (5, 'Climate Ally');

ALTER TABLE users
    ADD COLUMN eco_rank_id INT DEFAULT 1,
    ADD CONSTRAINT fk_user_eco_rank FOREIGN KEY (eco_rank_id) REFERENCES eco_rank (id);

UPDATE users
SET eco_rank_id = 1
WHERE eco_rank_id IS NULL;

ALTER TABLE users
    MODIFY COLUMN eco_rank_id INT NOT NULL DEFAULT 1;
