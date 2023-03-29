# Contributors guide
We are happy You are considering contributing to jbehave-support! 😃

Before you start, please read this short guide, so You don`t get lost. If You have any questions, do not hesitate to ask in GitHub discussions.

## Dev environment
This project is written in Java 17, however we are aiming for LTS Java and latest Java compatibility (CI is run against 17 and latest Java - currently 20). 

We are using the latest version of IntelliJ IDEA as an IDE. These are the plugins you need:
* SonarLint
* Spock Framework Enhancements
* GMavenPlus IntelliJ Plugin
* JBehave support
* Lombok
* Pipe Table Formatter

## Code
### Coding guidelines
1. Do not use asterisk imports (`org.springframework.beans.factory.*`)
2. If possible, use Lombok constructor injection
    * Do not use in groovy tests
    * Do not use for optional dependencies `@Autowired(required = false)`
    * Do not use in components, which the user is expected to extend (e.g. `WebServiceHandler`/`RestServiceHandler`), to allow for simple constructor injection by users on their subclasses.
3. If possible, write [tests](#testing) for Your code.
4. Use SonarLint to prevent issues (delete unused imports etc.)
5. When writing xsl transformations, call templates according to name, not match, in case the user wanted to use their own reporter with their own tags.
    
### Formatting
Please keep the coding style uniform. (In IntelliJ IDEA select the text You want to format and press `Ctrl+Alt+L` (Windows) or `Cmd+Alt+L` (Mac).)

## Documentation
For the documentation, we use .md files stored in `jbehave-support-core/docs` and public APIs are documented with JavaDoc. Sometimes, it is best to just show examples, as is the case with sample stories.

## Building the project with Maven
To build this project use the standard `mvn clean install`. We have only one specific building profile:
1. release
    * Used for new version releases
    
## Bug reporting
If you find a bug or have a question, feel free to create a new GitHub issue and/or discussion. 

## Testing
We write tests primarily in groovy (even though Java can be used as well, plus we use Sample stories mentioned below for testing as well). 
Integration tests are ran against a mock application located in the `jbehave-support-core-test` folder. To start it, run the `jbehave-support-core-test/jbehave-support-core-test-app/src/main/java/org/jbehavesupport/core/test/app/JbehaveSupportCoreTestApplication.class`.

Sample stories (`jbehave-support-core/src/test/groovy/org/jbehavesupport/test/sample`) are samples for users and tests at the same time.

## Multi-browser testing
We test our code against Chrome, Safari and Firefox browsers. Tests are not running in a pipeline before PR but after it in master branch.

To be able to run BrowserStack specific test in your local branch please:
 * fill your BrowserStack credentials into environment variables: `BROWSER-STACK_USERNAME`, `BROWSER-STACK_KEY` (overwrites properties in test.yml)
 * run the BrowserStack Local binary on your system, please refer to [BrowserStack documentation](https://www.browserstack.com/local-testing/automate#command-line) 
   for the correct download link and usage


## Release
>
> The release process is done by maven and for the setup, you should follow [the release document](docs/Release.md)
> 
