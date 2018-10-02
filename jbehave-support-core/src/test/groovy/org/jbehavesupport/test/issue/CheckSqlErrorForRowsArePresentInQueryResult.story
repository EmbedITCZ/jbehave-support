Scenario: Execution query fails for not existing table

Given this query is performed on [TEST]:
select LAST_NAME AS LN1, LAST_NAME AS LN2 from person_
Then these rows are present in the query result:
| LN1 | LN2 |
| Doe | Doe |
| Doe | Doe |


