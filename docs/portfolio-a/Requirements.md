# Requirements

## Stakeholders

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

## Use cases

The diagram below reflects the current use cases of the Archives:

![](../graphviz/domain-model.png?raw=true)

Our goal is to remove obstacles and conflicting alternatives on the users' path
to the artifacts they need, as illustrated below:

![](../graphviz/reqs2.png?raw=true)

Below are some illustrative scenarios for the current system:

Scenario | Current path to goal | Desired path to goal
---------|----------------------|----------------------
A layperson wants to find images of Castle Street before the war, to see their ancestors' house that was bombed. | Archives search &rightarrow; Ref. no &rightarrow; on-site &rightarrow; multimedia machine | On-site &rightarrow; quick search using our web app &rightarrow; directly view media
A researcher wants to find media relating to the social impact on the local population of the British Empire's colonisation of Kenya. | BECC search online &rightarrow; irrelevant images of Kenya, then BECC navigation &rightarrow; Slow &rightarrow; Relevant-sounding video (ref. no) &rightarrow; On-site &rightarrow; multimedia machine | On-site &rightarrow; advanced search by location and date using our web app &rightarrow; directly view media

## Use case goals
### Access items via search
* Enter keywords using the search bar.
* View results in search results page.
* Navigate to relevant item and view it.


Alternative flow:
* Enter advanced search window.
* Enter keywords using the search bar and set any necessary filters within advanced search window.
* View results in search results page.
* Navigate to relevant item and view it.


Exceptional flow:
* Enter keywords using the search bar.
* If no results are returned based on keywords entered the user will be prompted to enter new keywords.
* Use the search bar at the top of the results page to search again.

### Access items via browse
* Click menu item that relates to the browse functionality.
* View categories displayed within the page.
* View any items within categories that are relevant.


Exceptional flow: 
* Click menu item that relates to the browse functionality.
* View categories displayed within the page.
* If no relevant categories are displayed then the user would press the button to return to the home screen.

### Learn system
* Click menu item that relates to the guide page.
* Read system guide for general information on how to use the system.


Alternative flow:
* Click menu item that relates to the FAQ page.
* Read FAQs to deal with any specific questions or misunderstandings relating to the system.


Exceptional flow:
* User reads both the guide and the FAQ page without their specific issue being answered.
* There is a prompt on the FAQ page to send contact form to deal with said issue.


## Search
Search is a key use case goal and essentially the core of our system here is an exhaustive list of flow steps:
1. Enter keywords into search bar.
2. Press search button.
3. View the search results page.
4. Click thumbnail or title of relevant record to go to the record page.
5. Read record information and description.
6. Enlarge image or media by clicking on it's thumbnail.
7. When finished press home button to return to the home page.

### Functional Requirements
* Search function must be run when the search button is pressed and keywords have been entered into search bar.
* Pressing each menu item must take the user to the relevant page.
* Must display database items in pre-defined format based on search terms.
* Only relevant characters can be entered into search function.
* Users should only be able to view the record items which they are authorised to see.


### Non-Functional Requirements
* Search function must be fast enough to not cause any inconvenience to the user.
* Search function should be accurate and relevant.
* Repeat searches with the same keywords should provide consistent replicatable results.
* The search system should be easy to understand and access for any user.
* Function should be memory and space efficient to reduce system costs and power use.
