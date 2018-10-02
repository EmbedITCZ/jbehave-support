Scenario: Execution query fails for not existing table

Given this query is performed on [TEST]:
select first_name, last_name, age from person_ where first_name = 'null'
Then these rows match the query result:
| first_name | last_name | age    |
| null       | value     | {NULL} |


