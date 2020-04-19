[Contents](../README.md)

## Web Service steps

The following sample steps will prepare a ws request of type ClientRequest (it can be class name or alias defined in request registration), fill it with input data, store that data in the test context under the variable names specified in the contextAlias column.

```
Given [ClientRequest] data for [MYAPP]:
| name               | data               | contextAlias    |
| client.firstName   | {RANDOM_STRING:10} | FIRST_NAME_1    |
| client.lastName    | {RANDOM_STRING:15} | LAST_NAME_1     |
| client.dateOfBirth | {RANDOM_DATE}      | DATE_OF_BIRTH_1 |
```

The next step sends the request to a ws api and verifies the result code is SUCCESS.

```
When [ClientRequest] is sent to [MYAPP] with success
```

When it's necessary to verify a non-success response code (for example ERROR_CLIENT_NOT_FOUND) the following steps should be used.

```
When [ClientRequest] is sent to [MYAPP]

Then [ClientRequest] result from [MYAPP] is:
| code                   |
| ERROR_CLIENT_NOT_FOUND |
```

The next step will verify the response received from the called system - MYAPP in this case.

```
Then [ClientRequest] values from [MYAPP] match:
| name           | expectedValue | verifier |
| client.id      | {NULL}        | NE       |
| client.version | {NULL}        | NE       |
```

The following step will save the specified values from the response to test context.

```
Given [ClientRequest] values from [MYAPP] are saved:
| name      | contextAlias |
| client.id | ID           |
```
Any prefix (`Given`/`When`/`Then`) can be used for this step.

To verify standard SOAP Fault response the step below can be used:

```
When [ClientRequest] is sent to [MYAPP] with fault:
| name        | expectedValue |
| faultCode   | Server        |
| faultReason | someReason    |
```
Note the special `faultCode` and `faultReason` parameters that can be checked (either one of them or both can be checked).

### Sending NIL values
A special `NIL` command can be used for sending nil values:
> ``` 
> [$request] data for [$application]:  
> | name        | data  |  
> | Foo.validTo | {NIL} |
> ```
The above will result in something like `<Foo xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"><validTo xsi:nil="true"/></Foo>`.

### Sending Empty values
An empty node (`<node/>`) can be sent by se setting the required node using the `NULL` command:
> ``` 
> [$request] data for [$application]:  
> | name        | data   |  
> | Foo.validTo | {NULL} |
> ```
The above will result in something like `<Foo><validTo/></Foo>`.

When using the `NULL` command the node gets sent with empty value, when not specifying any value at all (not specifying the key in the table) the node does not get sent at all.


---
### Custom type converters
Most of plain Java data types are supported for conversion from String to type. In some use cases you may need to convert type, which is not supported by default - typically joda date formats. We stick to this approach to sustain stability and maintainability of jbehave, avoiding countless dependencies on every possible package somebody might need to use some day. If you don't provide any conversion service bean, DefaultConversionService will be used.
To handle your own conversion you need to:
1. Create bean in your Spring context implementing `org.springframework.core.convert.ConversionService`. We strongly recommend you to do so using `org.springframework.core.convert.support.DefaultConversionService`, however other implementations are available to you as well.
2. In your `ConversionService` register own `Converter` via `addConverter()` method
3. Your converter must implement `convert` method, which usually takes String parameter and provides desired object

Code example of tutorial above can look like this:
```
@Bean
public ConversionService conversionService(){
    ConversionService conversionService = new DefaultConversionService();
    ((DefaultConversionService)conversionService).addConverter(new Converter<String, DateTime>() {
        @Override
        public DateTime convert(String source) {
            return new DateTime(source);
        }
    });
    //more addConverter methods can be called here
    return conversionService;
}
```

### Providing implementation for abstract class
In some cases, request class can have abstract attribute with several implementations. In this case you need to specify which implementation you want to use per request.
This can be specified directly in example table. Provide fully qualified class name in separate type column. Data column for particular line will be ignored if type is present.
```
Given [CreateAccountRequest] data for [MYAPP]:
| name                                   | data | type                                       |
| contract.contractParameters            |      | org.myapp.oxm.CurrentContractParametersDto |
| contract.contractParameters.billingDay | 7    |                                            |
```
