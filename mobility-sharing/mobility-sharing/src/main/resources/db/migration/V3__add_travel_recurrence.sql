CREATE TABLE travel_recurrence
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE travel
    ADD COLUMN travel_recurrence_id INT,
    ADD FOREIGN KEY (travel_recurrence_id) REFERENCES travel_recurrence (id);

