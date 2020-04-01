[Contents](../README.md)

## Health checks

Health checks are used to check if the tested components are running properly - are healthy.

It is done by attempting to connect to said component. If the connection is successful, the component is evaluated as healthy.

### Usage

To implement health checks into Your story use the `Given these components are healthy:` step.

Example:
>```
>Given these components are healthy:
>| component     |
>| YOURCOMPONENT |
>```  

When setting up the component, use the `HealthChecks.http(URL, username, password);` If neither username nor password is required, leave the respective fields blank.

Example:

>```
>@Bean
>@Qualifier("YOURCOMPONENT")
>HealthCheck realHealthCheck() {
>    return HealthChecks.http("https://www.google.com", "", "");
>}
>``` 
