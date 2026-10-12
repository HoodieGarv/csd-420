-- Name: Garvin Stewart
-- Date: 10/04/2026
-- Assignment: CSD 420 Module 9 - JDBC Fan Database
-- Purpose: HOME TESTING ONLY. Creates the database, user, and populated fans table.
--          Run as root:  mysql -u root -p < setup_fans.sql
--          The Java application never runs this; the grader's table already exists.

CREATE DATABASE IF NOT EXISTS databasedb;

CREATE USER IF NOT EXISTS 'student1'@'localhost' IDENTIFIED BY 'pass';
GRANT ALL PRIVILEGES ON databasedb.* TO 'student1'@'localhost';
FLUSH PRIVILEGES;

USE databasedb;

CREATE TABLE IF NOT EXISTS fans (
  ID INT PRIMARY KEY,
  firstname VARCHAR(25),
  lastname VARCHAR(25),
  favoriteteam VARCHAR(25)
);

INSERT IGNORE INTO fans (ID, firstname, lastname, favoriteteam) VALUES
  (1, 'Marcus', 'Reed', 'Carolina Panthers'),
  (2, 'Dana', 'Lopez', 'Charlotte Hornets'),
  (3, 'Kevin', 'Park', 'Green Bay Packers'),
  (4, 'Alicia', 'Moore', 'Dallas Cowboys'),
  (5, 'Tom', 'Nguyen', 'Chicago Bears');
