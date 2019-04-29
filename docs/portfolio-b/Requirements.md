# Requirements

## Stakeholders

### Human Stakeholders

* Researchers: Professionals who need to quickly and accurately access key data from the archives that is relevant to their job.
* Public User: Members of the general public with an interest in the contents of the archive. Their use of the system is more casual than researchers but still has similar requirements.
* Archives staff: Staff at the archive want to improve the accessibility of their collections,
  and need software infrastructure that eases their job as maintainers and
  digitizers. This we know from speaking to them directly.
* We, the developers have an interest in delivering a product, that meets the
  needs of the client and users, whilst developing our skills and understanding of complex real world systems.
* In our opinion it is in the public interest to document and make accessible
  information about local and national history, so the general public is also
  an interested party.
* Bristol City Council/Bristol Museums: As the funders of Bristol Archives their goals are to make history more accesible via an improved system and to have a more efficient system that would result in reduced costs.
* Producers of archive documents: these would include writers, photographers etc. whose work is preserved at the archives.

### Non-human Stakeholders

* City of Bristol: The archives document its history and it is in its interest that it is efficiently maintained in a database such as the one we built.
* The documents at the archives: This represents the documents such as videos, photos, audio files etc. the data of which is stored in our database.
* England: The country preserves its history through archives in its cities, one of which is Bristol.
* Humanity: archives are important for the world population as they help preserve important historical artifacts.

## Use-cases

The diagram below reflects the current use-cases of the Archives:

![](../graphviz/domain-model.png?raw=true)
Diagram 1: current way of using the archives

Our architecture supports two main uses to get access to archive documents. Our goal was to remove obstacles and conflicting alternatives on the users' path
to the artifacts they need, as illustrated below:

![](../graphviz/reqs2.png?raw=true)
Diagram 2: our basic goal for the web app

To that end, our main goal was to have a simple search. After implementing that, giving the users the option to carry out a more specific search seemed like the most logical step forward. Our architecture also supports a way to add entries to the databases. This may be done after an admin login.

Below are some illustrative scenarios for the current system:

Scenario | Current path to goal | Path to goal implemented
---------|----------------------|----------------------
A layperson wants to find images of Castle Street before the war, to see their ancestors' house that was bombed. | Archives search &rightarrow; Ref. no &rightarrow; on-site &rightarrow; multimedia machine | Quick search using our web app &rightarrow; directly view or get preview of available data
A researcher wants to find media relating to the social impact on the local population of the British Empire's colonisation of Kenya. | BECC search online &rightarrow; irrelevant images of Kenya, then BECC navigation &rightarrow; Slow &rightarrow; Relevant-sounding video (ref. no) &rightarrow; On-site &rightarrow; multimedia machine | Advanced search by location and date using our web app &rightarrow; directly view media

## Use-case goals

### Access items via search

Most conventional flow:
* Enter keywords using the search bar.
* View results in search results page.
* Navigate to relevant item and view it.

![](../graphviz/simple_search.png?raw=true)
Diagram 3: simple search use-case

Alternative flow:
* Enter advanced search window.
* Enter keywords using the search bar and set any necessary filters within advanced search window.
* View results in search results page.
* Navigate to relevant item and view it.

![](../graphviz/advanced_search.png?raw=true)
Diagram 4: advanced search use-case

Exceptional flow 1:
* Enter keywords using the search bar.
* If no results are returned based on keywords entered the user will be prompted to enter new keywords.
* Use the search bar at the top of the results page to search again.

Exceptional flow 2:
* Enter advanced search window with specific query in mind.
* No results are returned.
* Try a more general search using simple search.

### Add entries to database

Most conventional flow:
* Enter admin login page.
* Log in with appropriate admin credentials.
* Select CSV file of entries to add to database and click add entries.

![](../graphviz/add_entries.png?raw=true)
Diagram 5: database management use-case

Exceptional flow:
* Enter admin login page.
* Try logging in with incorrect credentials.
* Redirect to homepage triggered.

## Goal of paramount importance: simple search
Simple search is a key use-case goal and essentially the core of our system. It is in fact of interest to the researchers looking for items relating to a general concept. But it is also of interest to the general public as they are more likely to have
more vague ideas of what they would like to observe and therefore would use simple search rather than advanced search. Hence, the most conventional flow of accessing items via search is the most important. It is also the component which took the most time
work.

Here is an exhaustive list of a conventional flow steps:
1. Enter keywords into search bar.
2. Press search button.
    * The keywords are then parsed by thymeleaf and passed to the backend.
    * SQL for database query generated to search for keywords in all columns of the database.
    * Union of queries is returned.
3. View the search results page.
    * Displays list of items.
    * Each item has its title, collection and reference number displayed, as well as a thumbnail.
4. Click thumbnail or title of relevant record to go to the record page.
5. Read record information and description.
    * Each item page includes more information such as a description, dates on which the item was created and an enlarged image of document.
6. Image on item page may be enlarged to examine it better.
7. When finished press home button to return to the home page.

### Functional Requirements
* Search function must be run when the search button is pressed and keywords have been entered into search bar.
* Not inputting any keywords must redirect user to homepage.
* Results page must enable user to browse all items relating to keywords.
* Pressing each menu item must take the user to the relevant page of the item clicked.
* Must display database items in pre-defined format based on search terms.
* Only relevant characters can be entered into search function.
* Users should only be able to view the record items which they are authorised to see.
* Images must be big enough to inspect on item pages.
* Relevant information corresponding to correct to item must be displayed on item page.
* Keywords must be searched for in all attributes of item (i.e. because keywords can be dates, locations etc.) 

### Non-Functional Requirements
* Search function must be fast enough to not cause any inconvenience to the user.
* Search results must be paginated as to not cause any lag in the browser or inconvenience to the user.
* Search results must have an order of relevance as to be convenient to browse for the user.
* Search results should be accurate and relevant.
* Repeat searches with the same keywords should provide consistent replicable results.
* The search system should be easy to understand and access for any user.
* Function should be memory and space efficient to reduce system costs and power use.
