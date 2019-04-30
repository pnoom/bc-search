# Object-Oriented Design & UML

## Overview of Implementation

Our application is written in Java, and its architecture was primarily
determined by our use of the Spring Web MVC framework, and our choice of MySQL
as the backend. Below are two diagrams that reflect the structure of the code
and our database schema respectively. The latter is discussed in depth in the
"Design Challenges" section.

### Code Structure

We use the Spring framework's `@Autowired` annotation to ensure that
dependencies between our modules are automatically injected where needed. The
result is a very loosely-coupled set of classes which implement our
application.

![](../diagrams/code-structure.png?raw=true)

### Database Schema

![](../diagrams/schema.png?raw=true)

## Design Challenges

We faced several challenges in designing the architecture of our
application.

The first was that it was not until the project was well under way that the
client provided us with a realistic dataset: our initial design and
implementation had been based on roughly 1000 sample data points from three
Bristol Archives photographic collections only; the final dataset consists of
nearly 100,000 data points from numerous Archive and Museum collections, not
only of photographs but of other kinds of artifact. As result, we had to
substantially restructure the backend mid-way through the project, which slowed
forward progress and prevented us from automating tests to the extent that we
would have liked.

The second challenge was that the structure of the Archives data was very
hierarchical, while that of the Museums was not. We decided on the basis of
discussions with our client that, although the hierarchy is of use to the
Archivists themselves, it would have been for our purposes an unnecessary and
substantial complication. We thus decided to represent the data using a
relational database, as it provided us with a starting point for the
infrastructure of our app that was based on technology that we already
understood. This would probably not have been possible had we decided to retain
the full hierarchy, as we would have had to use a non-relational data store in
order to easily represent it.

The third challenge was processing the client's data in the form that they were
best able to provide it. Without interfacing directly with the Museum's
archiving software (which we deemed to be out of scope for the project, since
the software is proprietary and developed by a third party), the only viable
way for the client to provide us with data was via enormous CSV files. The
information provided in said files differed between the Archives and Museums,
and the data were extremely inconsistent, especially with respect to date
formatting. Providing users with the ability to search historical artifacts by
date is of paramount importance, but we could find no better way of normalizing
the date entries than by writing a large number of regular expressions and
corresponding handlers. This was very time-consuming, and we were unable to
reach full converage due to some very bizarre formatting present in the data
provided.

Additionally, having to populate a relational database by processing a CSV file
line-by-line severely restricted our choice of schema. Items have to be added in
large batches (to minimize the number of SQL queries and enforce a reasonable
upper bound on the memory usage of the ORM library we used, thereby ensuring reasonable
processing times). Each item has a foreign key into the collection table, which
means that all of the collections to which the items in a batch belong must
already have been added before attempting to insert the batch. Likewise, each
collection has a foreign key to a department, so the same principle
applies.

Finally, in the client's data there is a many-many relationship between
multimedia items (usually photographs) and items. This could not be represented
using a Multimedia table and a join table between it and the Item table (the
ideal relational solution) because this approach could not be trivially
reconciled with the batch-insertion difficulty explained previously. Due to
time limitations, in this instance we had to violate the principles of database
normalization and store the multimedia reference numbers as a comma-delimited
string attribute of items. Also due to time limitations, the user may only view
the first image associated with an item, even if there are multiple images
available; so this violation is currently unnecessary.

## Appraisal

In retrospect, we could have generated numerous intermediate representations of
the data from the raw CSV files, and processed those separately to allow use of
a fully normalized relational schema. However we are still unsure how this
would work given the performance restrictions required of our ORM
system. (Alternatively we could have explored NoSQL representations, which
would also have enabled us to preserve data hierarchy where present.)
Additionally, we thought it wise to allow users to update the database using a
simple format that they can easily generate using their current system, and the
CSV format served that purpose. Introducing an alternative format, while useful
for our purposes, would increase the learning curve for users of our system.

