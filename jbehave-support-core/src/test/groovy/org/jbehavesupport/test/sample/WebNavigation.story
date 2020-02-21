Narrative:
In order to explain how web testing works
As a development team
I want to show you how to navigate on web pages

Scenario: Test browser navigation

!-- Open web browser on page from configuration
Given [TEST] homepage is open
Then on [home] page wait until [@title] has text Marvelous

!-- Open web browser on page from a configuration with suffix
Given [TEST]/[search.html] url is open
Then on [search] page wait until [@url] has text search.html

!-- Navigate back (the same as using browser back button)
When navigated back
Then on [home] page wait until [@title] has text Marvelous

!-- Navigate forward (the same as using browser back button)
When navigated forward
Then on [search] page wait until [@url] has text search.html

!-- Open new tab and focus it
Then open and focus new tab

!-- Open web browser on page from a configuration with suffix (on focused tab)
Given [TEST]/[frames.html] url is open
Then on [frames] page wait until [@url] has text frames.html

!-- Open new tab and focus it
Then open and focus new tab

!-- Open web browser on page from a configuration with suffix (on focused tab)
Given [TEST]/[index.html] url is open

!-- Focus tab, which url contains 'search'
Then tab with [url] containing [search] is focused
Then on [search] page wait until [@url] has text search.html

!-- Focus tab, which title contains 'Marvelous Frames'
Then tab with [title] containing [Marvelous Frames] is focused
Then on [frames] page wait until [@url] has text frames.html

!-- Focus an inner frame of page
Given on page [frames] frame [firstFrame] is focused

!-- Check element property inside frame
Then on [frames] page these conditions are verified:
| element       | property | data |
| numberOfFrame | TEXT     | 1    |

!-- Focus an inner frame of frame
Given on page [frames] frame [secondFrame] is focused

!-- Check element property inside frame
Then on [frames] page these conditions are verified:
| element       | property | data |
| numberOfFrame | TEXT     | 2    |

!-- Focus an inner frame of ...
Given on page [frames] frame [thirdFrame] is focused

!-- Check element property inside frame
Then on [frames] page these conditions are verified:
| element       | property | data |
| numberOfFrame | TEXT     | 3    |

!-- Focus page again
Given main frame is focused

!-- Check element property inside frame
Then on [frames] page these conditions are verified:
| element       | property | data |
| numberOfFrame | TEXT     | 0    |
