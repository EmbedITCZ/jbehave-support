[Contents](../README.md)

## Test context

Test context is key-value map used to store values during a test execution so that they can be accessed later.
There are many steps that can be used to store data into the test context.
Typically they have a contextAlias column that should contain a name of the variable that will hold the value in the test context such as in the example below.

```
Given [ClientRequest] data for [MYAPP]:
| name               | data               | contextAlias  |
| client.firstName   | {RANDOM_STRING:10} | FIRST_NAME    |
| client.lastName    | {RANDOM_STRING:15} | LAST_NAME     |
| client.dateOfBirth | {RANDOM_DATE}      | DATE_OF_BIRTH |
```

There is also a step used just for storing values in the test context.
After this step is executed the test context will contain two key-value pairs - FIRST_NAME, LAST_NAME.
Data stored by this step will always have `userDefined()` metadata.

```
Given the following values are saved:
| name                  | data               |
| FIRST_NAME            | {RANDOM_STRING:10} |
| LAST_NAME             | {RANDOM_STRING:15} |
```


To retrieve values from the test context the TestContextCopy evaluation command (shorthand CP) should be used as in the step below.

```
Given [ClientRequest] data for [MYAPP]:
| name          | data                         |
| firstName     | {CP:CONSTANTS.FIRST_NAME}    |
| lastName      | {CP:CONSTANTS.LAST_NAME}     |
```

It's also possible to store data from a yaml file into the context.
Consider the following file with the name "mock-data.yml" is available on classpath.

```
UC1:
  ban: 111111111
  assessment:
    colorCode: green
    welcomeText: Hi Karel
  customerInfo:
    firstName: Karel
    lastName: Klobaska
    middleName: Stavnata

UC2:
  ban: 000000000
```

Then by using the following step the data from the file are saved in the test context and become available to the scenarios.

```
Given data from resource [mock-data.yml] is saved in context
```

In the scenario the data will be available by calling the expression {CP:UC1.ban} etc. At the moment only yaml files (yml extension) are supported.

Test context's scope is limited to a scenario.
Key-value pairs entered in other scenarios will not be accessible.

---
