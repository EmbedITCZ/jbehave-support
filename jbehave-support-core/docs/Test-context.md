[Contents](../README.md)

## Test context

Test context is key-value map used to store values during a test execution so that they can be accessed later. 
There are many steps that can be used to store data into the test context. 
Typically they have a contextAlias column that should contain a name of the variable that will hold the value in the test context such as in the example below.

```
Given SetGetClientRequest data for CIF:
| name                               | data               | contextAlias  |
| client.clientDetails.0.trustLevel  | 5                  | TRUST_LEVEL   |
| client.clientDetails.0.firstName   | {RANDOM_STRING:10} | FIRST_NAME    |
| client.clientDetails.0.lastName    | {RANDOM_STRING:15} | LAST_NAME     |
| client.clientDetails.0.dateOfBirth | {RANDOM_DATE}      | DATE_OF_BIRTH |
```

There is also a step used just for storing values in the test context. 
After this step is executed the test context will contain two key-value pairs - CONSTANTS.CREDIT_CLASS, CONSTANTS.FIRST_NAME.
The prefix CONSTANTS can be replaced by any arbitrary string.

```
Given the following test constants with prefix CONSTANTS and values:
| name                  | data                                   |
| CREDIT_CLASS          | AQ                                     |
| FIRST_NAME            | {RANDOM_STRING:10}                     |
```

There's also a variant of the above step without a prefix.  
After this step is executed the test context will contain two key-value pairs - CREDIT_CLASS, FIRST_NAME.

```
Given the following values are saved:
| name                  | data                                   |
| CREDIT_CLASS          | AQ                                     |
| FIRST_NAME            | {RANDOM_STRING:10}                     |
```


To retrieve values from the test context the TestContextCopy evaluation command (shorthand CP) should be used as in the step below. 

```
Given [CreateCreditAccountApplicationRequest] data for [LS]:
| name                                          | data                                 |
| creditClass                                   | {CP:CONSTANTS.CREDIT_CLASS}          |
| firstName                                     | {CP:CONSTANTS.FIRST_NAME}            |
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
