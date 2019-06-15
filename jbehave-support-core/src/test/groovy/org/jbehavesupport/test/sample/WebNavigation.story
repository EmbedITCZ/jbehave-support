Scenario: Test browser navigation

Given [TEST] homepage is open
Then on [home] page wait until [@title] has text Marvelous

Given [TEST]/[search.html] url is open
Then on [search] page wait until [@url] has text search.html

When navigated back
Then on [home] page wait until [@title] has text Marvelous

When navigated forward
Then on [search] page wait until [@url] has text search.html

Then open and focus new tab

Given [TEST]/[title.html] url is open
Then on [title] page wait until [@url] has text title.html

Then open and focus new tab

Given [TEST]/[index.html] url is open

Then tab with [url] containing [search] is focused
Then on [search] page wait until [@url] has text search.html

Then tab with [title] containing [Marvelous Title] is focused
Then on [title] page wait until [@url] has text title.html

Given on page [title] frame [firstFrame] is focused

Then on [title] page these conditions are verified:
| element       | property | data |
| numberOfFrame | TEXT     | 1    |

Given on page [title] frame [secondFrame] is focused

Then on [title] page these conditions are verified:
| element       | property | data |
| numberOfFrame | TEXT     | 2    |

Given on page [title] frame [thirdFrame] is focused

Then on [title] page these conditions are verified:
| element       | property | data |
| numberOfFrame | TEXT     | 3    |

Given main frame is focused

Then on [title] page these conditions are verified:
| element       | property | data |
| numberOfFrame | TEXT     | 0    |
