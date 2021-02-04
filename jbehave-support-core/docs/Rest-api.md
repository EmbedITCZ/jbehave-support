[Contents](../README.md)

## REST API steps

### Configuration

A bean of type RestServiceHandler has to be registered in the Spring context.

```
@Bean
@Qualifier("TEST")
public RestServiceHandler testRestServiceHandler() {
    return new TestRestServiceHandler();
}
```

The RestServiceHandler implementation is responsible for registering the URL of the REST service.

### Available steps

There are two steps available for sending requests to REST APIs.
The steps work directly with JSON so there's no need to register request or response objects for REST calls.

The following step sends a request without a JSON body.
```
When [GET] request to [TEST]/[user/] is sent
```

The following step will send a POST request to the TEST system on URL user/ with the following data in the request as JSON.
The contextAlias column is optional and can be used to store the value from the 'data' column into the test context.
```
When [POST] request to [TEST]/[user/] is sent with data:
| name      | data               | contextAlias |
| firstName | Bruno              |              |
| lastName  | {RANDOM_STRING:10} | LAST_NAME    |
```

REST steps provide support for lists as well.
The following step is an example how to send a list of addresses.

```
When [POST] request to [TEST]/[user/] is sent with data:
| name                    | data             | contextAlias        |
| firstName               | Pedro            | FIRST_NAME          |
| lastName                | Salgado          | LAST_NAME           |
| addresses[0].country    | Brazil 0         | ADDRESS_0_COUNTRY   |
| addresses[0].city       | Rio de Janeiro 0 | ADDRESS_0_CITY      |
| addresses[0].details[0] | details 0 0      | ADDRESS_0_DETAILS_0 |
| addresses[0].details[1] | details 0 1      | ADDRESS_0_DETAILS_1 |
| addresses[1].country    | Austria 1        | ADDRESS_1_COUNTRY   |
| addresses[1].city       | Graz 1           | ADDRESS_1_CITY      |
| addresses[2].country    | Austria 2        | ADDRESS_2_COUNTRY   |
| addresses[2].city       | Graz 2           | ADDRESS_2_CITY      |
```

It's possible to send an empty list by using empty brackets.
In the following example, an empty list of documents will be sent:
```
When [POST] request to [TEST]/[user/] is sent with data:
| name        | data   | contextAlias |
| firstName   | Ricky  | FIRST_NAME   |
| lastName    | Gruber | LAST_NAME    |
| documents[] |        |              |
```

To send a list at the root level use the following syntax:

```
When [POST] request to [TEST]/[user/batch] is sent with data:
| name          | data         |
| [0].id        | 22           |
| [0].firstName | first name 1 |
| [0].lastName  | last name 1  |
| [1].id        | 222          |
| [1].firstName | first name 2 |
| [1].lastName  | last name 2  |
```

The following step can be used to save data from the last REST API response in the test context.
```
When response values from [TEST] REST API are saved:
| name | contextAlias |
| id   | NEW_ID       |
```

There are two methods for verification of the response.

The following step checks the response code of the last REST API call.
The response code can be either a number or a String.
```
Then response from [TEST] REST API has status [200]
```

The following step checks the response code and the response body JSON.
```
Then response from [TEST] REST API has status [OK] and values match:
| name          | expectedValue |
| [0].id        | 5             |
| [0].firstName | Emanuel       |
| [0].lastName  | Rego          |
| [1].id        | 9             |
| [1].firstName | Alison        |
| [1].lastName  | Cerutti       |
```

Each of steps above can be used as one of successful variants, where you define default success status and result (data).
`Then response from [TEST] REST API is successful` or `Then response from [TEST] REST API is successful and values match`.
Successful response is defined in form of ExamplesTable in `RestServiceHandler#getSuccessResult()`

#### Handling headers
By default `application/json` will be send. If you need multipart request, i.e.: for sending file you have to specify appropriate header: `multipart/form-data`

The following step will send a POST request with a specific header.
```
When [POST] request to [TEST]/[user/] is sent with data:
| name                 | data                           |
| @header.Content-Type | application/json;charset=utf-8 |
| firstName            | Bruno                          |
| lastName             | Schmidt                        |
```

The response headers can be verified using the following step.
```
Then response from [TEST] REST API has status [200] and values match:
| name                 | expectedValue    | verifier |
| @header.Content-Type | application/json | CONTAINS |
| id                   |                  | NOT_NULL |
| firstName            | Bruno            |          |
| lastName             | Schmidt          |          |
```

Similar approach is used for saving response headers to the text context.
```
When response values from [TEST] REST API are saved:
| name                      | contextAlias      |
| @header.Transfer-Encoding | TRANSFER_ENCODING |
 ```
---

#### Handling url parameters

Any command can be used while expressing url to be called. Nesting is supported as well.
```
Given the value [name] is saved as [ORDER]
When [GET] request to [TEST]/[user/?order={CP:ORDER}] is sent
```
Example above produces GET request to URL .../user/?order=name

#### Sending raw body in request
In some cases you might need to send unstructured data to WS. This is done via `@body` key. If `@body` is present, no other keys must be defined, except headers.
```
When [POST] request to [TEST]/[user/] is sent with data:
| name                 | data                           |
| @header.Content-Type | application/json;charset=utf-8 |
| @body                | Anything you want to send.     |
```
(Saving and verifying raw body from response is done in the same way with the `@body` as well, except in these cases other keys can be present as well.)

#### Handling JSON data types
All JSON data will be sent as a string, unless specified otherwise. To specify a data type, use optional `type` column in your data table. These data types are supported:
 + `string`
 + `boolean`
 + `number`

If you leave the column blank, the data will be sent as a string. The `{NULL}` command will send `null` no matter what is written in the type column.

Ex.:
```
| name        | data     | type    |
| firstName   | Mario    | string  |
| age         | 24       | number  |
| height      | 1.56     | number  |
| plumber     | true     | boolean |
| brother     | Luigi    |         |
| princess    | {NULL}   |         |
| powerups[0] | fireball | string  |
| powerups[1] | {NULL}   | string  |
```
This table will generate this JSON:
```
{"firstName":"Mario","plumber":true,"princess":null,"brother":"Luigi","age":24,"height":1.56,"powerups":["fireball",null]}
```

#### Enable JSON message logging from REST steps
To enable logging of JSON messages add `RestLoggingInterceptor` into logback
```
<logger name="org.jbehavesupport.core.rest.RestLoggingInterceptor" level="trace" additivity="false">
        <appender-ref ref="CONSOLE"/>
</logger>
```

#### Verifying using JSONPath
We offer limited support for JSONPath verification. To use it simply start the value in the `name` column with `$`, e.g.:
```
Then response from [TEST] REST API has status [200] and values match:
| name                                   | expectedValue    | verifier |
| $[?(@.firstName=='Bruno')].id          |                  | NOT_NULL |
```
