# Overview

In this project we worked with Bristol Culture, which incorporates Bristol Museums and Bristol Archives. The Archives looks after the official archives of the city of Bristol, besides collecting and preserving many other records relating to the city and surrounding area for current and future generations to consult. The collections are being constantly expanded and digitized. The Archives are open to the general public and are used by laypeople and professional researchers alike.

Our client invited us to improve upon the current available methods of searching the Archive collections. They currently have a searching system which has been identified with the following weaknesses:

* There are two different search engines for two different collections.
* The first supports advanced searching and gives quite relevant results, but
  provides only references numbers and other metadata (no thumbnails/media
  access).
* The second lacks an advanced search option, but does provide
  images of artefacts. Instead of searching, one can navigate the hierarchy of
  collections directly, but this is very slow.
* Artefacts that have not yet been digitized and those with copyright
  restrictions may only be viewed on-site at the Archives. Likewise for some
  digital media such as audio and video.
* Such digital media may only be viewed on a single (offline) Archive
  computer, which lists items by reference numbers only. So one must first find
  reference numbers using the search engines on the other computers, then look
  them up on the multimedia computer.

Our job is to design and build a web application that provides a fast, unified user
interface for searching viewing all media available, and hopefully good enough to replace the existing search tools. 

Our vision for the product that will provide a suitable solution:
* The web-app should be able to access to all media collections. To do this we would first need a normalization of the data since different collections have different metadata and hierarchy structures.
* The web-app should provide three interfaces: for normal users it should support "simple search" which performs a string-matching search with key words; for archive researchers it should have "advanced search" which can perform search according to various criteria (dates, locations etc.) with filters; and for the archive administrators there should be a management system to upload new entries. 
* To speed up the access, we can let the web-app perform database queries, and then fetch the thumbnails and images through the Bristol Museum image RESTful API for displaying the results.

In the project we have used spring boot framework, along with mySQL in the back-end and thymeleaf in the front-end. The implementation will be described in detail in the OO Design section.

