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
create database archivesearch;
create user 'archiveuser'@'%' identified by 'historicbanana';
grant all on archivesearch.* to 'archiveuser'@'%';
use archivesearch;
```

1. Then do these IN THIS ORDER:

```
source collections.sql;
source collectionsdata.sql;
source haslam.sql;
source elliott.sql;
source trotter.sql;
```

1. Now you can run some SQL queries directly, eg:

```
select Item.name from Item join SubCollection on Item.subCollectionId = SubCollection.id where SubCollection.name like '%African%';
```

1. Now start the Java program!
