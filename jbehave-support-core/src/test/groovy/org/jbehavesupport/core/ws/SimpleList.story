Scenario: Ws scenario to confirm send request with string list

Given [NameRequest] data for [TEST]:
| name      | data     |
| name      | test     |
| address.0 | addres 1 |
| address.1 | addres 2 |
| address.2 | addres 3 |
When [NameRequest] is sent to [TEST] with success

