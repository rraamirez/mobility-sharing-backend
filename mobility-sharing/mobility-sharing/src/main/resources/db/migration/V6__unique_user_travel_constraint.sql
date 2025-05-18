ALTER TABLE user_travel
    ADD CONSTRAINT uq_user_travel UNIQUE (user_id, travel_id);
