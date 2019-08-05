Scenario: Test connection and log data from start of the test
Then the following data are present in [TEST] log:
| header     |
| long       |
| unexpected |

Scenario: Test data are not present in log
Then the following data are not present in [TEST] log:
| header |
| fubar  |

Scenario: Test verifier support when searching from log
Then the following data are present in [TEST] log:
| header     | verifier    |
| ^some.*$   | REGEX_MATCH |
| unexpected |             |

Scenario: Test log data from specific start and end timestamp
Given log read start timestamp is set to now
Given log read end timestamp is set to now
Then the following data are present in [TEST] log:
| header |
| also   |
| sharp  |
Then the following data are present in [TEST] log:
| header |
| also   |
| sharp  |

Scenario: Test log data from specific start and end timestamp saved as into context
Given log timestamp is saved as [START]
Given log timestamp is saved as [END]
Given log read start timestamp is set to saved value [{CP:START}]
Given log read end timestamp is set to saved value [{CP:END}]
Then the following data are present in [TEST] log:
| header |
| also   |
| sharp  |
