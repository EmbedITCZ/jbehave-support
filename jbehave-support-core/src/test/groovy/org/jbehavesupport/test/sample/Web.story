Scenario: Test open steps

Given [https://google.com] url is open
Then on [home] page wait until [@title] has text Google

Given [TEST] homepage is open
Then on [home] page wait until [@title] has text Marvelous

Given [TEST]/[search.html] url is open
Then on [search] page wait until [@url] has text search.html

Given the value [123] is saved as [ID]
Given [TEST]/[search.html?id={CP:ID}] url is open
Then on [search] page wait until [@url] has text search.html?id=123

Given [TEST]/[search.html] url is open with query parameters:
| name | data  |
| name | foo   |
| sort | asc   |
Then on [search] page wait until [@url] has text search.html?name=foo&sort=asc


Scenario: Simple WEB test. Pages are static. Id in the second step is irrelevant

Given [TEST] homepage is open

When on [home] page these actions are performed:
| element | action | data                           |
| id      | FILL   | {RANDOM_NUMBER_IN_RANGE:1:100} |
| submit  | CLICK  |                                |

Then on [search-result] page these conditions are verified:
| element   | property | data       |
| id        | TEXT     | 85         |
| firstName | TEXT     | wszystkich |
| lastName  | TEXT     | biurowych  |



Scenario: Test implicit web mapping

Given [TEST] homepage is open

When on [home] page these actions are performed:
| element    | action | data  |
| #search-id | FILL   | 85    |

Then on [home] page these conditions are verified:
| element | property | data  |
| id      | VALUE    | 85    |



Scenario: Test implicit web mapping for unknown page

Given [TEST] homepage is open

When on [unknown-page] page these actions are performed:
| element    | action | data  |
| #search-id | FILL   | 85    |

Then on [home] page these conditions are verified:
| element | property | data  |
| id      | VALUE    | 85    |

Scenario: Implemetation of default elements

Given [https://google.com] url is open

Then on [unknown-page] page wait until [@url] has text www.google.com

Then on [unknown-page] page wait until [@title] has text Google

Then on [unknown-page] page these conditions are verified:
| element | property | data                    | verifier     |
| @url    | TEXT     | https://www.google.com/ | EQ           |
| @title  | VALUE    | Google                  | EQ           |
| @url    | TEXT     | dummy                   | NOT_CONTAINS |
| @title  | VALUE    | dummy                   | NOT_CONTAINS |