# Rest testing
This is a guide, showing how to set up a simple Rest test. It will send a Json request and check the response is exactly the same.
Note: _This guide depends on the user already reading the [Web testing guide](../../../README.md#simple-use-case---web-testing) in the main README.md._

### Contents
1. [Setup](#setup)
2. [Create a TestConfig file](#create-a-testconfig-filereadmemdcreate-a-testconfig-configuration-file)
3. [Write a story](#write-a-storyreadmemdwrite-your-story)
4. [Write the story class](#write-the-story-classreadmemdwrite-your-story-class)

## [Setup](../../../README.md#add-to-java-project-as-a-maven-dependency)
Add jbehave-support to your project as a Maven dependency
## Create a [TestConfig file](../../../README.md#create-a-testconfig-configuration-file)
Register the `RestServiceHandler` as a Spring bean:
```
@Bean
@Qualifier("TESTQUALIFIER")
public RestServiceHandler testRestServiceHandler() {
    return new TestRestServiceHandler("http://resttest.com");
}
```
As a parameter to the constructor, pass the URL of your rest service as a String. (In this case `http://resttest.com`.)

## Write a [story](../../../README.md#write-your-story)
Create a Rest.story file with this scenario:
```
Scenario: JSON mirroring
When [POST] request to [TESTQUALIFIER]/[MIROR/] is sent with data:
| name      | data  | contextAlias | type   |
| firstName | Mario | FIRST_NAME   | string |
| brother   | Luigi | BROTHER      | string |
| age       | 24    | AGE          | number |

Then response from [TESTQUALIFIER] REST API has status [200] and values match:
| name                 | expectedValue    | verifier |
| @header.Content-Type | application/json | CONTAINS |
| id                   |                  | NOT_NULL |
| firstName            | {CP:FIRST_NAME}  |          |
| brother              | {CP:BROTHER}     |          |
| age                  | {CP:AGE}         |          |
```

The first step will create this JSON message: `{"firstName":"Mario", "brother":"Luigi", "age":24}` and send it to the address `http://resttest.com/mirror/`.

The data for the JSON are taken from the ExamplesTable behind the colon. This is what each column means:
1. `name` - Under this name the data will be stored in the JSON
2. `data` - The value we want to send
3. `contextAlias` - under this key, the data will be stored in the [test context](../Test-context.md); This column is optional. If you don't use it, the data won't be stored.
4. `type` - the data type as which you want the data to be sent; This column is optional. If you don't use it or leave it blank, the data will be sent as string.

The second step will wait for the response to this request, check it is successful and the values are the same.

The values to check are taken from the ExamplesTable behind the colon. This is what each column means:
1. `name` - value from which data from the response to check
2. `expectedValue` -  the value we expect to be in the data with the name from `name`
      + In this instance, the expacted values are loaded from the test context using an [expression command](../Expression-commands.md). It is also possible to avoid using the test context and write the expected values as plain strings.
3. `verifier` - the [verifier](../../src/main/java/org/jbehavesupport/core/verification/VerifierNames.java) you want to use; If left blank it will be EQ (equals)

To learn more about rest steps, check out [Rest-api.md](../Rest-api.md).

## Write the [story class](../../../README.md#write-your-story-class)
This is the runnable class