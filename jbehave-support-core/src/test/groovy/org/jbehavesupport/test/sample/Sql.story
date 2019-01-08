A story is collection of scenarious for testing sql steps

Narrative:
In order to test sql steps in jbehave-support-core
As a development team
I want to confirm correct functionality

Scenario: Execute SQL query on database and verify result

Given this query is performed on [TEST]:
select * from person order by first_name
Then the result set has 3 row(s)
Then the result set has {PLUS:1:1:1} row(s)
Then these rows match the query result:
| FIRST_NAME | LAST_NAME |
| Jane       | Doe       |
| John       | Doe       |
| Michael    | Doe       |

Scenario: Execute update SQL query on database and verify result

Given this update is performed on [TEST]:
update person set first_name = 'James' where first_name = 'John'
When this query is performed on [TEST]:
select * from person where first_name = 'James'
Then these rows match the query result:
| FIRST_NAME | LAST_NAME |
| James      | Doe       |
Given this update is performed on [TEST]:
update person set first_name = 'John' where first_name = 'James'

Scenario: Execute SQL query on database with parameters and verify result

When this query is performed on [TEST]:
select * from person where first_name = :firstName
with parameters:
| name      | data  |
| firstName | John  |
Then these rows match the query result:
| FIRST_NAME | LAST_NAME |
| John       | Doe       |

Scenario: Execute SQL query on database with parameters and store first_name to context

When this query is performed on [TEST]:
select * from person where first_name = 'John'
When these columns from the single-row query result are saved:
| name       | contextAlias |
| first_name | first_name   |
Then context contains [John] under [first_name]

Scenario: Execute SQL query on database with parameters and store multi row first_name to context

When this query is performed on [TEST]:
select * from person order by first_name
When these columns from the multi-row query result are saved:
| name       | contextAlias |
| first_name | first_name   |
Then context contains [Jane] under [first_name[0]]
Then context contains [John] under [first_name[1]]


Scenario: Execute SQL query with inline parameters on database with parameters and store multi row first_name to context

Given the value [John] is saved as [JOHN_VARIABLE]
When this query is performed on [TEST]:
select * from person where first_name = '{CP:JOHN_VARIABLE}'
Then these rows match the query result:
| FIRST_NAME | LAST_NAME |
| John       | Doe       |


Scenario: Execute SQL DELETE statement

When this update is performed on [TEST]:
insert into person (first_name,last_name) values ('Lucifer', 'Unwanted');

When this update is performed on [TEST]:
delete from person where first_name = 'Lucifer';

Scenario: columns equality
Given this query is performed on [TEST]:
select LAST_NAME AS LN1, LAST_NAME AS LN2 from person
Then these columns from the query result are equal:
| column1 | column2 |
| LN1     | LN2     |

And these rows are present in the query result:
| LN1 | LN2 |
| Doe | Doe |
| Doe | Doe |

Scenario: Check NULL command

When this update is performed on [TEST]:
insert into person (first_name,last_name,age) values ('null', 'value', null);

Given this query is performed on [TEST]:
select first_name, last_name, age from person where first_name = 'null'
Then these rows match the query result:
| first_name | last_name | age    |
| null       | value     | {NULL} |

When this update is performed on [TEST]:
delete from person where first_name = 'null';


Scenario: Query fails with message

Given this query with expected exception is performed on [TEST]:
select first_name from person_
Then query fails and error message contains: Table "PERSON_" not found


Scenario: NULL command in step - these rows are present in the query result
Given this query is performed on [TEST]:
select first_name, last_name, age, last_update from person
Then these rows are present in the query result:
| first_name | last_name | age    | last_update |
| Michael    | Doe       | {NULL} | {NULL}      |
