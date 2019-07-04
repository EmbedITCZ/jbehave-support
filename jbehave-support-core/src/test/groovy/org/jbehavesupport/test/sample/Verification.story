Scenario: Test comparison of columns and REGEX_VERIFIER

Then following data are compared:
| data                             | expectedValue | verifier   |
| TEST                             | TEST          | EQ         |
| TEST_CONTAINS                    | TEST          | CONTAINS   |
| TEST_REGEX                       | [OPQRS]E.*?E  | REGEX_FIND |
| {MAP:0:[0,Zero],[1,One]}         | Zero          | EQ         |
| {MAP:{NULL}:[{NULL},NOT_NULL]}   | NOT_NULL      | EQ         |
| {NULL}                           | {NULL}        |            |
