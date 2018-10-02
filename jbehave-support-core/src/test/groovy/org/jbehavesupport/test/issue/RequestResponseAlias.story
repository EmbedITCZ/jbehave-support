Scenario: Ws scenario to confirm send request with alias to WS and validate response with alias

Given [AliasNameRequest] data for [TEST]:
| name | data |
| name | test |
When [AliasNameRequest] is sent to [TEST] with success
Then [AliasNameResponse] values from [TEST] match:
| name      | expectedValue | verifier |
| firstName | John          | EQ       |
| lastName  | Doe           | EQ       |

