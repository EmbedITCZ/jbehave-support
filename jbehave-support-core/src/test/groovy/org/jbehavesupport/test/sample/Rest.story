Scenario: parameterless GET

When [PATCH] request to [TEST]/[init/] is sent
Then response from [TEST] REST API has status [OK]

When [GET] request to [TEST]/[user/] is sent
Then response from [TEST] REST API has status [OK] and values match:
| name          | expectedValue |
| [0].id        | 5             |
| [0].firstName | Emanuel       |
| [0].lastName  | Rego          |
| [1].id        | 9             |
| [1].firstName | Alison        |
| [1].lastName  | Cerutti       |

Given the value [name] is saved as [ORDER]
When [GET] request to [TEST]/[user/?order={CP:ORDER}] is sent
Then response from [TEST] REST API has status [OK] and values match:
| name          | expectedValue |
| [0].id        | 9             |
| [0].firstName | Alison        |
| [0].lastName  | Cerutti       |


Scenario: request body with headers only

When [GET] request to [TEST]/[user/] is sent with data:
| name                 | data                           | contextAlias |
| @header.Content-Type | application/json;charset=utf-8 |              |
Then response from [TEST] REST API has status [OK]


Scenario: GET with id

When [GET] request to [TEST]/[user/9] is sent
Then response from [TEST] REST API has status [200] and values match:
| name      | expectedValue | verifier |
| id        | 8             | GT       |
| firstName | Alison        |          |
| lastName  | Cerutti       |          |


Scenario: POST

!-- add a story that calls a POST method on user controller
When [POST] request to [TEST]/[user/] is sent with data:
| name                 | data                           | contextAlias |
| @header.Content-Type | application/json;charset=utf-8 |              |
| firstName            | Bruno                          |              |
| lastName             | {RANDOM_STRING:10}             | LAST_NAME    |
Then response from [TEST] REST API has status [200] and values match:
| name                 | expectedValue    | verifier |
| @header.Content-Type | application/json | CONTAINS |
| id                   |                  | NOT_NULL |
| firstName            | Bruno            |          |
| lastName             | {CP:LAST_NAME}   |          |
When response values from [TEST] REST API are saved:
| name                      | contextAlias      |
| @header.Transfer-Encoding | TRANSFER_ENCODING |
Then context contains [chunked] under [TRANSFER_ENCODING]


Scenario: POST, GET, PUT

When [POST] request to [TEST]/[user/] is sent with data:
| name      | data    |
| firstName | Pedro   |
| lastName  | Salgado |
Then response from [TEST] REST API has status [200]
When response values from [TEST] REST API are saved:
| name | contextAlias |
| id   | NEW_ID       |
When [PUT] request to [TEST]/[user/] is sent with data:
| name      | data        |
| id        | {CP:NEW_ID} |
| firstName | Nick        |
| lastName  | Lucena      |
Then response from [TEST] REST API has status [200]
When [GET] request to [TEST]/[user/{CP:NEW_ID}] is sent
Then response from [TEST] REST API has status [200] and values match:
| name      | expectedValue |
| id        | {CP:NEW_ID}   |
| firstName | Nick          |
| lastName  | Lucena        |


Scenario: json with collections
When [POST] request to [TEST]/[user/] is sent with data:
| name                 | data           | contextAlias      |
| firstName            | Pedro          | FIRST_NAME        |
| lastName             | Salgado        | LAST_NAME         |
| addresses[0].country | Brazil         | ADDRESS_0_COUNTRY |
| addresses[0].city    | Rio de Janeiro | ADDRESS_0_CITY    |
| addresses[1].country | Austria        | ADDRESS_1_COUNTRY |
| addresses[1].city    | Graz           | ADDRESS_1_CITY    |
Then response from [TEST] REST API has status [200] and values match:
| name                 | expectedValue          | verifier |
| @header.Content-Type | application/json       | CONTAINS |
| id                   |                        | NOT_NULL |
| firstName            | {CP:FIRST_NAME}        |          |
| lastName             | {CP:LAST_NAME}         |          |
| addresses[0].country | {CP:ADDRESS_0_COUNTRY} |          |
| addresses[0].city    | {CP:ADDRESS_0_CITY}    |          |
| addresses[1].country | {CP:ADDRESS_1_COUNTRY} |          |
| addresses[1].city    | {CP:ADDRESS_1_CITY}    |          |


Scenario: double-digit indexed collections
When [POST] request to [TEST]/[user/] is sent with data:
| name                    | data             | contextAlias        |
| firstName               | Pedro            | FIRST_NAME          |
| lastName                | Salgado          | LAST_NAME           |
| addresses[0].country    | Brazil 0         | ADDRESS_0_COUNTRY   |
| addresses[0].city       | Rio de Janeiro 0 | ADDRESS_0_CITY      |
| addresses[0].details[0] | details 0 0      | ADDRESS_0_DETAILS_0 |
| addresses[0].details[1] | details 0 1      | ADDRESS_0_DETAILS_1 |
| addresses[1].country    | Austria 1        | ADDRESS_1_COUNTRY   |
| addresses[1].city       | Graz 1           | ADDRESS_1_CITY      |
| addresses[2].country    | Austria 2        | ADDRESS_2_COUNTRY   |
| addresses[2].city       | Graz 2           | ADDRESS_2_CITY      |
| addresses[3].country    | Austria 3        | ADDRESS_3_COUNTRY   |
| addresses[3].city       | Graz 3           | ADDRESS_3_CITY      |
| addresses[4].country    | Austria 4        | ADDRESS_4_COUNTRY   |
| addresses[4].city       | Graz 4           | ADDRESS_4_CITY      |
| addresses[5].country    | Austria 5        | ADDRESS_5_COUNTRY   |
| addresses[5].city       | Graz 5           | ADDRESS_5_CITY      |
| addresses[6].country    | Austria 6        | ADDRESS_6_COUNTRY   |
| addresses[6].city       | Graz 6           | ADDRESS_6_CITY      |
| addresses[7].country    | Austria 7        | ADDRESS_7_COUNTRY   |
| addresses[7].city       | Graz 7           | ADDRESS_7_CITY      |
| addresses[8].country    | Austria 8        | ADDRESS_8_COUNTRY   |
| addresses[8].city       | Graz 8           | ADDRESS_8_CITY      |
| addresses[9].country    | Austria 9        | ADDRESS_9_COUNTRY   |
| addresses[9].city       | Graz 9           | ADDRESS_9_CITY      |
| addresses[10].country   | Austria 10       | ADDRESS_10_COUNTRY  |
| addresses[10].city      | Graz 10          | ADDRESS_10_CITY     |

Then response from [TEST] REST API has status [200] and values match:
| name                    | expectedValue            | verifier |
| @header.Content-Type    | application/json         | CONTAINS |
| id                      |                          | NOT_NULL |
| firstName               | {CP:FIRST_NAME}          |          |
| lastName                | {CP:LAST_NAME}           |          |
| addresses[0].country    | {CP:ADDRESS_0_COUNTRY}   |          |
| addresses[0].city       | {CP:ADDRESS_0_CITY}      |          |
| addresses[0].details[0] | {CP:ADDRESS_0_DETAILS_0} |          |
| addresses[0].details[1] | {CP:ADDRESS_0_DETAILS_1} |          |
| addresses[1].country    | {CP:ADDRESS_1_COUNTRY}   |          |
| addresses[1].city       | {CP:ADDRESS_1_CITY}      |          |
| addresses[2].country    | {CP:ADDRESS_2_COUNTRY}   |          |
| addresses[2].city       | {CP:ADDRESS_2_CITY}      |          |
| addresses[3].country    | {CP:ADDRESS_3_COUNTRY}   |          |
| addresses[3].city       | {CP:ADDRESS_3_CITY}      |          |
| addresses[4].country    | {CP:ADDRESS_4_COUNTRY}   |          |
| addresses[4].city       | {CP:ADDRESS_4_CITY}      |          |
| addresses[5].country    | {CP:ADDRESS_5_COUNTRY}   |          |
| addresses[5].city       | {CP:ADDRESS_5_CITY}      |          |
| addresses[6].country    | {CP:ADDRESS_6_COUNTRY}   |          |
| addresses[6].city       | {CP:ADDRESS_6_CITY}      |          |
| addresses[7].country    | {CP:ADDRESS_7_COUNTRY}   |          |
| addresses[7].city       | {CP:ADDRESS_7_CITY}      |          |
| addresses[8].country    | {CP:ADDRESS_8_COUNTRY}   |          |
| addresses[8].city       | {CP:ADDRESS_8_CITY}      |          |
| addresses[9].country    | {CP:ADDRESS_9_COUNTRY}   |          |
| addresses[9].city       | {CP:ADDRESS_9_CITY}      |          |
| addresses[10].country   | {CP:ADDRESS_10_COUNTRY}  |          |
| addresses[10].city      | {CP:ADDRESS_10_CITY}     |          |


Scenario: GET on secured URL with basic auth setup

When [GET] request to [TEST-SECURE]/[secure/user/9] is sent
Then response from [TEST-SECURE] REST API has status [200] and values match:
| name      | expectedValue | verifier |
| id        | 8             | GT       |
| firstName | Alison        |          |
| lastName  | Cerutti       |          |


Scenario: send body with array at root level - one element
When [POST] request to [TEST]/[user/batch] is sent with data:
| name                     | data         |
| [0].id                   | 225          |
| [0].firstName            | first name 1 |
| [0].lastName             | last name 1  |
| [0].addresses[0].city    | city 0 0     |
| [0].addresses[0].country | country 0 0  |
| [0].addresses[1].city    | city 0 1     |
| [0].addresses[1].country | country 0 1  |
Then response from [TEST] REST API has status [OK]

Given [GET] request to [TEST]/[user/225] is sent
Then response from [TEST] REST API has status [OK] and values match:
| name                 | expectedValue |
| id                   | 225           |
| firstName            | first name 1  |
| lastName             | last name 1   |
| addresses[0].city    | city 0 0      |
| addresses[0].country | country 0 0   |
| addresses[1].city    | city 0 1      |
| addresses[1].country | country 0 1   |


Scenario: send body with array at root level - multiple elements

When [POST] request to [TEST]/[user/batch] is sent with data:
| name                     | data         |
| [0].id                   | 22           |
| [0].firstName            | first name 1 |
| [0].lastName             | last name 1  |
| [0].addresses[0].city    | city 0 0     |
| [0].addresses[0].country | country 0 0  |
| [0].addresses[1].city    | city 0 1     |
| [0].addresses[1].country | country 0 1  |
| [1].id                   | 222          |
| [1].firstName            | first name 2 |
| [1].lastName             | last name 2  |
| [1].addresses[0].city    | city 1 0     |
| [1].addresses[0].country | country 1 0  |
| [1].addresses[1].city    | city 1 1     |
| [1].addresses[1].country | country 1 1  |
Then response from [TEST] REST API has status [OK]

When [GET] request to [TEST]/[user/22] is sent
Then response from [TEST] REST API has status [OK] and values match:
| name                 | expectedValue |
| id                   | 22            |
| firstName            | first name 1  |
| lastName             | last name 1   |
| addresses[0].city    | city 0 0      |
| addresses[0].country | country 0 0   |
| addresses[1].city    | city 0 1      |
| addresses[1].country | country 0 1   |

When [GET] request to [TEST]/[user/222] is sent
Then response from [TEST] REST API has status [OK] and values match:
| name                 | expectedValue |
| id                   | 222           |
| firstName            | first name 2  |
| lastName             | last name 2   |
| addresses[0].city    | city 1 0      |
| addresses[0].country | country 1 0   |
| addresses[1].city    | city 1 1      |
| addresses[1].country | country 1 1   |

When [POST] request to [TEST]/[body/] is sent with data:
| name  | data             |
| @body | this is raw body |
Then response from [TEST] REST API has status [OK]

When [POST] request to [TEST]/[body/] is sent with data:
| name          | data         |
| [0].id        | 22           |
| [0].firstName | first name 1 |
Then response from [TEST] REST API has status [BAD_REQUEST]

Scenario: file upload
When [POST] request to [TEST]/[base64/multipart/] is sent with data:
| name                 | data                 |
| @header.Content-Type | multipart/form-data  |
| file1                | {RESOURCE:image.png} |
| file2                | {RESOURCE:image.png} |
Then response from [TEST] REST API has status [OK] and values match:
| name  | expectedValue |
| file1 | file1         |
| file2 | file2         |

When [POST] request to [TEST]/[base64/multipart/] is sent with data:
| name                 | data                 |
| @header.Content-Type | multipart/form-data  |
| data                 | {RESOURCE:image.png} |
| firstName            | someName             |
| lastName             | lastName             |
Then response from [TEST] REST API has status [OK]

Scenario: test success handlers
When [POST] request to [TEST]/[mirror/] is sent with data:
| name             | data        |
| httpStatus       | CREATED     |
| payload[0]       | happy       |
Then response from [TEST] REST API is successful

When [POST] request to [TEST]/[mirror/] is sent with data:
| name             | data        |
| httpStatus       | CREATED     |
| payload[0]       | happy       |
| payload[1]       | astronaut   |
Then response from [TEST] REST API is successful and values match:
| name       | expectedValue |
| payload[1] | astronaut     |


