Narrative:
In order to explain how to load data from file
As a development team
I want to show you how to load data from yml file

Scenario: Load data from file

Given data from resource [mock-data.yml] is saved in context

Then context contains [value1] under [key1]
Then context contains [value2] under [key2]
Then context contains [value3] under [prefix1.key1]
