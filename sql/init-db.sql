CREATE TABLE users
(
    userId   SERIAL  NOT NULL,
    userName varchar NOT NULL UNIQUE
);

INSERT INTO users(userName)
VALUES ('User #1');
INSERT INTO users(userName)
VALUES ('User #2');