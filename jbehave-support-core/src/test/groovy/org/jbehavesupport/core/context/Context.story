Scenario: Context tests

Given the value [test data] is saved as [TEST_DATA]
Then context contains [test data] under [TEST_DATA]

Scenario: Context test scriptable parameter

Given the value [test] is saved as [TEST_DATA_FOR_CP]
Given the value [{CP:TEST_DATA_FOR_CP}] is saved as [TEST_DATA_CP]
Then context contains [test] under [TEST_DATA_CP]

Given the following values are saved:
| name         | data |
| CREDIT_CLASS | AQ   |
Then context contains [AQ] under [CREDIT_CLASS]

Scenario: Clearing

Given context is cleared
Then context is empty

Scenario: Files
Given a file with the [txt] extension is created and the file path is stored as [myTextFile]:
This is Given content of file
Then context contains [{CP:myTextFile}] under [myTextFile]
