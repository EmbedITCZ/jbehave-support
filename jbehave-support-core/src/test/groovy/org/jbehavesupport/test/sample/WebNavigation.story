Scenario: Test browser navigation

Given [TEST] homepage is open
Then on [home] page wait until [@title] has text Marvelous

Given [TEST]/[search.html] url is open
Then on [search] page wait until [@url] has text search.html

When navigated back
Then on [home] page wait until [@title] has text Marvelous

When navigated forward
Then on [search] page wait until [@url] has text search.html
