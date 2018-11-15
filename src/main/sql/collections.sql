DROP TABLE IF EXISTS Location;
DROP TABLE IF EXISTS Item;
DROP TABLE IF EXISTS SubCollection;
DROP TABLE IF EXISTS Collection;

CREATE TABLE Collection (
  id             INTEGER       PRIMARY KEY AUTO_INCREMENT,
  name           VARCHAR(100)  NOT NULL,
  description    TEXT          NOT NULL
);

CREATE TABLE SubCollection (
  id             INTEGER        PRIMARY KEY AUTO_INCREMENT,
  description    TEXT           NOT NULL,
  collectionId   INTEGER        NOT NULL,
  FOREIGN KEY    (collectionId) REFERENCES Collection(id)
);

CREATE TABLE Item (
  id             		INTEGER        PRIMARY KEY AUTO_INCREMENT,
  name           		VARCHAR(100)   NOT NULL,
  description           	TEXT           NOT NULL,
  dateAdded		 	DATE	       NULL,
  earliestDateCreated           DATE	       NULL,
  latestDateCreated		DATE	       NULL,
  copyrighted                   BIT            NOT NULL,
  objectNumber			VARCHAR(100)   NOT NULL,
  nature			VARCHAR(100)   NOT NULL,
  physTechDesc			TEXT           NULL,
  collectionId                  INTEGER        NOT NULL,
  subcollectionId               INTEGER        NULL,
  FOREIGN KEY (collectionId)    REFERENCES Collection(id),
  FOREIGN KEY (subcollectionId) REFERENCES SubCollection(id)
);

CREATE TABLE Location (
  id             INTEGER      PRIMARY KEY AUTO_INCREMENT,
  name           VARCHAR(100) NOT NULL,
  itemId         INTEGER      NOT NULL,
  FOREIGN KEY    (itemId)     REFERENCES Item(id)
);
