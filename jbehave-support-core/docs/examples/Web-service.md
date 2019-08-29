# Web service testing
This is a guide, showing how to set up a simple Web service test. It will send a SOAP request, check if it was sent with success and the response is exactly the same.

Note: _This guide depends on the user already reading the [Web testing guide](../../../README.md#simple-use-case---web-testing) in the main README.md._

### Contents

## [Setup](../../../README.md#add-to-java-project-as-a-maven-dependency)
Add jbehave-support to your project as a Maven dependency

## Create your Handler class
Create a class called `MyWebServiceHandler` which extends WebServiceHandler
```
public class MyWebServiceHandler extends WebServiceHandler {
```

To set the url of the app you want to send your requests to, you have to add this method:
 ```
    @Override
    protected void initTemplate(WebServiceTemplateConfigurer templateConfigurer) {
        templateConfigurer
            .defaultUri("http://wstest.com");
    }
```
You have to set up the types of request and responses to using this method:
```
    @Override
    protected void initEndpoints(WebServiceEndpointRegistry endpointRegistry) {
        endpointRegistry
            .register(MirrorRequest.class, MirrorResponse.class)
            .register(ClientRequest.class, "AliasClientRequest", ClientResponse.class, "AliasClientResponse");
    }
```
Here you use the register() method to set the java classes responsible for creating and managing the types of requests and responses you want to send. You have to copy the classes you use in the tested application.

You can register your request-response like in the first call of the register() method (`.register(MirrorRequest.class, MirrorResponse.class)`) and in your [story](#write-a-storyreadmemdwrite-your-story) call the by their class name. Or you can register them like in the second call (`.register(ClientRequest.class, "AliasClientRequest", ClientResponse.class, "AliasClientResponse")`) and call them by their alias.

## Create a [TestConfig file](../../../README.md#create-a-testconfig-configuration-file)
Register `MyWebServiceHandler` as a Spring bean:
```
    @Bean
    @Qualifier("TESTQUALIFIER")
    public WebServiceHandler testWebServiceHandler() {
        return new MyWebServiceHandler();
    }
```

## Write a [story](../../../README.md#write-your-story)
Create a WebService.story file with this scenario:
```
Scenario: Web service scenario to confirm send request to web service and validate mirror response
Given [MirrorRequest] data for [TESTQUALIFIER]:
| name      | data  |
| firstName | Mario |
| brother   | Luigi |
| age       | 24    |
When [MirrorRequest] is sent to [TESTQUALIFIER] with success
Then [MirrorResponse] values from [TESTQUALIFIER] match:
| name      | expectedValue | verifier |
| firstName | Mario         | EQ       |
| brother   | Luigi         | EQ       |
| age       | 24            | EQ       |
```

The first step will create a SOAP request of the type `MirrorRequest`, with the data from the ExamplesTable behind the colon:
1. `name` - the name by which the data can be identified
2. `data` - the value of the data

The second step sends the `MirrorRequest` and checks if the sending was successful.

The third step compares the response according to the ExamplesTable behind the colon:
1. `name` - name of the value from the response you want to compare
2. `expectadValue` - the value you want to compare the recieved value to
3. `verifier` - the type of comparison ([verifier](../../src/main/java/org/jbehavesupport/core/verification/VerifierNames.java)) you want to use to compare the two values. (In this exmaple `EQ` - equals.)

To learn more about web service steps, check out [Web-service.md](../Web-service.md)

## Write the [story class](../../../README.md#write-your-story-class)
This is the runnable class