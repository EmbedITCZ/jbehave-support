Scenario: escaping in test context

Given [TEST]/[frames.html] url is open

Then on [frames] page wait until [@url] has text frames.html

When screenshot is taken
