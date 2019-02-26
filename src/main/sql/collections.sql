-- DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS collection;
DROP TABLE IF EXISTS dept;

CREATE TABLE dept (
  id             INTEGER       PRIMARY KEY AUTO_INCREMENT,
  name           VARCHAR(200)  NOT NULL
);

CREATE TABLE collection (
  id             INTEGER       PRIMARY KEY AUTO_INCREMENT,
  name           VARCHAR(200)  NOT NULL,
  dept_id	 INTEGER       NOT NULL,
  FOREIGN KEY (dept_id) REFERENCES dept(id)
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
  location                      VARCHAR(200)   NOT NULL, -- should be NOT NULL, really
  name           		VARCHAR(200)   NULL,
  description           	TEXT           NULL,
  start_date			DATE	       NULL,
  end_date			DATE	       NULL,
  -- What the archivists typed in
  display_date                  VARCHAR(200)   NULL,  -- should be NOT NULL, really
  copyrighted                   VARCHAR(200)   NULL,  -- should be NOT NULL, really
  extent			VARCHAR(200)   NULL,  -- should be NOT NULL, really
  phys_tech_desc		TEXT           NULL,
  multimedia_irn    		VARCHAR(200)   NULL,
  collection_display_name       VARCHAR(200)   NULL,
  collection_id                 INTEGER        NULL,
  FOREIGN KEY (collection_id) REFERENCES collection(id)
);

/*
CREATE TABLE account (
  id				INTEGER		PRIMARY KEY AUTO_INCREMENT
);
*/
