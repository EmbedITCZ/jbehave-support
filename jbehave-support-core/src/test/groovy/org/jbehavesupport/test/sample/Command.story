Narrative:
In order to test divide comman
As a development team
I want to compare divided values

Scenario: Test open steps

Then following data are compared:
| data                | expectedValue | verifier   |
| {DIVIDE:1:1:1}      | 1             | EQ         |
| {DIVIDE:4:2}        | 2             | EQ         |
| {DIVIDE:1.1:1.5:1}  | 0.73333       | EQ         |
| {DIVIDE:1.1:1.5:5}  | 0.14667       | EQ         |
| {DIVIDE:-2.6:-1.3:1}| 2             | EQ         |
| {DIVIDE:1.1:-1.1:1} | -1            | EQ         |
