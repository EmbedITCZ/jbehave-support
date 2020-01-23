Scenario: Execute SQL query on database with parameters and store first_name to context - test fails for not existing table

Given this query is performed on [TEST]:
select LAST_NAME AS LN1, LAST_NAME AS LN2 from person_
Then these columns from the query result are equal:
| column1 | column2 |
| LN1     | LN2     |

