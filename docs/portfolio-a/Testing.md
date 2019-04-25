# Testing

We plan to test the components of our program using JUnit and any extensions
thereof which are most suited to our domain. The components that will pose the
greatest challenge to automated testing will be:

1. the parts with which users will directly interact, namely the servlets
   handling each web page, and multimedia support. Human input may have to be
   simulated using a UI-oriented testing harness and/or mock implementations.

1. the parts that rely on the presence of real Archive data in the database. We
   will have to construct a dummy database to serve as a test bed for our (or
   the ORM library's) queries, unless the client can provide us with some real
   data.

Unit tests will be written for all feasible components and run automatically
upon compilation to prevent regressions. Additional testing of the UI and
database manipulation will need to be performed before releases.
