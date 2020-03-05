Narrative:
In order to explain how the test contex works
As a development team
I want to show you how to work with test context

Scenario: Test contex story

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


