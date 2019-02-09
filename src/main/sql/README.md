# SQL setup instructions

1. Install MySQL, setting a root password that you can actually remember.

1. Run:

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
grant all on archivesearch.* to 'archiveuser'@'%';
use archivesearch;
```

1. Then do these IN THIS ORDER:

```
source master-script.sql
```

1. Now you can run some SQL queries directly, eg:

```
select item.name from item join subcollection on item.subcollection_id = subcollection.id where subcollection.name like '%African%';
```

1. Now start the Java program!
