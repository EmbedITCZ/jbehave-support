[Contents](../README.md)

## Sql steps

### Configuration

For each database used in the scenarios a bean of javax.sql.DataSource type with a database ID qualifier should be added to the Spring context with a qualifier that will be used in the scenario steps.

```
@Bean
@Qualifier("MYAPP")
public DataSource testDatasource() {
    BasicDataSource dataSource = new BasicDataSource();
    dataSource.setDriverClassName(env.getProperty("db.driver"));
    dataSource.setUrl(env.getProperty("db.url"));
    dataSource.setUsername(env.getProperty("db.username"));
    dataSource.setPassword(env.getProperty("db.password"));
    return dataSource;
}
```

> SQL steps are not transactional. Each sql statement is executed in autocommit mode.


### Usage of Sql related steps:

The following example will query the *MYAPP* database with the provided query.
The *:id* placeholder in the query will be replaced the *ID* value from the test context as specified in the table below the query.
`Given` or `When` can be used to call the step.
```
Given this query is performed on [MYAPP]:
select first_name from client where id = :id
with parameters:
| name | data    |
| id   | {CP:ID} |
```

A step for querying a database without parameters is also available.
`Given` or `When` can be used to call the step.


```
Given this query is performed on [MYAPP]:
select first_name from client
```

The next step will verify the query result matches the data in the provided example table.
The first row contains column names. All the values will be injected from the test context.

```
Then these rows match the query result:
| FIRST_NAME        | LAST_NAME        | DATE_OF_BIRTH        |
| {CP:FIRST_NAME_1} | {CP:LAST_NAME_1} | {CP:DATE_OF_BIRTH_1} |
| {CP:FIRST_NAME_2} | {CP:LAST_NAME_2} | {CP:DATE_OF_BIRTH_2} |
```

For verifying only a subset of the query result set the following step should be used.

```
Then these rows are present in the query result:
| FIRST_NAME | LAST_NAME |
| John       | Doe       |
```

If it's only required to check the number of rows returned the following step should be used.

```
Then the result set has 2 row(s)
```

If it's necessary to save the query result into the test context so that they can be used later in the test scenario the following step does just that.
`Given` or `When` can be used to call the step.

```
When these columns from the single-row query result are saved:
| name          | contextAlias     |
| FIRST_NAME     | FIRST_NAME       |
| LAST_NAME      | LAST_NAME        |
| IS_BLOCKED     | BLOCKED          |
| IS_DEACTIVATED | DEACTIVATED      |
```

The step above will only work with a result set with exactly one row.
If more rows need to be saved the test context the following step should be used.
It will save all rows with an index number in [ ] such as FIRST_NAME[0].

```
When these columns from the multi-row query result are saved:
| name           | contextAlias     |
| FIRST_NAME     | FIRST_NAME       |
| LAST_NAME      | LAST_NAME        |
| IS_BLOCKED     | BLOCKED          |
| IS_DEACTIVATED | DEACTIVATED      |
```

It's also possible to run an UPDATE, INSERT or DELETE statements against the selected database. The following step does just that.
The contextAlias column in the parameters is optional.
If used and a name of a new context variable is entered into that column the value from the value column is stored in the test context under that variable name.
`Given` or `When` can be used to call the step.
```
Given this update is performed on [MYAPP]:
update client set first_name = :firstName where id = :id
with parameters:
|name      |data            |
|id        |{CP:ID}         |
|firstName |{CP:FIRST_NAME} |
```

The step below shows how to execute a DELETE statement with no parameters
```
When this update is performed on [MYAPP]:
delete from client
```

#### Exception checking

By default any exceptions are thrown from the steps right away as they occur.
A delayed exception handling can be enabled by using the step variants including the phrase `with expected exception` e.g.:
```
When this update with expected exception is performed on [MYAPP]:
```
```
Given this query with expected exception is performed on [MYAPP]:
```

These step variants cause the sql exception __NOT__ to be thrown immediately and allows the checking of exception using the following step
(in the example below we are checking the error message for the sentence `Table "PERSON_" not found`):
```
Then query fails and error message contains: Table "PERSON_" not found
```

If a step with delayed exception throwing is used using the above mentioned and there is another sql related test step run afterwards without first checking the exception, then the next step will fail with the original exception.
(And if there is no such sql related step run then the exception will be thrown at the end of the scenario - in AfterScenario)


#### Back up update

Following step is used to make sure that update returning DB to original state will be executed even when story fails
```
Given back up update with key [REVERT_NAME] is saved for database [MYAPP]:
update client set name = '{CP:ORIGINAL_VALUE}' where name like '{CP:CHANGED_VALUE}'
```

Update saved by this step will be executed after end of the story even when story fails. (Aborting story will prevent this update) 

---
