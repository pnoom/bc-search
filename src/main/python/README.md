# Python script for converting CSV files into SQL insert statements for our schema

Do this:

python csv-to-sql.py
sudo mysql --password

use mydatabase;
source collections.sql; source collectionsdata.sql; source haslam.sql; source elliott.sql; source trotter.sql;

(MUST source the scripts in this order!) Then try some SELECT statements, eg:

select Item.name from Item join SubCollection on Item.subCollectionId = SubCollection.id where SubCollection.name like '%African%';
