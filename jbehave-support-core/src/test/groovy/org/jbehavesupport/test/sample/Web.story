Narrative:
In order to explain how web testing works
As a development team
I want to show you basic web testing examples

Scenario: Test open steps

!-- Open web browser on given url
Given [https://google.com] url is open
Then on [home] page wait until [@title] has text Google

!-- Open web browser on page from configuration
Given [TEST] homepage is open
Then on [home] page wait until [@title] has text Marvelous

!-- Open web browser on page from a configuration with suffix
Given [TEST]/[search.html] url is open
Then on [search] page wait until [@url] has text search.html

!-- Open web browser on page from a configuration with suffix with ID from test context
Given the value [123] is saved as [ID]
Given [TEST]/[search.html?id={CP:ID}] url is open
Then on [search] page wait until [@url] has text search.html?id=123

!-- Open web browser on page from a configuration with query parameters
Given [TEST]/[search.html] url is open with query parameters:
| name | data  |
| name | foo   |
| sort | asc   |
Then on [search] page wait until [@url] has text search.html?name=foo&sort=asc

Scenario: Simple WEB test. Pages are static. Id in the second step is irrelevant

!-- Open web browser on page from configuration
Given [TEST] homepage is open

!-- Perform action on page: fill text into an element and click on submit button
When on [home] page these actions are performed:
| element | action | data                           |
| id      | FILL   | {RANDOM_NUMBER_IN_RANGE:1:100} |
| submit  | CLICK  |                                |

!-- Verify elements properties on page
Then on [search-result] page these conditions are verified:
| element   | property | data       |
| id        | TEXT     | 85         |
| firstName | TEXT     | wszystkich |
| lastName  | TEXT     | biurowych  |

Scenario: Test implicit web mapping

!-- Open web browser on page from configuration
Given [TEST] homepage is open

!-- Perform action on page: fill text into an element with id '#search-id'
When on [home] page these actions are performed:
| element    | action | data  |
| #search-id | FILL   | 85    |

!-- Verify element properties on page
Then on [home] page these conditions are verified:
| element | property | data  |
| id      | VALUE    | 85    |

Scenario: Test implicit web mapping for unknown page

!-- Open web browser on page from configuration
Given [TEST] homepage is open

!-- Perform action on page: fill text into an element with id '#search-id'
When on [unknown-page] page these actions are performed:
| element    | action | data  |
| #search-id | FILL   | 85    |

!-- Verify element properties on page
Then on [home] page these conditions are verified:
| element | property | data  |
| id      | VALUE    | 85    |

Scenario: Implemetation of default elements

!-- Open web browser on given url
Given [https://google.com] url is open
Then on [unknown-page] page wait until [@url] has text www.google.com
Then on [unknown-page] page wait until [@title] has text Google

!-- Verify element properties on page using default elements (@url and @title)
Then on [unknown-page] page these conditions are verified:
| element | property | data                    | verifier     |
| @url    | TEXT     | https://www.google.com/ | EQ           |
| @title  | VALUE    | Google                  | EQ           |
| @url    | TEXT     | dummy                   | NOT_CONTAINS |
| @title  | VALUE    | dummy                   | NOT_CONTAINS |