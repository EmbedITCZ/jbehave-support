[Contents](../README.md)

## Splunk steps

 * **WARNING**: Splunk implementation is still under active development and changes can/will be done.
 * Please use at your own risk. If you want to use splunk support please contact us and let us know. 

### Configuration
All the search queries are handled by a bean of `SplunkClient` type.

It is configured with a few properties that will be set to a bean of `SplunkConfig` type.

```java
    @Bean
    SplunkConfig splunkConfig() throws IOException {
        return SplunkConfig.builder()
            .host(env.getProperty("splunk.host"))
            .port(Integer.parseInt(env.getProperty("splunk.port")))
            .scheme(env.getProperty("splunk.scheme"))
            .sslSecurityProtocol(nonNull(env.getProperty("splunk.sslSecurityProtocol")) ? SSLSecurityProtocol.valueOf(env.getProperty("splunk.sslSecurityProtocol")) : null)
            .username(env.getProperty("splunk.credentials.username"))
            .password(env.getProperty("splunk.credentials.password"))
            .token(env.getProperty("splunk.credentials.token"))
            .build();
    }
```

The properties are following:

| name                           | value                                  | note                                                    |
|--------------------------------|----------------------------------------|---------------------------------------------------------|
| splunk.host                    | host with running Splunk instance      |                                                         |
| splunk.port                    | 8089                                   |                                                         |
| splunk.scheme                  | http or https                          |  Use "https" for EIT/Local Docker Splunk instance       |
| splunk.sslSecurityProtocol     | TLSv1_2                                |  Used when splunk.scheme="https"                        |
| splunk.credentials.username    | technical user username                |  Used for test purposes when you dont have authToken    |  
| splunk.credentials.password    | technical user password                |  Used for test purposes when you dont have authToken    |
| splunk.credentials.token       | Bearer <your Splunk auth token>        |  Prefer this to username/password                       |


Example of configuration working with local Docker Splunk instance: 
```
splunk:
  host: localhost
  port: 8089
  scheme: https
  sslSecurityProtocol: TLSv1_2
  credentials:
    token: Bearer <your Splunk auth token>
```

Example of configuration working with Splunk endpoints mocked by JBehaveSupport Core Test Application: 
```
splunk:
  host: localhost
  port: 11110
  scheme: http
  credentials:
    username: admin
    password: password
```

Apart of `SplunkConfig` bean, user has to provide a bean `SplunkClient` which depends on `SplunkConfig`. No default bean will be provided.

```java
    @Bean
    public SplunkClient splunkClient(SplunkConfig config) {
        return new OneShotSearchSplunkClient(config);
    }
```

### Available steps
There are two steps available for sending requests to Splunk Search API using following input:
* Splunk search query
* Splunk search query within a given timeframe

#### Splunk Query
The following step sends a request without a time range.
```
When the Splunk search query is performed:
search index="am-index" namespace="*" level="*" message="*response*" traceId="{CP:TRACEID_VARIABLE}" | tail 1
```

#### Splunk Query with Time Range
The following step will send a request including time range.

```
When the Splunk search query is performed within [2020-07-10T00:00:00.000+02:00] and [2020-07-20T23:59:59.000+02:00]:
search index="am-index" namespace="*" level="*" message="*response*" traceId="{CP:TRACEID_VARIABLE}" | tail 1
```

Please note that the both boundaries can leverage context variables as well as it is available in the query.
```
When the Splunk search query is performed within [{CP:EARLIEST_TIME_VARIABLE}] and [{CP:LATEST_TIME_VARIABLE}]:
search index="am-index" namespace="*" level="*" message="*response*" traceId="{CP:TRACEID_VARIABLE}" | tail 1
```

####  Verification Steps
Search results can be verified with the help of two more steps.

##### Search Result Size
You can verify that Splunk returned particular number of rows
``` 
Then the Splunk search result set has 1 row(s)
```

You can also verify that all returned rows match verifier rules.
```
Then the Splunk search result match these rules:
| data                                              | verifier        |
| ^.*200 OK with headers.*$                         | REGEX_MATCH     |
| X-B3-TraceId:"30a3d60bd3d698eae25eaf5afe3e1df5"   | CONTAINS        |
| does not contain                                  | NOT_CONTAINS    |
|                                                   | NOT_NULL        |
| no equality                                       | NE              |
```