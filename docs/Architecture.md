
## Application Architecture

The below diagram illustrates the inital structure of our application:

(TODO: add subgraph of user/admin accounts and connect using dotted lines)

![](graphviz/architecture1.png?raw=true)

The primary role of the frontend is to receive user input - in the form of
search strings and preferences, navigation through the web pages by clicking
links and so on - and display the corresponding output to the user.

Input will be received using HTML forms which will invoke HTTP requests to the
... layer...

