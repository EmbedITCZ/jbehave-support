Scenario: Context test scriptable parameter with typo

Meta:
@result should fail

Given the value [test] is saved as [TEST_DATA_FOR_CP]
!-- typo in test context key "TTEST..."
Given the value [{CP:TTEST_DATA_FOR_CP}] is saved as [TEST_DATA_CP]
Then context contains [test] under [TEST_DATA_CP]
