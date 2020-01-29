Scenario: test success status verification
When [POST] request to [TEST]/[mirror/] is sent with data:
| name             | data        |
| httpStatus       | BAD_REQUEST |
| payload[0]       | happy       |
Then response from [TEST] REST API is successful

Scenario: test success result verification
When [POST] request to [TEST]/[mirror/] is sent with data:
| name             | data        |
| httpStatus       | CREATED     |
| payload[0]       | sad         |
Then response from [TEST] REST API is successful

Scenario: test user input after success result
When [POST] request to [TEST]/[mirror/] is sent with data:
| name             | data        |
| httpStatus       | CREATED     |
| payload[0]       | happy       |
| payload[1]       | astronaut   |
Then response from [TEST] REST API is successful and values match:
| name       | expectedValue |
| payload[1] | hippopotamus  |
