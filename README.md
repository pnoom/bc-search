# Bristol Archive Search Engine

A web application for searching the Bristol Archive. It is intended to be a
"unified interface" for accessing the Archive's multimedia collections,
replacing the various existing search tools such as
[this](http://museums.bristol.gov.uk/) and
[this](http://museums.bristol.gov.uk/). It is currently available
[here](https://archivesearch.spe.cs.bris.ac.uk). Alternatively it can be run
locally by following the below instructions.

It is being developed by a group of second-year Computer Science students at
the University of Bristol.

## Installation

First install [Maven](https://maven.apache.org/install.html) and
[MySQL](https://dev.mysql.com/doc/mysql-installation-excerpt/5.7/en/). For the
latter, be sure to set a memorable root password during installation.

Then clone this repository. Change into its root directory, then compile the
program with `mvn clean compile package`.

Then initialize the database as follows. Run:

```
cd bc-search/src/main/sql/
```

Start MySQL up as root:

```
sudo mysql -u root -p
Enter password: .......
```

Do this at MySQL prompt:

```
drop database archivesearch;
create database archivesearch;
grant all on archivesearch.* to 'archiveuser'@'%';
use archivesearch;
source master-script.sql;
quit;
```

Finally, start the program using `mvn exec:java` or `java -jar
target/archives-api-1.0-SNAPSHOT.jar`. Open a browser window, type
`localhost:8011` into the address bar, and hit go.

## Usage instructions

Try the following example search terms in the search bar:

Cinderella, KAR, Uniformed soldiers, Trotter, Uganda

You should be met with a list of results. Click the name of a result to see its
detail page. Click images to enlarge them, if present. Click the Back button in
your browser to return to the results list, or the Home button in the website's
NavBar to start a new search.

Click "Advanced Search" for more searching options. (Note that most items
currently in the database are dated from the 1940s to the 1960s, and relate to
various places in Africa, so if you search for dates and places outside of
these then you may be met with no results.) Click "Date Range" to toggle
between searching by a specific date and searching by a range of dates.

## Image credits

The Bristol Council and Archive logos are taken from their respective
websites. All items in the database are copyright of the Bristol Record Office.

## Original Brief

"At the Bristol Archives, we hold over 10,000 historical photographs and film
from the British Empire and Commonwealth Collection. Archives staff are working
to digitise and catalogue the collections in our collections Management system,
Emu.

We wish to make the collections available to the public to search online, in
order to raise awareness and generate interest in the collections. We would
like a project team to build a prototype which will help our uses (both
specialist researchers and non specialist) browser the collection and search
the data in a user friendly and fast way.

Our database has an image APi and we can provide .csv files of the raw data. We
are interested in any solutions which open up the database to researchers in
novel ways, but also provide a detailed search option and are able to represent
the hierarchical structure of archives in a tree view.

Any solutions to performance of database searches and multimedia manipulation
would be interesting. We also have video and audio recordings as part of the
archive."
