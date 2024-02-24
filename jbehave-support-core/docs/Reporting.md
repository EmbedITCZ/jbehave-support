[Contents](../README.md)

## Reporting
There is an out-of-the-box support prepared for XML reports to be generated for each story as well as creating an index file with links to each report.

This feature is turned on by default and registers XmlReporterFactory bean. To turn it off just set `reporting.enabled` property to `false`.
For implementation details please check the `XmlReporterAutoConfiguration` class.

We also autoconfigure [reporter extension](#reporter-extensions) (bean implementing a XmlReporterExtension interface).

The default generated XML report contains only information about:
 - story narrative
 - story start time
 - story end time
 - story duration
 - story status (e.g. failed, successful)
 - story steps

Rest of the information available in the report depends on the extensions autoconfigured/manually registered.

In case you want to have narrative in report you have to follow JBehave narrative format:
```
In order to explain how to use narrative
As a development team
I want to show you how to do it
```
All three fixed part of narrative must be present in given order: `In order to`, `As a` and `I want to`

### Reporter extensions
The default report can be extended with other info by registering beans implementing the interface `XmlReporterExtension` 
(for convenience an abstract class `AbstractXmlReporterExtension` containing methods for writing XML elements can also be used).

There are several extensions already prepared and ready to use:
 - `EnvironmentInfoXmlReporterExtension` (copies Spring Environment properties starting with name `environmentInfo`, can be explicitly disabled by setting property `reporting.environment.enabled` to `false`)
 - `RestXmlReporterExtension` (copies REST requests/responses from [RestServiceSteps](Rest-api.md), can be explicitly disabled by setting property `reporting.rest.enabled` to `false`)
 - `WsXmlReporterExtension` (copies SOAP requests/responses from [WebServiceSteps](Web-service.md), can be explicitly disabled by setting property `reporting.ws.enabled` to `false`)
 - `TestContextXmlReporterExtension` (copies contents of [TestContext](Test-context.md), can be explicitly disabled by setting property `reporting.context.enabled` to `false`)
 - `FailScreenshotsReporterExtension` (prints out error screenshots from [Web testing](Web-testing.md) - if any were generated, can be explicitly disabled by setting property `reporting.web.screenshot.failed.enabled` to `false`)
 - `SqlXmlReporterExtension` (copies SQL statements/results from [SqlSteps](Sql-steps.md), can be explicitly disabled by setting property `reporting.sql.enabled` to `false`)
 - `ScreenshotReporterExtension` (prints out screenshots (except error) from [Web testing](Web-testing.md) - if any were generated, can be explicitly disabled by setting property `reporting.web.screenshot.enabled` to `false`)
   - Frequency of screenshot taking can be controlled by property: 'web.screenshot.reporting.mode'
     - MANUAL: screenshots from a manual step only
     - WAIT: screenshots after every web wait
     - STEP: screenshots after every web step
     - DEBUG: screenshots after every web step and action
  - `ServerLogXmlReporterExtension` (copies server logs, or their parts used by [SshSteps](Ssh.md), can be explicitly disabled by setting property `reporting.ssh.enabled` to `false`)
    - Content can be controlled by property: ssh.reporting.mode 
      - FULL: copies server log(s) for each system with configured [SshTemplate](Ssh.md)
      - TEMPLATE: copies server log(s) for each system with configured SshTemplate with an attribute reportable = true
      - CACHE: copies cached server log(s) used within scenario execution
    - Extension contains fail mode, which acts like TEMPLATE mode if test fails
      - it can be turned on by using property: ssh.reporting.logOnFailure with value "true"


These extensions are autoconfigured by default if it makes sense (e.g. if there is a `DataSource` bean available for `SqlXmlReporterExtension`, and so on).

#### Report steps
Following step allows setting specific server log report extension mode
Step throws AssertionError when ServerLogXmlReporterExtension isn't registered.
```
Given ssh reporter mode is set to [TEMPLATE]
```

---
