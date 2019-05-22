Given [TEST]/[wait-condition-test.html] url is open

Then on [home] page wait until [@url] has text wait-condition-test
Then on [home] page wait until [@title] has text WebWaitCondition
Then on [home] page wait until [@title] has text WebWaitCondition test page
Then on [home] page wait until [@title] has text {CONCAT:Web:Wait:Condition test page}

Then on [home] page wait until [@url] missing text XXX
Then on [home] page wait until [@title] missing text XXX

Then on [home] page wait until [#isClickable] is clickable
Then on [home] page wait until [#isPresent] is present
Then on [home] page wait until [#isVisible] is visible
Then on [home] page wait until [#isNotVisible1] is not visible
Then on [home] page wait until [#isNotVisible2] is not visible

Then on [home] page wait until [#hasText] has text ipsum
Then on [home] page wait until [#hasAttribute] has class foo
Then on [home] page wait until [#hasAttribute] has my-attribute
Then on [home] page wait until [#hasAttribute] has my-attribute bar

Then on [home] page wait until [#hasText] missing text XXX
Then on [home] page wait until [#missingText] missing text
Then on [home] page wait until [#hasAttribute] missing class XXX
Then on [home] page wait until [#hasAttribute] missing my-attribute XXX
Then on [home] page wait until [#hasAttribute] missing xxx-attribute
