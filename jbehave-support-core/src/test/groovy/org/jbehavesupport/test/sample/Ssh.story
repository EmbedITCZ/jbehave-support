Scenario: Test connection and log data from start of the test
Then the following data are present in [TEST] log:
| header     |
| long       |
| unexpected |

Scenario: Test log data from specific start time
Given log timestamp is saved as [START_TIMESTAMP]
Then the following data are present in [TEST] log since [START_TIMESTAMP]:
| header |
| also   |
| sharp  |

Scenario: Test data are not present in log
Then the following data are not present in [TEST] log:
| header |
| fubar  |

Scenario: Test verifier support when searching from log
Then the following data are present in [TEST] log:
| header     | verifier    |
| ^some.*$   | REGEX_MATCH |
| unexpected |             |
