Scenario: Execute SQL query on database with parameters and store first_name to context - test fails for not existing table

When this query is performed on [TEST]:
select * from person_ where first_name = 'John'
And this update is performed on [TEST]:
update person set first_name = 'George'
