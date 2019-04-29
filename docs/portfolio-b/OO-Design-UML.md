# Object-Oriented Design & UML

## Overview

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
order to represent it.

The third challenge was processing the client's data in the form that they were
best able to provide it. Without interfacing directly with the Museum's
archiving software (which we deemed to be out of scope for the project, since
the software is proprietary and developed by a third party), the only viable
way for the client to provide us with data was via enourmous CSV files. The
information provided in said files differed between the Archives and Museums,
and the data were extremely inconsistent, especially with respect to date
formatting. Providing users with the ability to search historical artifacts by
date is of paramount importance, but we could find no better way of normalizing
the date entries than by writing a large number of regular expressions and
corresponding handlers. This was very time-consuming, and we were unable to
reach full converage due to some very bizarre formatting present in the data
provided.

Finally, having to populate a relational database by processing CSV files
line-by-line severely restricted our choice of schema: Items had to be added in
large batches (to minimize the number of SQL queries, ensuring reasonable
processing times)

The architecture of our application was determined primarily by 

## Backend Design

Simplified schema... dicuss pros and cons...

![](../diagrams/schema.png?raw=true)

## OO Structure

Uses Spring Web MVC etc... see Autowired relationships...

![](../diagrams/code-structure.png?raw=true)

