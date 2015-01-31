CREATE DATABASE test;
use test;

CREATE TABLE users
(
id int NOT NULL AUTO_INCREMENT,
name  varchar(255) NOT NULL,
user_id int,
PRIMARY KEY (id)
);

CREATE TABLE messages
(
id int NOT NULL AUTO_INCREMENT,
text  varchar(255) NOT NULL,
user_id int,
PRIMARY KEY (id),
FOREIGN KEY (user_id) REFERENCES users(id)
);