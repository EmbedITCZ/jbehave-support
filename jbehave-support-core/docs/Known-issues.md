[Contents](../README.md)
## Known issues

- [UI testing](#ui-testing)
- [Reports](#reports)

### UI testing
#### Wrong element selected when using JavaScript frameworks that change DOM structures often
When testing UIs written in JS frameworks changing DOM structure on the fly (React, Angular, etc.) 
you can sometimes encounter problems with selectors selecting different elements than expected (due to not all elements being present in DOM tree at the time when the selecting happens).

This is sadly a Selenium related issue which we cannot fix. 
In case you encounter this behavior then a solution for this is to introduce some static wait before running the selector.

### Reports
#### Broken Reports when using JBehave parametrised test with Examples keyword
Our reports are currently not being generated in a way that supports the `Examples` keyword.  
See [#80](https://github.com/EmbedITCZ/jbehave-support/issues/80) for more details.
