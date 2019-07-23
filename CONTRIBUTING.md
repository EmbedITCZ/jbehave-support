# Contributors guide
We are happy You are considering contributing to jbehave-support! 😃

Before you start, please read this short guide, so You don`t get lost. If You have any questions, do not hesitate to ask either on Gitter or through GitHub issues.

## Dev environment
This project is written in Java 8 using the latest version of IntelliJ IDEA as an IDE. These are the plugins we use:
* SonarLint
* Spock Framework Enhancements
* GMavenPlus IntelliJ Plugin
* JBehave support
* Lombok

## Code
### Coding guidelines
1. Do not use asterisk imports (`org.springframework.beans.factory.*`)
2. If possible, use Lombok constructor injection
    * Do not use in groovy tests
    * Do not use for optional dependencies `@Autowired(required = false)`
3. If possible, write [tests](#testing) for Your code.
4. Use SonarLint to prevent issues (delete unused imports etc.)
    
### Formatting
Please keep the coding style uniform. (In IntelliJ IDEA select the text You want to format and press `Ctrl+Alt+L` (Windows) or `Cmd+Alt+L` (Mac).)

## Documentation
For documentation we use .md files stored in `jbehave-support-core/docs` and public APIs are documented with JavaDoc.

## Building the project with Maven
To build this project use the standard `mvn clean install`. We have 3 building profiles:
1. development
    * Build for contributors;
2. integration-test
    * Build for integration tests; Builds the app with all tests and runs them against test application backend (launched as part of the profile). 
3. release
    * Used for new version releases
    
## Bug reporting
If you find a bug or have a question, feel free to create a new GitHub issue. Or contact us on:

[![Gitter](https://badges.gitter.im/jbehave-support/community.svg)](https://gitter.im/jbehave-support/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

## Testing
We write tests primarily in groovy (with the exception of Sample stories mentioned below). The tests are running against a mock aplication located in the `jbehave-support-core-test` folder.

Sample stories (`jbehave-support-core/src/test/groovy/org/jbehavesupport/test/sample`) are samples for users and tests at the same time.
