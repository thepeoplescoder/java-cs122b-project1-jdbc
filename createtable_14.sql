-- Destroy the moviedb database and recreate it from scratch
DROP DATABASE moviedb;
CREATE DATABASE moviedb;
USE moviedb;				-- Ensure that we make it the active DB so we can start creating tables.

CREATE TABLE movies
(
	id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
	title VARCHAR(100) NOT NULL,
	year INTEGER NOT NULL,
	director VARCHAR(100) NOT NULL,
	banner_url VARCHAR(200),
	trailer_url VARCHAR(200)
);

CREATE TABLE stars
(
	id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	dob DATE,
	photo_url VARCHAR(200)
);

CREATE TABLE stars_in_movies
(
	star_id INTEGER NOT NULL REFERENCES stars(id),
	movie_id INTEGER NOT NULL REFERENCES movies(id)
);

CREATE TABLE genres
(
	id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(32) NOT NULL
);

CREATE TABLE genres_in_movies
(
	genre_id INTEGER NOT NULL REFERENCES genres(id),
	movie_id INTEGER NOT NULL REFERENCES movies(id)
);

CREATE TABLE customers
(
	id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	cc_id VARCHAR(20) NOT NULL REFERENCES creditcards(id),
	address VARCHAR(200) NOT NULL,
	email VARCHAR(50) NOT NULL,
	password VARCHAR(20) NOT NULL
);

CREATE TABLE sales
(
	id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
	customer_id INTEGER NOT NULL REFERENCES customers(id),
	movie_id INTEGER NOT NULL REFERENCES movies(id),
	sale_date DATE NOT NULL
);

CREATE TABLE creditcards
(
	id VARCHAR(20) NOT NULL PRIMARY KEY,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	expiration DATE NOT NULL
);