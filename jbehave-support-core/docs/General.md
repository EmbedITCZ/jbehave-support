[Contents](../README.md)

## Escaping special characters

Characters `:`, `'`, `{` and `}` are treated as special characters they are used to mark an expression command (`{` and `}`), delimit parameters (`:`) or to force ignoring of the `:` delimiter (using `'`) in an expression command.
When JBehave-support tries to parse the string `{x:y}` as an expression command it will return an error.
If you need use such a string you have to use `\` as an escape character for each special character individually. The string above should be typed as `\{x\:y\}`.   
You can also use the `'` character to escape whole sequences with delimiter, so for string `11:22:33` you could just type `'11:22:33'` instead of escaping each `:`.  
This behaviour is documented in [ExpressionEvaluatorTest](../src/test/groovy/org/jbehavesupport/core/expression/ExpressionEvaluatorTest.groovy)
and in [EscapingInContext story](../src/test/groovy/org/jbehavesupport/test/sample/EscapingInContext.story).


---


## Verification

### Comparison operators

For some verification steps it's possible to use verifiers such as:
`EQ` - equals - default when no operator is specified
`NE` - not equals
`LT` - lower than
`GT` - greater than
`CONTAINS` - String contains another String
`NOT_CONTAINS` - String doesn't contain another String
`SIZE_EQ` - Size of collection is exactly expected value
`SIZE_LT` - Size of collection is lower than expected value
`SIZE_GT` - Size of collection is greater than expected value
`REGEX_MATCH` - Matches String using regular expression - matches whole string
`REGEX_FIND` - Check if regular expression matches any part of given value - matches data on one line

The following sample step compares data from a ClientResponse from MYAPP against values in test context using operators.

```
Then [ClientResponse] values from [MYAPP] match:
| name        | verifier | expectedValue |
| client.cuid | NE       | {CP:CUID_1}   |
| client.cuid | NE       | {CP:CUID_2}   |
| client.cuid | NE       | {CP:CUID_3}   |
```

Following step compares values in every row (default verifier: EQ)

```
Then following data are compared:
| data              | expectedValue      | verifier |
| TEST              | TEST               | EQ       |
| TEST_CONTAINS     | TEST               | CONTAINS |
| {CP:ACTUAL_VALUE} |{CP:EXPECTED_VALUE} |          |
```

#### Limiting verification error count in logs

Verification steps use soft assertions to report errors - by default only first 10 comparison errors are reported (to avoid out of memory problems with long logs).  
This number can be changed by setting the property `verifier.max.assert.count`.


---
