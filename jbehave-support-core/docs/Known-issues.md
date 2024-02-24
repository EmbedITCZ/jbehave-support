[Contents](../README.md)
## Known issues

- [Runner limitations](#runner-limitations)
- [UI testing](#ui-testing)
- [Reports](#reports)

### Runner limitations
#### Declarative lifecycle
Please do not use declarative lifecycle as our runner is not compatible with it. For more Lifecycle info look [here](https://jbehave.org/reference/stable/lifecycle.html).


### UI testing
#### Wrong element selected when using JavaScript frameworks that change DOM structures often
When testing UIs written in JS frameworks changing DOM structure on the fly (React, Angular, etc.) 
you can sometimes encounter problems with selectors selecting different elements than expected (due to not all elements being present in DOM tree at the time when the selecting happens).

This is sadly a Selenium related issue which we cannot fix. 
In case you encounter this behavior then a solution for this is to introduce some static wait before running the selector.

### Reports
#### Broken Reports when using JBehave parametrised test with Examples keyword at story level
Our reports don't fully work with examples at story level. Report will be generated, but some features may be broken.

If you really do need to use it anyway please put it inside `Lifecycle`, otherwise it will break the narrative.
E.g.:
```
Narrative:
In order to explain how the parametrized scenarios works
As a development team
I want to show you how to use story level examples

Lifecycle:
Examples:
|symbol|
|STK1  |
|STK2  |

Scenario: Symbol A
```
