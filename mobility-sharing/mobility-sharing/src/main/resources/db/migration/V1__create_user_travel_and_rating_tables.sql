-- Users table
CREATE TABLE user
(
    id           INT AUTO_INCREMENT PRIMARY KEY,     -- Unique identifier for the user
    name         VARCHAR(100) NOT NULL,              -- User's name
    email        VARCHAR(150) NOT NULL UNIQUE,       -- Unique email
    password     VARCHAR(255) NOT NULL,              -- User's password
    nickname     VARCHAR(15)  NOT NULL,              -- Optional phone number
    rupee_wallet INT       DEFAULT 0,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Creation date
);

-- Travel table
CREATE TABLE travel
(
    id          INT AUTO_INCREMENT PRIMARY KEY,                    -- Unique identifier for the travel
    driver_id   INT          NOT NULL,                             -- FK to the user offering the travel (driver)
    origin      VARCHAR(150) NOT NULL,                             -- Travel origin
    destination VARCHAR(150) NOT NULL,                             -- Travel destination
    date        DATE         NOT NULL,                             -- Travel date
    time        TIME         NOT NULL,                             -- Travel time
    price       INT          NOT NULL DEFAULT 0,                   -- Price per seat
    created_at  TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,   -- Travel creation date
    FOREIGN KEY (driver_id) REFERENCES user (id) ON DELETE CASCADE -- Relationship with User
);

-- Intermediate table to manage users registered for travels
CREATE TABLE user_travel
(
    id         INT AUTO_INCREMENT PRIMARY KEY,                                      -- Unique identifier for the record
    user_id    INT NOT NULL,                                                        -- FK to the user registered for the travel
    travel_id  INT NOT NULL,                                                        -- FK to the related travel
    status     ENUM ('pending', 'confirmed', 'canceled') DEFAULT 'pending',         -- Status of the record
    created_at TIMESTAMP                                 DEFAULT CURRENT_TIMESTAMP, -- Record creation date
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,                   -- Relationship with User
    FOREIGN KEY (travel_id) REFERENCES travel (id) ON DELETE CASCADE                -- Relationship with Travel
);

-- Ratings table for user reviews
CREATE TABLE rating
(
    id             INT AUTO_INCREMENT PRIMARY KEY,                       -- Unique identifier for the rating
    rating_user_id INT NOT NULL,                                         -- FK to the user giving the rating
    rated_user_id  INT NOT NULL,                                         -- FK to the user receiving the rating
    travel_id      INT,                                                  -- FK to the related travel (optional)
    rating         INT CHECK (rating BETWEEN 1 AND 5),                   -- Rating (1-5)
    comment        TEXT,                                                 -- Optional comment
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                  -- Rating creation date
    FOREIGN KEY (rating_user_id) REFERENCES user (id) ON DELETE CASCADE, -- Relationship with the reviewer
    FOREIGN KEY (rated_user_id) REFERENCES user (id) ON DELETE CASCADE,  -- Relationship with the rated user
    FOREIGN KEY (travel_id) REFERENCES travel (id) ON DELETE SET NULL    -- Relationship with the travel
);
