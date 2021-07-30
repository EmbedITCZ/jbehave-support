Scenario: Test connection and log data from start of the test
Given ssh test data are filled
Then the following data are present in [TEST] log:
| header     |
| long       |
| unexpected |

Scenario: Test data are not present in log
Given ssh test data are filled
Then the following data are present in [TEST] log:
| header | verifier     |
| fubar  | NOT_CONTAINS |

Scenario: Test verifier support when searching from log
Given ssh test data are filled
Then the following data are present in [TEST] log:
| header     | verifier    |
| ^.*some.*$ | REGEX_MATCH |
| unexpected |             |

Scenario: Test log data from specific start and end timestamp
Given log start timestamp is set to current time
!-- Fill you logs (via calling some app etc)
And ssh test data are filled
Given log end timestamp is set to current time
Then the following data are present in [TEST] log:
| header |
| also   |

Then the following data are present in [TEST] log:
| header |
| also   |

Scenario: Test log data from specific start and end timestamp saved as into context
Given current time is saved as log timestamp [START_1]
!-- Fill you logs (via calling some app etc)
And ssh test data are filled
Given current time is saved as log timestamp [END_1]
Given current time is saved as log timestamp [START_2]
!-- Fill you logs again (via calling some app etc)
And ssh test data are filled
Given current time is saved as log timestamp [END_2]

Given log start timestamp is set to [{CP:START_1}]
Given log end timestamp is set to [{CP:END_1}]
!-- Read logs from first app
Then the following data are present in [TEST] log:
| header |
| sharp  |

Given log start timestamp is set to [{CP:START_2}]
Given log end timestamp is set to [{CP:END_2}]
!-- Read logs from second app
Then the following data are present in [TEST] log:
| header |
| also   |
