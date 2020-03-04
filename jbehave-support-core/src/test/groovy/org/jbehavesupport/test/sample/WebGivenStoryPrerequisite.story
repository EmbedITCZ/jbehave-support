Narrative:
In order to explain how web testing works
As a development team
I want to show you how given stories works

Scenario: Prerequisite story

!-- Open web browser on page from configuration
Given [TEST] homepage is open

!-- Perform action on page: fill text into element
When on [home] page these actions are performed:
| element | action | data  |
| id      | FILL   | 55    |

