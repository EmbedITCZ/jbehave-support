Scenario: I save value into test context

Given the value [JBehave] is saved as [TEST-FRAMEWORK]
Then context contains [JBehave] under [TEST-FRAMEWORK]

Scenario: It is not present in next scenario

Then context is empty
