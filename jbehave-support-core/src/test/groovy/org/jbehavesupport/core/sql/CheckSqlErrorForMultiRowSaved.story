Scenario: Execute SQL query on database with parameters and store first_name to context - test fails for not existing table

When this query is performed on [TEST]:
select * from person_ where first_name = 'John'
When these columns from the multi-row query result are saved:
| name       | contextAlias |
| first_name | first_name   |
| first_name | first_name   |
