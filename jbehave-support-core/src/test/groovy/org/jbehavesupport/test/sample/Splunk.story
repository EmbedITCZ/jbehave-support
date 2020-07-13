Scenario: Test data are present in Splunk search result
Given the value [30a3d60bd3d698eae25eaf5afe3e1df5] is saved as [TRACEID_VARIABLE]
When the Splunk search query is performed:
search index="main" namespace="*" level="*" message="*response*" traceId="{CP:TRACEID_VARIABLE}" | tail 1
Then the Splunk search result set has 1 row(s)
Then the Splunk search result match these rules:
| data                                              | verifier        |
| ^.*200 OK with headers.*$                         | REGEX_MATCH     |
| X-B3-TraceId:"30a3d60bd3d698eae25eaf5afe3e1df5"   | CONTAINS        |
| does not contain                                  | NOT_CONTAINS    |
|                                                   | NOT_NULL        |
| no equality                                       | NE              |

Scenario: Test data are present in Splunk search result within given time range
Given the value [30a3d60bd3d698eae25eaf5afe3e1df5] is saved as [TRACEID_VARIABLE]
And the value [2020-07-10T00:00:00.000+02:00] is saved as [EARLIEST_TIME_VARIABLE]
And the value [2020-07-20T23:59:59.000+02:00] is saved as [LATEST_TIME_VARIABLE]
When the Splunk search query is performed within [{CP:EARLIEST_TIME_VARIABLE}] and [{CP:LATEST_TIME_VARIABLE}]:
search index="main" namespace="*" level="*" message="*response*" traceId="{CP:TRACEID_VARIABLE}" | tail 1
Then the Splunk search result set has 1 row(s)
Then the Splunk search result match these rules:
| data                                              | verifier        |
| ^.*200 OK with headers.*$                         | REGEX_MATCH     |

Scenario: Test data are not present in Splunk search result
Given the Splunk search query is performed:
search index="main" namespace="no data" message="*"
Then the Splunk search result set has 0 row(s)