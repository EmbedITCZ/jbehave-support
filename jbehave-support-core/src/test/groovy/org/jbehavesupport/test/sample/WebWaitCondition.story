Narrative:
In order to explain how web testing works
As a development team
I want to show you how to use dinamic waits on web pages

Scenario: Test web wait conditions

!-- Open web browser on page from a configuration with suffix
Given [TEST]/[wait-condition-test.html] url is open

!-- Wait until an element text property contains given string
Then on [home] page wait until [@url] has text wait-condition-test
Then on [home] page wait until [@title] has text WebWaitCondition
Then on [home] page wait until [@title] has text WebWaitCondition test page
Then on [home] page wait until [@title] has text {CONCAT:Web:Wait:Condition test page}

!-- Wait until an element text property doesn't contain given string
Then on [home] page wait until [@url] missing text XXX
Then on [home] page wait until [@title] missing text XXX

!-- Wait until an element is enabled, exist, visible or not visible
Then on [home] page wait until [#isClickable] is clickable
Then on [home] page wait until [#isPresent] is present
Then on [home] page wait until [#isVisible] is visible
Then on [home] page wait until [#isNotVisible1] is not visible
Then on [home] page wait until [#isNotVisible2] is not visible

!-- Wait until an element text property contains given string, and some more attributes
Then on [home] page wait until [#hasText] has text ipsum
Then on [home] page wait until [#hasAttribute] has class foo
Then on [home] page wait until [#hasAttribute] has my-attribute
Then on [home] page wait until [#hasAttribute] has my-attribute bar

!-- Wait until an element doesn't have some attributes
Then on [home] page wait until [#hasText] missing text XXX
Then on [home] page wait until [#missingText] missing text
Then on [home] page wait until [#hasAttribute] missing class XXX
Then on [home] page wait until [#hasAttribute] missing my-attribute XXX
Then on [home] page wait until [#hasAttribute] missing xxx-attribute
