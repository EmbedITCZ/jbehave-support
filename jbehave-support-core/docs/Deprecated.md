[Contents](../README.md)

## Deprecated functionality

### To be removed - do not use


### To be rewritten - do not use


### Already removed

Class _YamlPropertiesConfigurer_

>Deprecated since version 1.1.4 - will be removed in the future
>
>Replaced with YamlPropertySourceFactory
>
>Example:
>```
>\\ Deprecated example
>@Bean
>public static YamlPropertiesConfigurer yamlPropertiesConfigurer() {
>   return new YamlPropertiesConfigurer("common.yml");
>}
>
>\\ What should be used instead
>@Configuration
>@PropertySource(value = "common-env.yml", factory = YamlPropertySourceFactory.class)
>public class PropertyConfig {
>}
>```
>To use variables please use standard spring `${}` placeholder format see `org.springframework.core.env.PropertySource` for more information
>For example:
>```
>@PropertySource(value = {"common-env.yml", "${spring.profiles.active}.yml"}, factory = YamlPropertySourceFactory.class)
>```

---

Class _FirefoxWebDriverFactory_

>Deprecated since version 1.0.0 - will be removed soon
>
>Chrome browser should be used instead

---

Property `web.browser.driver.version`

>Deprecated since 2.0.0
>
>`web.browser.version` should be used instead, for older browser compatibility testing

---

Column _OPERATOR_

>Deprecated since version 1.0.0 - will be removed soon
>
>Replaced with column _VERIFIER_
>
>Example:
>```
>!-- Deprecated example
>Then the following data are present in [TEST] log:
>| data      | OPERATOR |
>| my string | CONTAINS |
>
>!-- What should be used instead
>Then the following data are present in [TEST] log:
>| data      | VERIFIER |
>| my string | CONTAINS |
>```
---

Step _Then new tab is opened and focused_

>Deprecated since version 1.0.7 - will be removed in future
>
>Use _Then tab with [$urlTitle] containing [$text] is focused_ instead
>
>Step focuses an already opened tab instead of opening one
>
>Example:
>```
>!-- Correct usage
>Given tab with [url] containing [google] is focused
>```
>
---

4 parameters constructor of class _SshTemplate_

>Deprecated since 1.1.0 - will be removed in future
>
>Use 5 parameters constructor instead
>
>The new constructor was added due to new implementation of ServerLogReportExtension
>
>Example:
>```
>\\ Deprecated example
>SshTemplate myTemplate = new SshTemplate(keySetting, timestampFormat, rollingLogResolver);
>
>\\ What should be used instead
>SshTemplate keyTemplate = new SshTemplate(keySetting, timestampFormat, rollingLogResolver, false);
>```

---

Step _Given log timestamp is saved as [$startTimeAlias]_

>Deprecated since version 1.0.10 - will be removed in future
>
>Use _Given current time is saved as log timestamp [$logTimeAlias]_ instead
>
>New step have adjusted wording
>
>Example:
>```
>!-- Deprecated example
>Given log timestamp is saved as [LOG_TIMESTAMP]
>
>!-- What should be used instead
>Given current time is saved as log timestamp [LOG_TIMESTAMP]
>```

---

Step _Then the following data are present in [$systemQualifier] log since [$startTimeAlias]:$presentData_

>Deprecated since version 1.0.9 - will be removed in future
>
>Use _Then the following data are present in [$systemQualifier] log:$presentData"_ instead
>
>New step works better with timestamps
>
>Example:
>```
>!-- Deprecated example
>Given current time is saved as log timestamp [LOG_TIMESTAMP]
>
>When [PATCH] request to [TEST]/[init/] is sent
>Then response from [TEST] REST API has status [OK]
>
>Then the following data are present in [$systemQualifier] log since [LOG_TIMESTAMP]:
>| data      | VERIFIER |
>| my string | CONTAINS |
>
>!-- What should be used instead
>Given log start timestamp is set to current time
>
>When [PATCH] request to [TEST]/[init/] is sent
>Then response from [TEST] REST API has status [OK]
>
>Given log end timestamp is set to current time
>
>Then the following data are present in [TEST] log:
>| data      | VERIFIER |
>| my string | CONTAINS |
>
>```
>
>Method _SshHandler.checkLogDataPresence(String systemQualifier, String startTimeAlias, String stringTable, Verifier verifier)_ will be deleted too

---

Step _Then the following data are not present in [$systemQualifier] log:$missingData_

>Deprecated since version 1.0.10 - will be removed in future
>
>Use _Then the following data are present in [$systemQualifier] log:$presentData"_ instead
>
>Verifier _NOT_CONTAINS_ should be used instead of whole step
>
>Example:
>```
>!-- What should be used instead
>Given log start timestamp is set to current time
>
>When [PATCH] request to [TEST]/[init/] is sent
>Then response from [TEST] REST API has status [OK]
>
>Given log end timestamp is set to current time
>
>Then the following data are not present in [TEST] log:
>| data           |
>| missing string |
>
>!-- What should be used instead
>Given log start timestamp is set to current time
>
>When [PATCH] request to [TEST]/[init/] is sent
>Then response from [TEST] REST API has status [OK]
>
>Given log end timestamp is set to current time
>
>Then the following data are present in [TEST] log:
>| data           | VERIFIER     |
>| missing string | NOT_CONTAINS |
>
>```

---

_WebTableSteps_
>Deprecated since 1.1.0 - old version was archived to [separate repository](https://github.com/EmbedITCZ/jbehave-support-web-tables)
>
>Current steps were rewritten/simplified to support only simple HTML tables.

---

_JMSSteps_
>Old version was archived to [separate repository](https://github.com/EmbedITCZ/jbehave-support-jms)
>
>Removed altogether.
