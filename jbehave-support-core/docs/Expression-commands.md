[Contents](../README.md)
## Expressions commands

You can find usage of the commands below in:
- [ExpressionEvaluatorTest](../src/test/groovy/org/jbehavesupport/core/expression/ExpressionEvaluatorTest.groovy)
- [REST examples](../src/test/groovy/org/jbehavesupport/test/sample/Rest.story)
- [SOAP examples](../src/test/groovy/org/jbehavesupport/test/sample/WebService.story)

List of commands:
- [BYTES](#bytes)
- [CONCAT](#concat)
- [CURRENT_DATE](#current_date)
- [DATE_PARSE](#date_parse)
- [EMPTY_STRING](#empty_string)
- [FILE](#file)
- [FORMAT_DATE](#format_date)
- [LOWER_CASE](#lower_case)
- [MAP](#map)
- [NEXT_CALENDAR_DATE](#next_calendar_date)
- [NIL](#nil)
- [NULL](#null)
- [PLUS](#plus)
- [RANDOM_DATE](#random_date)
- [RANDOM_EMAIL](#random_email)
- [RANDOM_NUMBER](#random_number)
- [RANDOM_NUMBER_IN_RANGE](#random_number_in_range)
- [RANDOM_STRING](#random_string)
- [RESOURCE](#resource)
- [SUBSTR](#substr)
- [TEST_CONTEXT_COPY](#test_context_copy)
- [UNESCAPE](#unescape)
- [UPPER_CASE](#upper_case)

#### BYTES
Provides byte array for string in SOAP or REST requests.
* param - string 
* result - reference in meaning TestContext to byte array

#### CONCAT
This command simply concatenates the arguments it is supplied with. It can concatenate two and more arguments and the arguments can be commands as well.

>Examples:  
>```
>{CONCAT:1:2:3:4:5} 
>{CONCAT:12:{CONCAT:3:4:5}}
>```  
>Both examples above produce the result `12345`

#### CURRENT_DATE
Current date command returns text form of current date. 

The CurrentDateCommand can be used in JBehave's tables in three ways:
* ```{CURRENT_DATE}``` without parameter, is evaluated to the current date
* ```{CURRENT_DATE:<number>}``` with numeric parameter, is evaluated to the current date shifted about given number of days
* ```{CURRENT_DATE:<period>}``` with period parameter, is evaluated to the current day shifted about given period of time, see {@link Period#parse}

#### DATE_PARSE
Command for parsing date.
Command consumes two arguments: 
* date in string format 
* format.

> Example:
> ``` 
> {DATE_PARSE:05/20/2031:MM/dd/yyyy} 
> ```

#### EMPTY_STRING
This command produces empty string.

> Examples:  
> ```
> {EMPTY_STRING}  
> {EMPTY}
> ```

#### FILE
Provides canonical path to file. Command consumes two parameters:

* the resource location,  e.g. org/jbehavesupport/core/expression/FileCommandTest.class
* optional name of file

> Example:
> ```
> {FILE:image.png}
> ```

#### FORMAT_DATE
Format date to expected format. Command consumes two arguments:
* date in string format
* output format

> Example:
> ```
> {FORMAT_DATE:2031-05-20:MM/dd/yyyy
> ```

#### LOWER_CASE
This command takes one parameter that gets converted to lower case.

> Example:  
> ```
> {LOWER_CASE:FOO}  
> {LC:Foo}
> ```  
> Both the examples above produce the result `foo`

#### MAP
Maps the first argument according to the mapping supplied in the second argument. It can be used when the value passed in a SOAP message is shown in the UI differently. True might be displayed as Yes. Mapping must be supplied as a comma delimited list of comma delimited tuples in brackets [ ].

> Example:
> ```
> {MAP:0:[0,Zero],[1,One],[Unknown]}
> ```
> Produces result `Zero`

#### NEXT_CALENDAR_DATE
Command consumes one parameter as day. If the day is higher or equal to day from `TimeFacade` current month is used. If the day is lower than day from `TimeFacade` we will reset day to 1 and set month to next month.

> Example:
> ```
> {NEXT_CALENDAR_DATE:2}
> ```
> If today is `10.3.2018` the result will be `12.3.2018`

#### NIL
Dedicated to send nil=true in SOAP request for JAXBElement.

> Example: 
> ``` 
> [$request] data for [$application]:  
> | name        | data  |  
> | Foo.validTo | {NIL} |
> ```

#### NULL
This command takes no parameters.

> Example:
> ```
> {NULL}
> ```

#### PLUS
This command simply do the sum of parameters.

> Example:
> ```
> {PLUS:2:2:3}
> ```
> Result of the command: `7`

#### RANDOM_DATE
Command generate random date in range 1970 - 2059.

> Example:
> ```
> {RANDOM_DATE}
> ```
> Result of the command: `LocalDate object`

#### RANDOM_EMAIL
Creates a valid random email according with fixed length.

> Example:
> ```
> {RANDOM_EMAIL}
> ```
> Result of the command: `xslk@wkle.com`

#### RANDOM_NUMBER
Generate random numeric string with specific length.  Command consumes one parameter: 
* length of random number.

> Example:
> ```
> {RANDOM_NUMBER:3}
> ```
> Result of the command: `957`

#### RANDOM_NUMBER_IN_RANGE
Generate random number in range.

> Example:
> ```
> {RANDOM_NUMBER_IN_RANGE:3:5}
> ```
> Result of the command: `9576`

#### RANDOM_STRING
Generate random string with specific length. Command consumes one parameter which specified random string length.

> Example:
> ```
> {RANDOM_STRING:2}
> ```
> Result of the command: `aD`

#### RESOURCE
Provides byte array for sending files in SOAP or REST requests. 
* param - string path to resource
* result - reference in meaning TestContext to Resource

> Example:
> ```
> {RESOURCE:image.png}
> ```
> Result of the command: `Resource object`

#### SUBSTR
Returns a string that is a substring of the first parameter.
The substring begins at the index specified by second parameter and extends to the character at index - 1 specified by the third parameter (or end of string if the optional third parameter is missing).

> Example:
> ``` 
> {SUBSTR:unhappy:2}
> ```
> Result of the command: `happy`

#### TEST_CONTEXT_COPY
Get value from test context. Short hand `CP`. Command consumes two parameters:
* key in test context
* optional - prefix which will be added to result

> Example:
> ``` 
> {CP:FIRST_NAME}
> ```
> Result of the command will be data from test context. 

#### UNESCAPE
Unescapes any java literals found in string, useful for sending special characters like whitespaces.

> Example:  
> ``` 
> {UNESCAPE:\t}
> {UNESCAPE:FirstLine\nSecondLine}
> ``` 
> Result of first command is tab. Second command will produce:
> ```
> FirstLine
> SecondLine
> ```

#### UPPER_CASE
This command takes one parameter that gets converted to upper case.

> Examples:  
> ```
> {UPPER_CASE:foo}  
> {UC:Foo}
> ```
> Both the examples above produce the result `FOO`
