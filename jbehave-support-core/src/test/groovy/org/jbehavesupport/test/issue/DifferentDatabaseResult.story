Scenario: comparing database results fails
Given this query is performed on [TEST]:
select LAST_NAME AS LN, FIRST_NAME AS FN, AGE, to_char(LAST_UPDATE, 'yyyy-mm-dd') as LAST_UPDATE from person


Then these rows are present in the query result:
| LN   | FN   | AGE | LAST_UPDATE |
| Doe  | John | 31  | 2018-06-18  |
| Doe  | JANE | 29  | 2018-06-18  |
| doe  | Jane | 29  | 2018-06-18  |
| Doe  | Jane | 29  | 2018-06-18  |
| Doe  | john | 31  | 2018-06-18  |
| Doe  | Jane | 29  | 2018-06-18  |
| Doe  | Jane | 29  | 2018-06-18  |
| Doe  | John | 31  | 2018-06-18  |
| Doe  | John | 31  | 2018-06-17  |
| Doe  | John | 31  | 18/06/2018  |


