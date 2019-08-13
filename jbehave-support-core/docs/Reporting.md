[Contents](../README.md)

## Reporting
There is an out of the box support prepared for XML reports to be generated for each story as well as creating an index file with links to each report.

To use this feature all that is needed is to register a bean of type XmlReporterFactory, e.g.:
```
@Bean
public XmlReporterFactory xmlReporterFactory() {
    return new XmlReporterFactory();
}
```

Plus at least one [reporter extension](#reporter-extensions) (bean implementing a XmlReporterExtension interface).

The default generated XML report contains only information about:
 - story start time
 - story end time
 - story duration
 - story status (e.g. failed, successful)
 - story steps

Rest of the information available in the report depends on the extensions used/registered.

### Reporter extensions
The default report can be extended with other info by registering beans implementing the interface XmlReporterExtension 
(for convenience an abstract class AbstractXmlReporterExtension containing methods for writing XML elements can be used).

There are several extensions already prepared and ready to use:
 - `EnvironmentInfoXmlReporterExtension` (copies Spring Environment properties starting with name `environmentInfo`)
 - `RestXmlReporterExtension` (copies REST requests/responses from [RestServiceSteps](Rest-api.md))
 - `WsXmlReporterExtension` (copies SOAP requests/responses from [WebServiceSteps](Web-service.md))
 - `TestContextXmlReporterExtension` (copies contents of [TestContext](Test-context.md))
 - `FailScreenshotsReporterExtension` (prints out error screenshots from [Web testing](Web-testing.md) - if any were generated)
 - `SqlXmlReporterExtension` (copies SQL statements/results from [SqlSteps](Sql-steps.md))
 - `JmsXmlReporterExtension` (copies [JMS](Jms.md) message headers, prints out the message if it is `javax.jms.TextMessage`)
 - `ScreenshotReporterExtension` (prints out screenshots (except error) from [Web testing](Web-testing.md) - if any were generated)
   - Frequency of screenshot taking can be controlled by property: 'web.screenshot.reporting.mode'
     - MANUAL: screenshots from manual step only
     - WAIT: screenshots after every web wait
     - STEP: screenshots after every web step
     - DEBUG: screenshots after every web step and action
 - `ServerLogXmlReporterExtension` (copies server logs or their parts used by [SshSteps](Ssh.md))
   - Content can be controlled by property: ssh.reporting.mode 
     - FULL: copies server log(s) for each system with configured [SshTemplate](Ssh.md)
     - TEMPLATE: copies server log(s) for each system with configured SshTemplate with attribute reportable = true
     - CACHE: copies caches server log(s) used within scenario execution
   - Extensions contains fail mode, which act like TEMPLATE mode if test fails
     - it can be turned on by using property: ssh.reporting.logOnFailure with value "true"
     
To use these extensions simply register the wanted extension as a bean, e.g.:
```
@Bean
public WsXmlReporterExtension wsXmlReporterExtension() {
    return new WsXmlReporterExtension();
}
```

---
