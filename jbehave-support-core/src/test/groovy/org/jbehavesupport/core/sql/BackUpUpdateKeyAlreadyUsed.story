Scenario: Key already used
Given back up update with key [DELETE_LUCIFER] is saved for database [TEST]:
delete from persona where first_name = 'Lucifer'

Given back up update with key [DELETE_LUCIFER] is saved for database [TEST]:
delete from persona where first_name = 'Lucifer the second'