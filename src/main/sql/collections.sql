-- DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS multimedia;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS collection;
DROP TABLE IF EXISTS dept;

CREATE TABLE dept (
  id             INTEGER       PRIMARY KEY AUTO_INCREMENT,
  name           VARCHAR(200)  NOT NULL UNIQUE
);

CREATE TABLE collection (
  id             INTEGER       PRIMARY KEY AUTO_INCREMENT,
  name           VARCHAR(200)  NOT NULL UNIQUE,
  dept_id	 INTEGER       NOT NULL,
  FOREIGN KEY (dept_id) REFERENCES dept(id)
);

CREATE TABLE item (
  id             		INTEGER        PRIMARY KEY AUTO_INCREMENT,
  item_ref                      VARCHAR(200)   NOT NULL UNIQUE,
  location                      VARCHAR(200)   NULL,
  name           		VARCHAR(200)   NULL,
  description           	TEXT           NULL,
  start_date			DATE	       NULL,
  end_date			DATE	       NULL,
  -- What the archivists typed in
  display_date                  VARCHAR(200)   NULL,
  extent			VARCHAR(200)   NULL,
  phys_tech_desc		TEXT           NULL,
  media_irn				VARCHAR(200)	NOT NULL,
  media_count     INTEGER       NOT NULL,
  copyrighted                   VARCHAR(200)    NOT NULL,
  collection_display_name       VARCHAR(200)   NULL,
  collection_id                 INTEGER        NOT NULL,
  FOREIGN KEY (collection_id) REFERENCES collection(id)
);

/*
CREATE TABLE account (
  id				INTEGER		PRIMARY KEY AUTO_INCREMENT
);
*/
