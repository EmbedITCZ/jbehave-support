Scenario: Update fails
Given back up update with key [DELETE_LUCIFER] is saved for database [TEST]:
delete from persona where first_name = 'Lucifer'
Given back up update with key [DELETE_LUCIFER_THE_SECOND] is saved for database [TEST]:
delete from personel where first_name = 'Lucifer the second'
