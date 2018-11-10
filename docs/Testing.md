# Testing

We plan to test the components of our program using JUnit and any extensions
thereof which are most suited to our domain. The components that will pose the
greatest challenge to automated testing will be:

1. the parts with which users will directly interact, namely the servlets
   handling each web page. Human input may have to be simulated.
1. the parts that rely on the presence of real Archive data in the database. We
   will have to construct a dummy database to serve as a test bed for our (or
   the ORM library's) queries
1. multimedia support, largely because these too will be interactive


