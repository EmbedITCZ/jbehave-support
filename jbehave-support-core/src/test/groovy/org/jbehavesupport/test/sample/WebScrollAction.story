Narrative:
In order to test SCROLL_ON web action and DISPLAYED_ON_SCREEN web property
As a development team
I want to use those two in short test

Scenario: Test SCROLL_ON web action

Given [TEST]/[action-test.html] url is open

Then on [home] page these conditions are verified:
| element      | property            | data  |
| #bottom-text | DISPLAYED_ON_SCREEN | false |

When on [home] page these actions are performed:
| element     | action    |
| #bottom-text| SCROLL_ON |

Then on [home] page these conditions are verified:
| element      | property            | data |
| #bottom-text | DISPLAYED_ON_SCREEN | true |
