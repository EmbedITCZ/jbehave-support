Narrative:
In order to explain how escaping in context works
As a development team
I want to show you escaping in context

Scenario: Escaping in test context

Given the value [colon : and \{text in curlies\} with \'] is saved as [TEST_DATA_LC]
Given the value [{UC:{CP:TEST_DATA_LC}}] is saved as [TEST_DATA_UC]
Then context contains [colon : and \{text in curlies\} with \'] under [TEST_DATA_LC]
Then context contains [{UC:{CP:TEST_DATA_LC}}] under [TEST_DATA_UC]
Then context contains [COLON : AND \{TEXT IN CURLIES\} WITH \'] under [TEST_DATA_UC]

Given the value ['11:22:33'] is saved as [TEST_DATA]
Then context contains ['11:22:33'] under [TEST_DATA]
