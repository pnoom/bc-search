
## Application Architecture

The below diagram illustrates the inital structure of our application:

![](graphviz/architecture1.png?raw=true)

Input will be received using HTML forms in the View layer, which will invoke
HTTP requests to the Controller layer. This will determine the appropriate
response - often by first referring to the Model - and trigger a corresponding
change in the View.

To store the archive item data and metadata, We will use a relational database
with the following schema:

![](graphviz/entity-relationships.png?raw=true)

The Model will use a Database Access Object (DAO) to make queries to the
database. As far as possible we will make use of an Object-Relational Mapping
library, but for performance or other reasons we may later have to construct
SQL queries ourselves using 'prepared statements' or equivalent instead.
