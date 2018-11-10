# Overview

Our client is Bristol Culture, which incorporates Bristol Museums and Bristol
Archives. The Archives are open to the general public and are used by laypeople
and professional researchers alike. The collections are being constantly
expanded and digitized.

Our client invited us to improve upon the current available methods of
searching the Archive collections. We have identified several weaknesses of the
current system, which are detailed below:

* There are two different search engines for two different collections.
* The first supports advanced searching and gives quite relevant results, but
  provides only references numbers and other metadata (no thumbnails/media
  access).
* The second lacks an advanced search option, but does provide at least provide
  images of artifacts. Instead of searching, one can navigate the hierarchy of
  collections directly, but this is very slow.
* Artifacts that have not yet been digitized and those with copyright
  restrictions may only be viewed on-site at the Archives. Likewise for some
  digital media such as audio and video.
* Such digital media may only be viewed on a single (offline) Archive
  computer, which lists items by reference numbers only. So one must first find
  reference numbers using the search engines on the other computers, then look
  them up on the multimedia computer.

By their nature, we will not be able to addess the issues of copyright
restrictions and incomplete digitization of artifacts. However the other issues
can be addressed.

To that end, we plan to build a web application that provides a unified user
interface for both searching media and viewing it. Initially (as our Minimal
Viable Product) this will be deployed on-site and offline only: this will allow
us to avoid potential copyright issues while we develop the product, and limit
the scope of the project until we have evaluated the effectiveness of our
MVP. The app is intended to replace the two (or three, if you include the
multimedia machine) existing search engines in on-site use. If successful, and
pending the resolution of various technical and other issues, the app could be
extended to be deployable online for the general public.

The app should support advanced searches: by various criteria, with filters.
Our MVP should support thumbnail images at a minimum, though should preferably
allow images to be enlarged. Audio and video support should be added later.

Whether or not we provide user accounts (for things like bookmarks, recently
viewed artifacts, or (for Archive staff only) adding artifacts to the database)
depends on what additional information feedback we receive from the client and
users.
