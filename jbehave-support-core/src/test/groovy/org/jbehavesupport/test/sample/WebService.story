A story is collection of scenarious for testing web services

Narrative:
In order to test ws call implemetation in jbehave-support-core
As a development team
I want to confirm correct functionality

Scenario: Ws scenario to confirm send request to WS and validate response
Given [NameRequest] data for [TEST]:
| name       | data              |
| name       | test              |
| CUID       | {RANDOM_STRING:7} |
| address.0  | {RANDOM_EMAIL}    |
| passDate   | {RANDOM_DATE}     |
| maxResults | {RANDOM_NUMBER:9} |
When [NameRequest] is sent to [TEST] with success
Then [NameResponse] values from [TEST] match:
| name      | expectedValue | verifier |
| firstName | John          | EQ       |
| lastName  | Doe           | EQ       |
| age       | 33            | EQ       |
| relatives | 2             | SIZE_EQ  |

Scenario: Ws scenario to confirm send request to WS with expected error message
Given [NameRequest] data for [TEST]:
| name | data  |
| name | error |
When [NameRequest] is sent to [TEST]
Then [NameResponse] result from [TEST] is:
| code   |
| ERR111 |

Scenario: Ws scenario to confirm send request to WS and validate response with all operators
Given [NameRequest] data for [TEST]:
| name | data |
| name | test |
When [NameRequest] is sent to [TEST] with success
Then [NameResponse] values from [TEST] match:
| name      | expectedValue | verifier     |
| firstName | John          | EQ           |
| firstName | ^Jo.n$        | REGEX_MATCH  |
| firstName | oh            | CONTAINS     |
| firstName | al            | NOT_CONTAINS |
| lastName  | Doe           | EQ           |
| lastName  | Doe1          | NE           |
| age       | 33            | EQ           |
| age       | 44            | LT           |
| age       | 4             | GT           |
And [NameRequest] values from [TEST] are saved:
| name | contextAlias |
| name | requestName  |
And [NameResponse] values from [TEST] are saved:
| name      | contextAlias |
| firstName | responseName |

Then context contains [test] under [requestName]
Then context contains [John] under [responseName]

Scenario: Ws scenario to verify null values
Given [NameRequest] data for [TEST]:
| name | data   |
| name | error  |
| CUID | {NULL} |
When [NameRequest] is sent to [TEST]
Then [NameResponse] values from [TEST] match:
| name      | expectedValue | verifier |
| firstName | {NULL}        | EQ       |

Scenario: Ws scenario to confirm request has valid implementation of abstract class
Given [NameRequest] data for [TEST]:
| name           | data        | type                                              |
| name           | test        |                                                   |
| contact        |             | org.jbehavesupport.core.test.app.oxm.PhoneContact |
| contact.number | 399 811 477 |                                                   |
When [NameRequest] is sent to [TEST] with success

Scenario: nested list
Given [NameRequest] data for [TEST]:
| name                           | data  |
| name                           | test  |
| addressList.addressInfo.0.city | Praha |
| addressList.addressInfo.0.zip  | 11000 |
| addressList.addressInfo.1.city | Brno  |
| addressList.addressInfo.1.zip  | 60200 |
When [NameRequest] is sent to [TEST] with success

Scenario: jaxbElement
Given [NameRequest] data for [TEST]:
| name     | data     |
| name     | simpleJB |
| passDate | {NIL}    |
When [NameRequest] is sent to [TEST] with success
Then [NameResponse] values from [TEST] match:
| name      | expectedValue |
| firstName | resurrected   |

Given [NameRequest] data for [TEST]:
| name     | data           |
| name     | simpleJB       |
| passDate | {CURRENT_DATE} |
When [NameRequest] is sent to [TEST] with success
Then [NameResponse] values from [TEST] match:
| name      | expectedValue |
| firstName | dead          |

Given [NameRequest] data for [TEST]:
| name                                  | data           |
| name                                  | inlayJB        |
| addressList.addressInfo.0.city        | Praha          |
| addressList.addressInfo.0.zip         | 11000          |
| addressList.addressInfo.0.livingSince | {CURRENT_DATE} |
| addressList.addressInfo.1.city        | Brno           |
| addressList.addressInfo.1.zip         | 60200          |
| addressList.addressInfo.1.livingSince | {NIL}          |
When [NameRequest] is sent to [TEST] with success
Then [NameResponse] values from [TEST] match:
| name       | expectedValue |
| error.code | OK            |

Scenario: complex jaxbElement
Given [NameRequest] data for [TEST]:
| name                 | data          |
| name                 | complexJB     |
| phoneContact.number  | 44            |
| phoneContact.validTo | {RANDOM_DATE} |
When [NameRequest] is sent to [TEST] with success
Then [NameResponse] values from [TEST] match:
| name         | expectedValue |
| testResponse | 44            |

Scenario: jaxb inheritance
Given [NameRequest] data for [TEST]:
| name         | data           |
| name         | test           |
| cell.number  | 132            |
| cell.validTo | {CURRENT_DATE} |
When [NameRequest] is sent to [TEST] with success

Scenario: base 64
Given [NameRequest] data for [TEST]:
| name            | data                 |
| name            | file                 |
| photoInfo.photo | {RESOURCE:image.png} |
When [NameRequest] is sent to [TEST] with success
Then [NameResponse] values from [TEST] match:
| name         | expectedValue |
| testResponse | Base 64 valid |

Given [NameRequest] data for [TEST]:
| name            | data                 |
| name            | file                 |
| photoInfo.photo | {RESOURCE:image.png} |
| photoInfo.path  | {FILE:image.png}     |
When [NameRequest] is sent to [TEST] with success
Then [NameResponse] values from [TEST] match:
| name         | expectedValue  |
| testResponse | file validated |

Given [NameRequest] data for [TEST]:
| name            | data           |
| name            | file           |
| photoInfo.photo | MY LITTLE FILE |
When [NameRequest] is sent to [TEST] with success
Then [NameResponse] values from [TEST] match:
| name         | expectedValue |
| testResponse | Base 64 valid |
