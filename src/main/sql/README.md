# SQL setup instructions

1. Install MySQL, setting a root password that you can actually remember.

2. Run:

```
cd bc-search/src/main/sql/
```

1. Start MySQL up as root:

```
sudo mysql -u root -p
Enter password: .......
``` 

1. Do this at MySQL prompt:

```
drop database archivesearch;
create database archivesearch;
create user 'archiveuser'@'%' identified by 'YourDatabasePassword';
grant all on archivesearch.* to 'archiveuser'@'%';
use archivesearch;
source schema.sql;
```

1. Now you can run some SQL queries directly, eg:

```
select item.name from item join collection on item.collection_id = collection.id where collection.name like '%African%';
```

1. Now start the Java program!
