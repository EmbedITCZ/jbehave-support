Narrative:
In order to explain how the parametrized scenarios works
As a development team
I want to show you how to work with scenario level examples - examples in external file

Scenario: Examples story

Given the value [<value_A>] is saved as [ASD]

Given the following values are saved:
| name          | data               |
| RANDOM_STRING | {RANDOM_STRING:20} |
| CURRENT_DATE  | {CURRENT_DATE}     |
| NULL          | {NULL}             |
| VALUE         | {CP:ASD}           |

Given the value [{CP:CURRENT_DATE}-<VALUE_B>] is saved as [CURRENT_DATE_FROM_CONTEXT]

Given a file with the [txt] extension is created and the file path is stored as [MY_TEXT_FILE]:
This is Given content of file

Then context contains [{CP:MY_TEXT_FILE}] under [MY_TEXT_FILE]
Then context contains [{CURRENT_DATE}-<VALUE_B>] under [CURRENT_DATE_FROM_CONTEXT]
Then context contains [{NULL}] under [NULL]
Then context contains [{NULL}] under [NULL]
Then context contains [<value_A>] under [VALUE]

Examples:
examples/examples.table