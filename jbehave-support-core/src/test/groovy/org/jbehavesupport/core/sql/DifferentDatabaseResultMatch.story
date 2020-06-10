Scenario: comparing database results fails
Given this query is performed on [TEST]:
select LAST_NAME AS LN, FIRST_NAME AS FN, AGE, to_char(LAST_UPDATE, 'yyyy-mm-dd') as LAST_UPDATE from person

Then these rows match the query result:
| AGE     | FN      | LAST_UPDATE | LN     |
| 1       | Dummy   | 2001-06-18  | Does't |
| 29      | Jane    | 2018-06-18  | Doe    |
| {NULL}  | {NULL}  | {NULL}      | {NULL} |


