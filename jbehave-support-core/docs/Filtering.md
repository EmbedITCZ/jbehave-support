[Contents](../README.md)

## Filtering
There is support for filtering stories/scenarios based on metadata attributes. Filtering is implemented as extended story parser accepting custom MetaFilter implementation.
Parser also allows to set custom story name instead of default derived from story file name. This is extremely useful in the cases where single story file is reused and executed by different test suites and you need to distinguish one execution from another.

Below there is a description of features supported by extended filtering framework.
      
1.Stories/scenarios are filtered at the time of parsing, not at the time of execution. 
      
2. GroovyMetaFilterBuilder provides extended capabilities.

By default, value of meta attribute is simple string where you can do simple wildcard matching with default matcher or there you can do little bit more with default groovy matcher.
Improved groovy matcher allows standard set of operations on metadata values and introducing new operations `all()`, `only()`, `any()`, `none()` which treat metadata values as list of delimited tags. 
      
Let's take [SendSms.story](examples/SendSms.story) as an example.
      
In the story metadata we have: @api v1 v2 jms. This defines for what api versions the story is designed. When we run test we tell that we want to run it for api v1 (see: [SendSmsV1Story.java](examples/SendSmsV1Story.java)). 
      
Other examples:
      
- if we want to run test scenarios for apis `v1` or `v2`, we can use filter - any('api','v1','v2')
- if we want to include scenarios that contains all given values we can use filter - all('api', 'v1', 'v2'), this will be evaluated to true when metadata['attributes'].containsAll(['v1', 'v2']) is true
- if we want to run test scenarios only for apis `v1`, `v2` and `v3`, we can use filter - only('api','v1','v2','v3'), this will be evaluated to true when ['v1', 'v2', 'v3'].containsAll(metadata['attributes']) is true
- if we want to run scenario `SMS-004`, we can use eq('id','SMS-004')
- if we want to run other scenarios than `SMS-004`, we can use !eq('id','SMS-004')
- if we want to run scenarios where metadata matches `SMS-00.`, we can use matches('id','SMS-00.')
- if we want to run other scenarios than `SMS-004` that match `SMS-00.`, we can use !eq('id','SMS-004') && matches('id','SMS-00.')
     
In the examples above scenarios missing `@api` metadata or matching filter will be automatically included  
      
3.There is a special behaviour for predefined @features attribute. We use this when we need to run story to test particular features that may or may not be present in the environment. If specified, it enables test scenarios with `@features` metadata and containing all of given values. If not specified, scenarios marked with `@features` are not taken for execution. Scenarios not having `@features` meta are not affected.
      
Here scenarios having `@features` and not matching it will be automatically excluded.
      
In our example, scenario `SMS-004` will be executed only if we mention mssd when creating MetaFilter using GroovyMetaFilterBuilder
      
4.New groovy matcher can be easily extended to have new methods
      
All together it gives much more flexibility in managing scenarios to be executed.

Integration is usually done by setting custom parser when configuring JBehave.

Example:

```java
  configuration = new MostUsefulConfiguration();
  configuration.useStoryParser(
            new FilteringStoryParser("Scenario: "+testClass.getSimpleName(),
                new GroovyMetaFilterBuilder(testClass)
                    .metaFilter(filter)
                    .features(features)
                    .ignore(true)
                    .build()
                , configuration.storyControls().metaByRow()));

```   

---
