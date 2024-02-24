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
And ssh test data are filled

Then the following data are present in [TEST] log:
| header     |
| long       |
| unexpected |

!-- Open web browser on google.com and take screenshot
When [https://www.google.com] url is open

Then screenshot is taken
