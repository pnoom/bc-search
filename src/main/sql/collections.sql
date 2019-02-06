DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS subcollection;
DROP TABLE IF EXISTS collection;

CREATE TABLE collection (
  id             INTEGER       PRIMARY KEY AUTO_INCREMENT,
  collection_ref VARCHAR(100)  NOT NULL,
  name           VARCHAR(100)  NOT NULL,
  description    TEXT          NOT NULL
);

CREATE TABLE subcollection (
  id                INTEGER        PRIMARY KEY AUTO_INCREMENT,
  subcollection_ref VARCHAR(100)   NOT NULL,
  -- Mainly to allow Uncategorized. Should be VARCHAR and NOT NULL really, but unsure for now
  name              TEXT           NULL,
  -- Maybe omit this, since we want all interesting data in Items
  description       TEXT           NULL,
  
  collection_id     INTEGER        NOT NULL,
  FOREIGN KEY    (collection_id)   REFERENCES collection(id)
);

CREATE TABLE item (
  id             		INTEGER        PRIMARY KEY AUTO_INCREMENT,
  item_ref                      VARCHAR(100)   NOT NULL,
  location                      VARCHAR(100)   NOT NULL,
  name           		VARCHAR(100)   NULL,
  description           	TEXT           NULL,
  start_date			DATE	       NOT NULL,
  end_date			DATE	       NOT NULL,
  -- What the archivists typed in
  date_created                  VARCHAR(100)   NOT NULL,
  copyrighted                   VARCHAR(100)   NOT NULL,
  extent			VARCHAR(100)   NOT NULL,
  phys_tech_desc		TEXT           NULL,
  multimedia_irn    		VARCHAR(100),
  
  subcollection_id              INTEGER        NULL,
  FOREIGN KEY (subcollection_id) REFERENCES subcollection(id)
);
