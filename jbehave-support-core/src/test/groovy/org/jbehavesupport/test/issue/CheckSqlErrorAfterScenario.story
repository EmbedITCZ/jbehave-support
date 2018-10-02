Scenario: Execute SQL query on database with parameters and store first_name to context - test fails for not existing table

Given this query is performed on [TEST]:
select * from person_ order by first_name
