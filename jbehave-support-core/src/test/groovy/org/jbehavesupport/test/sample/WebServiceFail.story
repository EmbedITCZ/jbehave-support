A story is collection of scenarios for testing fault handler

Narrative:
In order to test ws faulhandler in jbehave-support-core
As a development team
I want to confirm correct functionality

Scenario: WS test call with fault handler

Given [NameRequest] data for [TEST]:
| name | data |
| name | fail |
When [NameRequest] is sent to [TEST] with fault:
| name      | expectedValue |
| faultCode | Server        |
