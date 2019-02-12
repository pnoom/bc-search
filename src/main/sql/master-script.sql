-- Just run all the other scripts in the right order.

-- Assumes the existence of a database called archivesearch, created as below.

/*
create database archivesearch;
create user 'archiveuser'@'%' identified by 'historicbanana';
grant all on archivesearch.* to 'archiveuser'@'%';
use archivesearch;
*/

SOURCE collections.sql;
SOURCE collectionsdata.sql;
SOURCE haslam.sql;
SOURCE elliott.sql;
SOURCE trotter.sql;
