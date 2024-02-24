# Migration guide

## Version 2.*

### Reporting Autoconfiguration
`XmlReporterFactory` and corresponding `ReporterExtension` classes now get autoconfigured by Spring Boot.  
There is now no need to declare them explicitly.
See more info in [Reporting.md](Reporting.md)

### @ConditionalOnMissingBean removed
We removed `org.jbehavesupport.core.internal.ConditionalOnMissingBean` annotation, you can use Spring Boot `org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean` alternative if needed.
