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
substantial complication. From the start we 

We thus decided t represent the data using a
relational database, as it provided us with a starting point for the
infrastructure of our app that we already understood.

Preserving the representation
of the hierarchy would

The third challenge was processing the client's data in the form that they were
best able to provide it.



The architecture of our application was determined primarily by 

## Backend Design

Simplified schema... dicuss pros and cons...

![](../diagrams/schema.png?raw=true)

## OO Structure

Uses Spring Web MVC etc... see Autowired relationships...

![](../diagrams/code-structure.png?raw=true)

