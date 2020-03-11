Narrative:
In order to show as many things as possible in report
As a development team
I want to  create one single scenario coverring all report extensions

Scenario: Full report example scenario

!-- Save some values into context
Given the following values are saved:
| name             | data                                                        |
| ANY_VALUE        | any value                                                   |
| MULTI_LINE_VALUE | first line{UNESCAPE:\n}second line{UNESCAPE:\n}another line |
| RANDOM_STRING    | {RANDOM_STRING:15}                                          |

!-- Publish a JSM message
Given [NameRequest] data for JMS broker [TEST]:
| name                     | data                  |
| name                     | test                  |
| CUID                     | {RANDOM_STRING:7}     |
| address.0                | {RANDOM_EMAIL}        |
| passDate                 | {RANDOM_DATE}         |
| maxResults               | {RANDOM_NUMBER:9}     |
| @header.JMSDestination   | queue                 |
| @header.JMSDeliveryMode  | 1                     |
| @header.JMSExpiration    | {RANDOM_NUMBER:2}     |
| @header.JMSPriority      | {RANDOM_NUMBER:1}     |
| @header.JMSMessageID     | {RANDOM_STRING:5}     |
| @header.JMSTimestamp     | {RANDOM_NUMBER:10}    |
| @header.JMSCorrelationID | CID:{RANDOM_STRING:5} |
| @header.JMSReplyTo       | reply back            |
| @header.JMSType          | not my type           |
| @header.JMSRedelivered   | false                 |

When [NameRequest] is sent to destination [QUEUE] on JMS broker [TEST]

!-- Send a POST request a save values
When [POST] request to [TEST]/[user/] is sent with data:
| name                 | data                           | contextAlias |
| @header.Content-Type | application/json;charset=utf-8 |              |
| firstName            | Bruno                          |              |
| lastName             | {RANDOM_STRING:10}             | LAST_NAME    |

Then response from [TEST] REST API has status [200]

When response values from [TEST] REST API are saved:
| name                      | contextAlias      |
| @header.Transfer-Encoding | TRANSFER_ENCODING |

!-- Send a WEB service request and save some values
Given [NameRequest] data for [TEST]:
| name | data |
| name | test | REQUEST_NAME |

When [NameRequest] is sent to [TEST] with success

And [NameResponse] values from [TEST] are saved:
| name      | contextAlias  |
| firstName | RESPONSE_NAME |

!-- Perform an SQL update and query and save result
Given this update is performed on [TEST]:
update person set first_name = 'James' where first_name = 'John'

When this query is performed on [TEST]:
select * from person where first_name = 'James'

When these columns from the single-row query result are saved:
| name | contextAlias |
| id   | JOHN_ID      |

Given this update is performed on [TEST]:
update person set first_name = 'John' where first_name = 'James'

!-- Set Ssh reporter mode to full and check something in logs
Given ssh reporter mode is set to [FULL]

Then the following data are present in [TEST] log:
| header     |
| long       |
| unexpected |

!-- Open web browser on google.com and take screenshot
When [https://www.google.com] url is open

Then screenshot is taken
