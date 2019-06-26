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

Given [TEST]/[frames.html] url is open
Then on [frames] page wait until [@url] has text frames.html

Then open and focus new tab

Given [TEST]/[index.html] url is open

Then tab with [url] containing [search] is focused
Then on [search] page wait until [@url] has text search.html

Then tab with [title] containing [Marvelous Frames] is focused
Then on [frames] page wait until [@url] has text frames.html

Given on page [frames] frame [firstFrame] is focused

Then on [frames] page these conditions are verified:
| element       | property | data |
| numberOfFrame | TEXT     | 1    |

Given on page [frames] frame [secondFrame] is focused

Then on [frames] page these conditions are verified:
| element       | property | data |
| numberOfFrame | TEXT     | 2    |

Given on page [frames] frame [thirdFrame] is focused

Then on [frames] page these conditions are verified:
| element       | property | data |
| numberOfFrame | TEXT     | 3    |

Given main frame is focused

Then on [frames] page these conditions are verified:
| element       | property | data |
| numberOfFrame | TEXT     | 0    |
