Narrative:
In order to explain how web testing works
As a development team
I want to show you how given stories works

Scenario: Story using prerequisite story

!-- Run another story
GivenStories:
org/jbehavesupport/test/sample/WebGivenStoryPrerequisite.story

!-- Verify that web browser is still open and value from prerequisite is filled
Then on [home] page these conditions are verified:
| element | property | data  |
| id      | VALUE    | 55    |
