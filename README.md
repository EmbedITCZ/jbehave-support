[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.jbehavesupport/jbehave-support-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.jbehavesupport/jbehave-support-core)
[![CI](https://github.com/EmbedITCZ/jbehave-support/workflows/CI/badge.svg)](https://github.com/EmbedITCZ/jbehave-support/actions?query=CI%3ABuild+branch%3Amaster)
[![BrowserStack](https://github.com/EmbedITCZ/jbehave-support/workflows/BrowserStack/badge.svg)](https://github.com/EmbedITCZ/jbehave-support/actions?query=BrowserStack%3ABuild+branch%3Amaster)
[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=EmbedITCZ/jbehave-support)](https://dependabot.com)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/1e5c39dfaa6240b8b448d0df114c0d8e)](https://www.codacy.com/app/jbehavesupport/jbehave-support?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=EmbedITCZ/jbehave-support&amp;utm_campaign=Badge_Grade)
[![Gitter](https://badges.gitter.im/jbehave-support/community.svg)](https://gitter.im/jbehave-support/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

# jbehave-support

Light extension to [JBehave](https://jbehave.org) using Spring framework.

Provides several base steps for working with 
[REST](jbehave-support-core/docs/Rest-api.md), 
[SOAP](jbehave-support-core/docs/Web-service.md), 
[JMS](jbehave-support-core/docs/Jms.md), 
[SQL](jbehave-support-core/docs/Sql-steps.md), 
[SSH](jbehave-support-core/docs/Ssh.md), 
[(Selenium based) web testing](jbehave-support-core/docs/Web-testing.md), 
[health checks](jbehave-support-core/docs/Health-checks.md) 
along with support for [verification](jbehave-support-core/docs/General.md#verification), 
[expression commands](jbehave-support-core/docs/Expression-commands.md) and 
basic [reporting](jbehave-support-core/docs/Reporting.md).

## Contents
1. [Modules](#modules)
1. [Contributors guide](#contributors-guide)
1. [Examples](#examples)
1. [Simple use case](#simple-use-case---web-testing)
    1. [Add to Java project as a Maven dependency](#add-to-java-project-as-a-maven-dependency)
    2. [Create a TestConfig configuration file](#create-a-testconfig-configuration-file)
    3. [Create a ui-mapping file](#create-a-ui-mapping-file)
    4. [Write your story](#write-your-story)
    5. [Write your story class](#write-your-story-class)
1. [Thanks](#thanks)

## Modules
- [core](jbehave-support-core/README.md) - details about integration and usage
- [core-test](jbehave-support-core-test/README.md) - contains details about the simple application used for integration tests
    
## Contributors guide
Contributors guide can be found in [CONTRIBUTING.md](CONTRIBUTING.md)

## Examples
- [examples](jbehave-support-core/docs/examples/Examples.md) - more example projects like the [simple use case](#simple-use-case---web-testing) shown below.
    
## Simple use case - Web testing

To show you how to set up a project using the jbehave-support library, I am going to make a test that Google searches `EmbedITCZ jbehave-support` and checks the result. To learn more about this example check out [Web-testing.md](jbehave-support-core/docs/Web-testing.md).

Of course you can use jbehave-support for much more than just selenium based testing. For example server communication ([SOAP](jbehave-support-core/docs/examples/Web-service.md), [REST](jbehave-support-core/docs/examples/Rest.md), [JMS](jbehave-support-core/docs/Jms.md)) or database manipulation ([SQL](jbehave-support-core/docs/Sql-steps.md)).

### Add to Java project as a Maven dependency

To add jbehave-support to a java project, just add [this](https://mvnrepository.com/artifact/org.jbehavesupport/jbehave-support-core) dependency to your pom.xml.
```
<dependency>
    <groupId>org.jbehavesupport</groupId>
    <artifactId>jbehave-support-core</artifactId>
    <version>[current version number]</version>
</dependency>
```
Then build your project (`mvn clean install`) to download all the necessary dependencies.

### Create a TestConfig configuration file

From this file, jbehave-support will take all the necessary information about tested applications and the types of reports you want.
First create a Java class and call it `TestConfig`. Add the spring annotation `@Configuration`.
```
@Configuration
public class TestConfig {
```
Setting up the application you want to test largely depends on what do you want to test. Generally, you need to add a Spring bean method setting up the necessary parameters. We will be setting-up a WebSetting for Selenium to access google.com. ([More](jbehave-support-core/docs/Web-testing.md#configuration) about setting up web testing)
```
@Bean
@Qualifier("GOOGLE")
public WebSetting google(){
    return WebSetting.builder()
          .homePageUrl("https://www.google.com")
          .elementLocatorsSource("home.yaml")
          .build();
}
```
The `@Qualifier` annotation sets up the name, under which we will be able to access this application in our `story`.

The `homePageUrl` method sets the url of the web applications home page.

The `elementLocatorsSource` methods sets the name of a [file](#create-a-ui-mapping-file) containing addresses of web page elements we want to interact with.

### Create a ui mapping file

In this yaml file, we need to setup the links to web page elements we want to interact with. I has to be placed in the resources directory, which is on one level above your main code directory:
- Project
    - src
        - main
            - java
                - your.main.code.directory
            - **resources**
                - home.yaml
            
The links and names should be written like this:
```
home:
  search.button.css: "#tsf > div:nth-child(2) > div > div.FPdoLc.VlcLAe > center > input[type='submit']:nth-child(1)"
  search.text.css: "input[type='text'][name='q']"
  search.output.css: "#rso > div:nth-child(1) > div > div:nth-child(1) > div > div > div.r > a > h3"
```
The title `home:` is the name of the page these elements can be found on.

Under the names `search.button`, `search.text` and `search.output`, we can use these elements in a story.

The `.css` extension tells the code, what type of address to look for. (Ex. `.xpath`)

The part after the colon is the address of the element itself.

[More](jbehave-support-core/docs/Web-testing.md#mapping-files) about ui mapping files

### Write your story

In the same resources directory you have your [`home.yaml` file](#create-a-ui-mapping-file), create a `.story` file. I will call it `Google.story`.

Inside write the narrative, which should explain what is the purpose of this story. It has 3 mandatory parts: `In order to`, `As a` and `I want to`.
```
Narrative:
In order to try jbehave-support
As a confused human
I want to see if I can set it up
```
Then write your scenario:
```
Scenario: Open Google
Given [GOOGLE] homepage is open
When on [home] page these actions are performed:
| element       | action | data                      |
| search.text   | FILL   | embeditcz jbehave-support |
| search.button | CLICK  |                           |
Then on [home] page these conditions are verified:
| element       | property | data    | verifier |
| search.output | TEXT     | EmbedIT | CONTAINS |
```
This scenario opens `www.google.com`, writes `embeditcz jbehave-support` into the Google search bar. Clicks `search` and checks if the first result contains the text `EmbedIT`. Notice instead of lengthy element addresses, the element names defined in `home.yaml` are used.

[More](jbehave-support-core/docs/Web-testing.md#web-steps) about web testing steps for your story

### Write your story class

Create a Java class that extends `AbstractSpringStories` and call it `<yourStoryName>Story`. Add the annotation `@ContextConfiguration(classes = TestConfig.class)` to link it with your TextConfig class. Leave this class empty.
```
@ContextConfiguration(classes = TestConfig.class)
public class GoogleStory extends AbstractSpringStories {
}
```
**This class is runnable. When you run it, it runs your story.**

## Thanks
### Jetbrains
For providing us open source licenses to IntelliJ IDEA.

### [![Browserstack](docs/browserstack.png)](https://browserstack.com) 
for providing us with a free license for our project.
We use BrowserStack in our build pipeline to make sure that our selenium based testing components are working correctly across multiple browsers.
