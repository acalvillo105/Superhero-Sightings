DROP DATABASE IF EXISTS superheroSighting ;
CREATE DATABASE superheroSighting;

USE superheroSighting;

CREATE TABLE superpower(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL
);

CREATE TABLE hero(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL,
    description VARCHAR(255),
    isHero boolean,
    superpowerId INT,
    FOREIGN KEY (superpowerId) REFERENCES superpower(id)    
);

CREATE TABLE location(
	id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    description VARCHAR(255),
    address VARCHAR(50),
    longitude VARCHAR(50),
    latitude VARCHAR(50)
);

CREATE TABLE organization(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL,
    locationId INT,
    description VARCHAR(255),
    FOREIGN KEY (locationId) REFERENCES location(id)
);

CREATE TABLE membership(
    heroId INT NOT NULL,
    organizationId INT NOT NULL,
    PRIMARY KEY(heroId, organizationId),
    FOREIGN KEY (heroId) REFERENCES hero(id),
    FOREIGN KEY (organizationId) REFERENCES organization(id)
);

CREATE TABLE sightings(
	id INT PRIMARY KEY AUTO_INCREMENT,
    locationId INT NOT NULL,
    date DATE,
    description VARCHAR(255),
    FOREIGN KEY (locationId) REFERENCES location(id)
);

CREATE TABLE hero_sightings(
    heroId INT NOT NULL,
    sightingId INT NOT NULL,
    PRIMARY KEY(heroId, sightingId),
    FOREIGN KEY (heroId) REFERENCES hero(id),
    FOREIGN KEY (sightingId) REFERENCES sightings(id)
);
