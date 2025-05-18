ALTER TABLE travel
    ADD COLUMN latitude_origin       FLOAT DEFAULT NULL,
    ADD COLUMN longitude_origin      FLOAT DEFAULT NULL,
    ADD COLUMN latitude_destination  FLOAT DEFAULT NULL,
    ADD COLUMN longitude_destination FLOAT DEFAULT NULL;
