# Migration guide

## Version 2.*

### @ConditionalOnMissingBean removed
We removed `org.jbehavesupport.core.internal.ConditionalOnMissingBean` annotation, you can use Spring Boot `org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean` alternative if needed.
