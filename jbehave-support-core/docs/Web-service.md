[Contents](../README.md)

## Web Service steps

The following sample steps will prepare a ws request of type SetGetClientRequest (it can be class name or alias defined in request registration), fill it with input data, store that data in the test context under the variable names specified in the contextAlias column.

```
Given [SetGetClientRequest] data for [CIF]:
| name                               | data               | contextAlias    |
| client.clientDetails.0.trustLevel  | 5                  | TRUST_LEVEL_1   |
| client.clientDetails.0.firstName   | {RANDOM_STRING:10} | FIRST_NAME_1    |
| client.clientDetails.0.lastName    | {RANDOM_STRING:15} | LAST_NAME_1     |
| client.clientDetails.0.dateOfBirth | {RANDOM_DATE}      | DATE_OF_BIRTH_1 |
```

The next step sends the request to a ws api and verifies the result code is SUCCESS.

```
When [SetGetClientRequest] is sent to [CIF] with success
```

When it's necessary te verify a non-success response code (for example ERROR_CLIENT_MERGE) the following steps should be used.

```
When [SetGetClientRequest] is sent to [CIF]

Then [SetGetClientResponse] result from [CIF] is:
| code               |
| ERROR_CLIENT_MERGE |

```

The next step will verify the response received from the called system - CIF in this case.

```
Then [SetGetClientResponse] values from [CIF] match:
| name                               | expectedValue        |
| client.cuid                        | {NOT_NULL}           |
| version                            | {NOT_NULL}           |
| client.clientDetails.0.firstName   | {CP:FIRST_NAME_1}    |
| client.clientDetails.0.lastName    | {CP:LAST_NAME_1}     |
| client.clientDetails.0.dateOfBirth | {CP:DATE_OF_BIRTH_1} |
| client.randomNumbers.rndNo6        | {NOT_NULL}           |
| client.randomNumbers.rndNo7        | {NOT_NULL}           |
| client.randomNumbers.rndNo8        | {NOT_NULL}           |
| client.randomNumbers.rndNo9        | {NOT_NULL}           |
| client.randomNumbers.rndNo10       | {NOT_NULL}           |
```

The following step will save the specified values from the response to test context.

```
Given [SetGetClientResponse] values from [CIF] are saved:
| name        | contextAlias |
| client.cuid | CUID         |
```
Any prefix (`Given`/`When`/`Then`) can be used for this step.

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
Given [CreateAccountRequest] data for [am_account_management]:
| name                                   | data | type                                                                               |
| contract.contractParameters            |      | org.hss.integration.account.oxm.account.v2.CurrentContractParametersDto |
| contract.contractParameters.billingDay | 7    |                                                                                    |

```
