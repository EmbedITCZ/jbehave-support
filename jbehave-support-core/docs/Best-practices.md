[Contents](../README.md)
## Best practices

- [Project Setup](#project-setup)
- [UI testing](#ui-testing)

### Project Setup
#### Create new empty test project
To avoid problems with dependency conflicts we recommend creating empty testing project with JBehave Support Core only 
and adding other dependencies that you need (OXM etc.) as you go along.

#### Use dependency versions from the project
We use a fair number of third party dependencies (lots of them based around the Spring ecosystem) so unless you really 
need to then try to use the dependency versions already provided to avoid any weird problems, e.g. during update to newer versions. 
We also usually try to use the latest stable versions. 

### UI testing
#### Avoid static waits
Do not use `Thread.wait()` if at all possible. We understand that sometimes there is no other way than to use it with Selenium based testing, 
but usually using dynamic wait for some UI state (step `Then on [page] page wait until [element] [condition]`).
Following this simple rule can make your UI tests much more stable (with a bigger chance for better cross browser compatibility as well).
This is also the reason why we do not provide any step for static waits. 

#### Use separate [mapping file](Web-testing.md#mapping-files) for each system/page
Do **not** use one mapping file for multiple systems, while it might seem simpler there is very good possibility that it will bring you problems in the long run.
We recommend creating one mapping file for the whole system for smaller UIs and creating a mapping file per page/system functionality for larger ones.
