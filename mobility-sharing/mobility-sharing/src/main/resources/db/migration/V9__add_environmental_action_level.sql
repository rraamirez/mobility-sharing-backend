ALTER TABLE travel
    ADD COLUMN environmental_action_level ENUM ('LOW','MEDIUM','HIGH')
        DEFAULT 'HIGH';

UPDATE travel
SET environmental_action_level = 'HIGH'
WHERE environmental_action_level IS NULL;

ALTER TABLE travel
    MODIFY COLUMN environmental_action_level ENUM ('LOW','MEDIUM','HIGH')
        NOT NULL DEFAULT 'HIGH';
