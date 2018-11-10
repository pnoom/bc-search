
## Application Architecture

The below diagram illustrates the inital structure of our application:

![](graphviz/architecture1.png?raw=true)

Input will be received using HTML forms in the View layer, which will invoke
HTTP requests to the Controller layer. This will determine the appropriate
response - often by first referring to the Model - and trigger a corresponding
change in the View.

# TODO

Design the actual interfaces so that we can produce static and dynamic UML
diagrams.
