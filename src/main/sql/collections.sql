DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS collection;

CREATE TABLE collection (
  id             INTEGER       PRIMARY KEY AUTO_INCREMENT,
  name           VARCHAR(200)  NOT NULL,
  description    TEXT          NOT NULL
);

/*
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
*/

CREATE TABLE item (
  id             		INTEGER        PRIMARY KEY AUTO_INCREMENT,
  item_ref                      VARCHAR(200)   NOT NULL,
  location                      VARCHAR(200)   NOT NULL,
  name           		VARCHAR(200)   NULL,
  description           	TEXT           NULL,
  start_date			DATE	       NULL,
  end_date			DATE	       NULL,
  -- What the archivists typed in
  display_date                  VARCHAR(200)   NOT NULL,
  copyrighted                   VARCHAR(200)   NOT NULL,
  extent			VARCHAR(200)   NOT NULL,
  phys_tech_desc		TEXT           NULL,
  multimedia_irn    		VARCHAR(200),
  collection_id                 INTEGER        NULL,
  FOREIGN KEY (collection_id) REFERENCES collection(id)
);

CREATE TABLE account (
  id				INTEGER		PRIMARY KEY AUTO_INCREMENT
);
