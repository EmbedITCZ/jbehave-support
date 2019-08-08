[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.jbehavesupport/jbehave-support-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.jbehavesupport/jbehave-support-core)
[![Build Status](https://travis-ci.org/EmbedITCZ/jbehave-support.svg?branch=master)](https://travis-ci.org/EmbedITCZ/jbehave-support)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/1e5c39dfaa6240b8b448d0df114c0d8e)](https://www.codacy.com/app/jbehavesupport/jbehave-support?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=EmbedITCZ/jbehave-support&amp;utm_campaign=Badge_Grade)
[![Gitter](https://badges.gitter.im/jbehave-support/community.svg)](https://gitter.im/jbehave-support/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

# jbehave-support

Light extension to [JBehave](https://jbehave.org).

## Contents

1. [Simple use case](#simple-use-case)
    1. [Add to Java project as a Maven dependency](#add-to-java-project-as-a-maven-dependency)
    2. [Create a TestConfig configuration file](#create-a-testconfig-configuration-file)
    3. [Create a ui-mapping file](#create-a-ui-mapping-file)
    4. [Write your story](#write-your-story)
    5. [Write your story class](#write-your-story-class)
2. [Modules](#modules)
    
## Simple use case

To show you how to set up a jbehave-support project, I am going to make a test that Google searches `EmbedITCZ jbehave-support` and checks the result. To learn more about this example check out [Web-testing.md](jbehave-support-core/docs/Web-testing.md).

### Add to Java project as a Maven dependency

To add jbehave-support to a java project, just add [this](https://mvnrepository.com/artifact/org.jbehavesupport/jbehave-support-core) dependency to your pom.xml.
```
<dependency>
    <groupId>org.jbehavesupport</groupId>
    <artifactId>jbehave-support-core</artifactId>
    <version>1.0.8</version>
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
Now you can add all the [reporters](jbehave-support-core/docs/Reporting.md) you need as Spring bean methods. Note: You allways need to create the `XmlReporterFactory` and one specific factory (here: `TestContextXmlReporterExtension`).
```
@Bean
public XmlReporterFactory xmlReporterFactory() {
        return new XmlReporterFactory();
}

@Bean
public TestContextXmlReporterExtension testContextXmlReporterExtension (TestContext testContext) {
    return new TestContextXmlReporterExtension(testContext);
}
```
Setting up the application you want to test largely depends on what do you want to test. Generally, you need to add a Spring bean method setting up the necessary parameters.
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

The `elementLocatorsSource` methods sets the name of a [file](#create-a-ui-mapping-file) containing adresses of web page elements we want to interact with.

### Create a ui-mapping file

In this yaml file, we need to setup the links to web page elements we want to interact with. I has to be placed in the resources directory, which is on one level above your main code directory:
- Project
    - src
        - main
            - java
                - your.main.code.directory
            - <code>**resources**</code>
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

The `.css` extension tells the code, what type of address to look for. (Ex. `.xpath` or `.id`)

The part after the colon is the address of the element itself.

### Write your story

In the same resources directory you have your `home.yaml` file, create a `.story` file. I will call it `Google.story`.

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

### Write your story class

Create a Java class that extends `AbstractSpringStories` and call it `<yourStoryName>Story`. Add the annotation `@ContextConfiguration(classes = TestConfig.class)` to link it with your TextConfig class. Leave this class empty.
```
@ContextConfiguration(classes = TestConfig.class)
public class GoogleStory extends AbstractSpringStories {
}
```

This class is runnable. When you run it, it runs your story.

## Modules
- [core](jbehave-support-core/README.md) - details about integration and usage 


