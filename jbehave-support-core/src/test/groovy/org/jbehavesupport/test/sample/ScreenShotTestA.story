Scenario: escaping in test context

Given [TEST] homepage is open

Then on [home] page wait until [@title] has text Marvelous

When screenshot is taken

Given [TEST]/[frames.html] url is open

Then on [frames] page wait until [@url] has text frames.html

When screenshot is taken
