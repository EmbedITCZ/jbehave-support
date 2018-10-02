Scenario: escaping in test context

Given the value [colon : and \{text in curlies\}] is saved as [TEST_DATA_LC]
Given the value [{UC:{CP:TEST_DATA_LC}}] is saved as [TEST_DATA_UC]
Then context contains [colon : and \{text in curlies\}] under [TEST_DATA_LC]
Then context contains [{UC:{CP:TEST_DATA_LC}}] under [TEST_DATA_UC]
Then context contains [COLON : AND \{TEXT IN CURLIES\}] under [TEST_DATA_UC]
