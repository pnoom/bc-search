DROP TABLE IF EXISTS Item;
DROP TABLE IF EXISTS SubCollection;
DROP TABLE IF EXISTS Collection;

CREATE TABLE Collection (
  id             INTEGER       PRIMARY KEY AUTO_INCREMENT,
  collectionRef  VARCHAR(100)  NOT NULL,
  name           VARCHAR(100)  NOT NULL,
  description    TEXT          NOT NULL
);

CREATE TABLE SubCollection (
  id                INTEGER        PRIMARY KEY AUTO_INCREMENT,
  subCollectionRef  VARCHAR(100)   NOT NULL,
  -- Maybe omit this, since relatively few will have names
  -- name              VARCHAR(100)   NULL,
  description       TEXT           NULL,
  
  collectionId      INTEGER        NOT NULL,
  FOREIGN KEY    (collectionId) REFERENCES Collection(id)
);

CREATE TABLE Item (
  id             		INTEGER        PRIMARY KEY AUTO_INCREMENT,
  itemRef                       VARCHAR(100)   NOT NULL,
  location                      VARCHAR(100)   NOT NULL,
  name           		VARCHAR(100)   NULL,
  description           	TEXT           NULL,
  -- For MVP, date will be a single string, due to inconsistent data entry
  -- dateAdded		 	DATE	       NULL,
  -- earliestDateCreated        DATE	       NULL,
  -- latestDateCreated		DATE	       NULL,
  dateCreated                   VARCHAR(100)   NOT NULL,
  
  copyrighted                   VARCHAR(100)   NOT NULL,
  extent			VARCHAR(100)   NOT NULL,
  physTechDesc			TEXT           NULL,
  
  subcollectionId               INTEGER        NULL,
  FOREIGN KEY (subcollectionId) REFERENCES SubCollection(id)
);
