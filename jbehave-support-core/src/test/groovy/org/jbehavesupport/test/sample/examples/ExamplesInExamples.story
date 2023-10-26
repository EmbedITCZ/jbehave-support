Narrative:
In order to explain how the parametrized scenarios works
As a development team
I want to show you how to work with story level examples

Lifecycle:
Examples:
| symbol |
| STK1   |
| STK2   |

Scenario: Symbol A

Given the value [<symbol>] is saved as [SYMBOL]
Given the value [<symbol_A>] is saved as [SYMBOL_A]

Given the following values are saved:
| name          | data               |
| RANDOM_STRING | {RANDOM_STRING:20} |
| CURRENT_DATE  | {CURRENT_DATE}     |
| NULL          | {NULL}             |

Given the value [{CP:CURRENT_DATE}] is saved as [CURRENT_DATE_FROM_CONTEXT]

Given a file with the [txt] extension is created and the file path is stored as [MY_TEXT_FILE]:
This is Given content of file

Then context contains [{CP:MY_TEXT_FILE}] under [MY_TEXT_FILE]
Then context contains [{CURRENT_DATE}] under [CURRENT_DATE_FROM_CONTEXT]
Then context contains [{NULL}] under [NULL]

Examples:
| symbol_A |
| STK1     |
| STK2     |

Scenario: Symbol B

Given the value [<symbol>] is saved as [SYMBOL]
Given the value [<symbol_B>] is saved as [SYMBOL_B]

Given the following values are saved:
| name          | data               |
| RANDOM_STRING | {RANDOM_STRING:20} |
| CURRENT_DATE  | {CURRENT_DATE}     |
| NULL          | {NULL}             |

Given the value [{CP:CURRENT_DATE}] is saved as [CURRENT_DATE_FROM_CONTEXT]

Given a file with the [txt] extension is created and the file path is stored as [MY_TEXT_FILE]:
This is Given content of file

Then context contains [{CP:MY_TEXT_FILE}] under [MY_TEXT_FILE]
Then context contains [{CURRENT_DATE}] under [CURRENT_DATE_FROM_CONTEXT]
Then context contains [{NULL}] under [NULL]

Examples:
| symbol_B |
| STK1     |
| STK2     |
