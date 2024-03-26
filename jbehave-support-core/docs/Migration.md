# Migration guide

## Version 2.*

### Reporting Autoconfiguration
`XmlReporterFactory` and corresponding `ReporterExtension` classes now get autoconfigured by Spring Boot.  
There is now no need to declare them explicitly.
See more info in [Reporting.md](Reporting.md)

### @ConditionalOnMissingBean removed
We removed `org.jbehavesupport.core.internal.ConditionalOnMissingBean` annotation, you can use Spring Boot `org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean` alternative if needed.

### Deprecated code removal
We have removed all previously deprecated code - please refer to [Deprecated.md](Deprecated.md) for more details how to migrate.

### Spring 6 upgrade
We are currently using Spring 6 internally, this brings with it several other requirements:

> #### Java 17
We now currently require Java 17 as a minimal Java version (previously 8).

> #### Jakarta EE 9
Spring 6 internally brings with it upgrade to Jakarta EE 9, this means migration from old `javax.*` to `jakarta.*` packages, please consult guides on the Internet (and or your IDE support for migration) if needed.

### Spring Boot 3 introduction
We are now internally using Spring Boot - this means that ideally your main test configuration file should be annotated with both `@SpringBootConfiguration` and `@EnableAutoConfiguration`.
