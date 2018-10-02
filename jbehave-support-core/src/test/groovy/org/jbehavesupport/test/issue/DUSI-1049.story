Scenario: Ws scenario to confirm send request to WS and validate response

Given [NameRequest] data for [TEST]:
| name | data |
| name | test |
When [NameRequest] is sent to [TEST] with success
Then [NameResponse] values from [TEST] match:
| name      | expectedValue | verifier |
| firstName | John          | EQ       |
| lastName  | Doe           | EQ       |
| age       | 33            | EQ       |
| married   | true          | EQ       |
| parent    | true          | EQ       |
