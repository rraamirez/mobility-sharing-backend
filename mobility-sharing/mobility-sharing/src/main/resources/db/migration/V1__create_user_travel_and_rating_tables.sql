CREATE TABLE roles
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO roles (name)
VALUES ('ADMIN'),
       ('USER');

CREATE TABLE users
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    email        VARCHAR(150) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    username     VARCHAR(15)  NOT NULL UNIQUE,
    rupee_wallet INT       DEFAULT 0,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    role_id      INT          NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE travel
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    driver_id   INT          NOT NULL,
    origin      VARCHAR(150) NOT NULL,
    destination VARCHAR(150) NOT NULL,
    date        DATE         NOT NULL,
    time        TIME         NOT NULL,
    price       INT          NOT NULL DEFAULT 0,
    created_at  TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (driver_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE user_travel
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT NOT NULL,
    travel_id  INT NOT NULL,
    status     ENUM ('pending', 'confirmed', 'canceled') DEFAULT 'pending',
    created_at TIMESTAMP                                 DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (travel_id) REFERENCES travel (id) ON DELETE CASCADE
);

CREATE TABLE rating
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    rating_user_id INT NOT NULL,
    rated_user_id  INT NOT NULL,
    travel_id      INT,
    rating         INT CHECK (rating BETWEEN 1 AND 5),
    comment        TEXT,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rating_user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (rated_user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (travel_id) REFERENCES travel (id) ON DELETE SET NULL
);
