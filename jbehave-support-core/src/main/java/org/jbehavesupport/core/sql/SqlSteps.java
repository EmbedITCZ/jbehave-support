package org.jbehavesupport.core.sql;

import static org.jbehavesupport.core.support.TestContextUtil.putDataIntoContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.sql.DataSource;

import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.expression.ExpressionEvaluatingParameter;
import org.jbehavesupport.core.internal.verification.ContainsVerifier;
import org.jbehavesupport.core.internal.verification.EqualsVerifier;

import junit.framework.AssertionFailedError;
import org.assertj.core.api.SoftAssertions;
import org.jbehave.core.annotations.AfterScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.jbehavesupport.core.internal.ExampleTableConstraints;
import org.jbehavesupport.core.internal.ExamplesTableUtil;
import org.jbehavesupport.core.internal.MetadataUtil;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public final class SqlSteps {

    private static final String SQL_RESULT_KEY = "sql_result";
    private static final String SQL_QUERY_KEY = "sql_query";
    private static final String MISSING_SQL_MESSAGE = "sql query must be run and saved in context prior to this step";
    private static final String SQL_EXCEPTION_KEY = "sql_exception";

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;
    @Autowired
    private TestContext testContext;

    @Autowired
    private EqualsVerifier equalsVerifier;

    @Autowired
    private ContainsVerifier containsVerifier;

    @Given("this query is performed on [$databaseId]:$sqlStatement")
    @When("this query is performed on [$databaseId]:$sqlStatement")
    public void executeQuery(String databaseId, ExpressionEvaluatingParameter<String> sqlStatement) {
        checkSqlException();
        executeQuery(databaseId, sqlStatement, new ExamplesTable(""));
    }

    @Given(value = "this query is performed on [$databaseId]:$sqlStatement with parameters:$parameters", priority = 100)
    @When(value = "this query is performed on [$databaseId]:$sqlStatement with parameters:$parameters", priority = 100)
    public void executeQuery(String databaseId, ExpressionEvaluatingParameter<String> sqlStatement, ExamplesTable parameters) {
        checkSqlException();
        putDataIntoContext(testContext, parameters, ExampleTableConstraints.ALIAS, ExampleTableConstraints.DATA);
        String resolvedStatement = sqlStatement.getValue();

        List<Map<String, Object>> result = new ArrayList<>();

        try {
            result = resolveJdbcTemplate(databaseId)
            .queryForList(resolvedStatement, ExamplesTableUtil.convertMap(parameters, ExampleTableConstraints.NAME, ExampleTableConstraints.DATA));
            testContext.put(SQL_RESULT_KEY, result);
            testContext.put(SQL_QUERY_KEY, resolvedStatement);
        } catch (DataAccessException e) {
            testContext.put(SQL_EXCEPTION_KEY, e);
        }
    }

    @Given("this update is performed on [$databaseId]:$sqlStatement")
    @When("this update is performed on [$databaseId]:$sqlStatement")
    public void executeUpdate(String databaseId, ExpressionEvaluatingParameter<String> sqlStatement) {
        checkSqlException();
        executeUpdate(databaseId, sqlStatement, new ExamplesTable(""));
    }

    @Given(value = "this update is performed on [$databaseId]:$sqlStatement with parameters:$parameters", priority = 100)
    @When(value = "this update is performed on [$databaseId]:$sqlStatement with parameters:$parameters", priority = 100)
    public void executeUpdate(String databaseId, ExpressionEvaluatingParameter<String> sqlStatement, ExamplesTable parameters) {
        checkSqlException();
        putDataIntoContext(testContext, parameters, ExampleTableConstraints.ALIAS, ExampleTableConstraints.DATA);
        String resolvedStatement = sqlStatement.getValue();
        try {
            resolveJdbcTemplate(databaseId).update(resolvedStatement, ExamplesTableUtil
                .convertMap(parameters, ExampleTableConstraints.NAME, ExampleTableConstraints.DATA));
        } catch (DataAccessException e) {
            testContext.put(SQL_EXCEPTION_KEY, e);
        }

        testContext.put(SQL_QUERY_KEY, resolvedStatement);
    }


    @Given("these columns from the single-row query result are saved:$storedData")
    @When("these columns from the single-row query result are saved:$storedData")
    public void storeSqlQueryDataInTestContext(ExamplesTable fieldsToStore) {
        checkSqlException();
        notNull(fieldsToStore, "fields to store data can't be null");
        isTrue(testContext.contains(SQL_RESULT_KEY), MISSING_SQL_MESSAGE);
        isTrue((getSqlResult()).size() == 1, "there must be a sql result with exactly one row");

        Map<String, Object> sqlRow0 = getSqlResult().get(0);
        for (Map<String, String> row : fieldsToStore.getRows()) {
            checkColumnPresentInResultSet(sqlRow0, row);
            testContext.put(getTestContextAliasOrFieldName(row), sqlRow0.get(row.get(ExampleTableConstraints.NAME).toUpperCase()), MetadataUtil.userDefined());
        }
    }

    @When("these columns from the multi-row query result are saved:$storedData")
    public void storeMultiRowSqlQueryDataInTestContext(ExamplesTable fieldsToStore) {
        checkSqlException();
        notNull(fieldsToStore, "fields to store data can't be null");
        isTrue(testContext.contains(SQL_RESULT_KEY), MISSING_SQL_MESSAGE);

        int i = 0;
        for (Map<String, Object> resultRow : getSqlResult()) {
            for (Map<String, String> row : fieldsToStore.getRows()) {
                checkColumnPresentInResultSet(resultRow, row);
                testContext.put(getTestContextAliasOrFieldName(row) + "[" + i + "]", resultRow.get(row.get(ExampleTableConstraints.NAME).toUpperCase()), MetadataUtil
                    .userDefined());
            }
            i++;
        }
    }

    private String getTestContextAliasOrFieldName(final Map<String, String> row) {
        return row.containsKey(ExampleTableConstraints.ALIAS) ? row.get(ExampleTableConstraints.ALIAS) : row.get(ExampleTableConstraints.NAME);
    }

    private void checkColumnPresentInResultSet(Map<String, Object> resultRow, Map<String, String> row) {
        if (!resultRow.keySet().contains(row.get(ExampleTableConstraints.NAME).toUpperCase())) {
            throw new IllegalArgumentException("Column " + row.get(ExampleTableConstraints.NAME).toUpperCase() + " is not present in result set");
        }
    }

    @Then("these columns from the query result are equal:$columnsToCompare")
    public void compareColumns(ExamplesTable columnsToCompare) {
        checkSqlException();
        notNull(columnsToCompare, "columns to compare can't be null");
        isTrue(testContext.contains(SQL_RESULT_KEY), MISSING_SQL_MESSAGE);

        Map<String, String> columns = ExamplesTableUtil.convertMap(columnsToCompare, "column1", "column2");

        List<Map<String, Object>> resultSet = testContext.get(SQL_RESULT_KEY);
        for (Map<String, Object> row : resultSet) {
            for (Map.Entry<String, String> columnEntry : columns.entrySet()) {
                assertThat(row.get(columnEntry.getKey()))
                    .as("failed while comparing columns " + columnEntry.getKey() + " and " + columnEntry.getValue())
                    .isEqualTo(row.get(columnEntry.getValue()));
            }
        }
    }

    @Then("these rows match the query result:$matchingData")
    public void compareQueryResult(ExamplesTable matchingData) {
        checkSqlException();
        compareExampleTableVersusListOfMaps(matchingData, true);
    }

    @Then("these rows are present in the query result:$presentData")
    public void resultSetContainsData(ExamplesTable presentData) {
        checkSqlException();
        compareExampleTableVersusListOfMaps(presentData, false);
    }

    private void compareExampleTableVersusListOfMaps(ExamplesTable compareData, Boolean strictMatch) {
        notNull(compareData, "matching data can't be null");

        ArrayList<Map<String, Object>> queryResultsToMatch = new ArrayList<>();
        ArrayList<String> upperCaseHeaders = compareData.getHeaders().stream()
            .map(String::toUpperCase)
            .collect(Collectors.toCollection(ArrayList::new));

        for (Map<String, Object> row : getSqlResult()) {
            row.entrySet().removeIf(r -> !upperCaseHeaders.contains(r.getKey()));
            if (!row.isEmpty()) {
                queryResultsToMatch.add(row);
            }
        }
        compareExampleTableVersusListOfMaps(testContext.get(SQL_QUERY_KEY), compareData, queryResultsToMatch, strictMatch);
    }

    @Then("the result set has $rowCount row(s)")
    public void compareRowCount(ExpressionEvaluatingParameter<Integer> rowCount) {
        checkSqlException();
        assertThat(getSqlResult())
            .as("returned rows does not fit expected count")
            .hasSize(rowCount.getValue());
    }

    @Then("query fails and error message contains: $message")
    public void queryFailsWithMessage(String message) {
        if (testContext.contains(SQL_EXCEPTION_KEY)) {
            String messageFromContext = testContext.get(SQL_EXCEPTION_KEY, DataAccessException.class).getMessage();
            testContext.remove(SQL_EXCEPTION_KEY);
            containsVerifier.verify(messageFromContext, message);
        } else {
            throw new IllegalArgumentException("SQL query passed, error message wasn't caught.");
        }
    }

    private List<Map<String, Object>> getSqlResult() {
        return testContext.get(SQL_RESULT_KEY);
    }

    private NamedParameterJdbcTemplate resolveJdbcTemplate(String databaseId) {
        try {
            DataSource dataSource = BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, DataSource.class, databaseId);
            return new NamedParameterJdbcTemplate(dataSource);
        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalArgumentException("SqlSteps requires DataSource bean with qualifier [" + databaseId + "] but no was found", e);
        }
    }

    private void compareExampleTableVersusListOfMaps(String query, ExamplesTable expectations, List<Map<String, Object>> actualData, Boolean strictMatch) {
        if (strictMatch) {
            assertThat(actualData.size()).as("actual result size does not match expected result size (%d) for query %s", expectations.getRows().size(), query)
                .isEqualTo(expectations.getRows().size());
        }
        compareExpectedRows(expectations, actualData, strictMatch);
    }

    private void compareExpectedRows(ExamplesTable expectations, List<Map<String, Object>> actualData, Boolean strictMatch) {
        if (strictMatch) {
            List<Map<String, String>> expectedData = ExamplesTableUtil.convertTableWithUpperCaseKeys(expectations);
            SoftAssertions softly = new SoftAssertions();
            IntStream.range(0, expectations.getRows().size())
                .forEach(i -> softly
                    .assertThatCode(() -> compareExpectedVersusActualMaps(expectedData.get(i), actualData.get(i), softly))
                    .doesNotThrowAnyException()
                );
            softly.assertAll();
        } else {
            List<Map<String, String>> expectedData = ExamplesTableUtil.convertTable(expectations);
            if (!actualData.containsAll(expectedData)) {
                List<Map<String, String>> notExpectedData = new ArrayList<>(expectedData);
                List<Map<String, String>> convertedActualData = new ArrayList<>();

                actualData
                    .forEach(map ->
                        convertedActualData.add(map
                            .entrySet()
                            .stream()
                            .collect(Collectors.toMap(Map.Entry::getKey,
                                e -> String.valueOf(e.getValue()))))
                    );

                notExpectedData.removeAll(convertedActualData);

                StringBuilder foundInDbDataBuilder = getFoundInDatabaseBuilder(actualData);
                StringBuilder expectedDataBuilder = getExpectedDataBuilder(expectedData, notExpectedData);

                throw new AssertionFailedError("Result set does not contain expected data"
                    + foundInDbDataBuilder.toString()
                    + expectedDataBuilder.toString());
            }
        }
    }

    private StringBuilder getExpectedDataBuilder(List<Map<String, String>> expectedData, List<Map<String, String>> notExpectedData) {
        StringBuilder expectedDataBuilder = new StringBuilder("\nExpected:\n");
        if (!expectedData.isEmpty()) {
            expectedDataBuilder.append("| ");
            for (String header : expectedData.get(0).keySet()) {
                expectedDataBuilder
                    .append(header)
                    .append(" | ");
            }
            for (Map<String, String> row : expectedData) {
                expectedDataBuilder.append("\n| ");
                for (Object value : row.values()) {
                    expectedDataBuilder
                        .append(value.toString())
                        .append(" | ");
                }
                int index = notExpectedData.indexOf(row);
                if (index > -1) {
                    expectedDataBuilder.append("<-- not found in database");
                }
            }
        }
        return expectedDataBuilder;
    }

    private StringBuilder getFoundInDatabaseBuilder(List<Map<String, Object>> actualData) {
        StringBuilder foundInDbDataBuilder = new StringBuilder("\nFound in database:\n");
        if (!actualData.isEmpty()) {
            foundInDbDataBuilder.append("| ");
            for (String header : actualData.get(0).keySet()) {
                foundInDbDataBuilder
                    .append(header)
                    .append(" | ");
            }
            for (Map<String, Object> row : actualData) {
                foundInDbDataBuilder.append("\n| ");
                for (Object value : row.values()) {
                    foundInDbDataBuilder
                        .append(value.toString())
                        .append(" | ");
                }
            }
        }
        return foundInDbDataBuilder;
    }

    @SuppressWarnings("WMI_WRONG_MAP_ITERATOR")
    private void compareExpectedVersusActualMaps(Map<String, String> expectedRow, Map<String, Object> actualRow, SoftAssertions softly) {
        for (String key : expectedRow.keySet()) {
            softly
                .assertThatCode(() -> equalsVerifier.verify(actualRow.get(key), expectedRow.get(key)))
                .doesNotThrowAnyException();
        }
    }

    @AfterScenario
    public void checkSqlException() {
        if (testContext.contains(SQL_EXCEPTION_KEY)) {
            throw (DataAccessException) testContext.remove(SQL_EXCEPTION_KEY);
        }
    }
}
