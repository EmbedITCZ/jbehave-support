package org.jbehavesupport.core.sql;

import static org.jbehavesupport.core.sql.SqlSteps.ExceptionHandling.CATCH_EXCEPTION;
import static org.jbehavesupport.core.sql.SqlSteps.ExceptionHandling.THROW_EXCEPTION;
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

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.expression.ExpressionEvaluatingParameter;
import org.jbehavesupport.core.internal.sql.InterceptingNamedParameterJdbcTemplate;
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
import org.jbehavesupport.core.report.extension.SqlXmlReporterExtension;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;

@Component
@RequiredArgsConstructor
public final class SqlSteps {

    private static final String SQL_RESULT_KEY = "sql_result";
    private static final String SQL_QUERY_KEY = "sql_query";
    private static final String MISSING_SQL_MESSAGE = "sql query must be run and saved in context prior to this step";
    private static final String SQL_EXCEPTION_KEY = "sql_exception";

    private final ConfigurableListableBeanFactory beanFactory;

    private final TestContext testContext;

    private final EqualsVerifier equalsVerifier;

    private final ContainsVerifier containsVerifier;

    @Autowired(required = false)
    private SqlXmlReporterExtension sqlXmlReporterExtension;

    enum ExceptionHandling {
        THROW_EXCEPTION,
        CATCH_EXCEPTION
    }

    @Given("this query is performed on [$databaseId]:$sqlStatement")
    @When("this query is performed on [$databaseId]:$sqlStatement")
    public void executeQuery(String databaseId, ExpressionEvaluatingParameter<String> sqlStatement) {
        checkSqlException();
        executeQuery(databaseId, sqlStatement, new ExamplesTable(""), THROW_EXCEPTION);
    }

    @Given("this query with expected exception is performed on [$databaseId]:$sqlStatement")
    @When("this query with expected exception is performed on [$databaseId]:$sqlStatement")
    public void executeQueryCatchException(String databaseId, ExpressionEvaluatingParameter<String> sqlStatement) {
        checkSqlException();
        executeQuery(databaseId, sqlStatement, new ExamplesTable(""), CATCH_EXCEPTION);
    }

    @Given(value = "this query is performed on [$databaseId]:$sqlStatement with parameters:$parameters", priority = 100)
    @When(value = "this query is performed on [$databaseId]:$sqlStatement with parameters:$parameters", priority = 100)
    public void executeQuery(String databaseId, ExpressionEvaluatingParameter<String> sqlStatement, ExamplesTable parameters) {
        executeQuery(databaseId, sqlStatement, parameters, THROW_EXCEPTION);
    }

    @Given(value = "this query with expected exception is performed on [$databaseId]:$sqlStatement with parameters:$parameters", priority = 100)
    @When(value = "this query with expected exception is performed on [$databaseId]:$sqlStatement with parameters:$parameters", priority = 100)
    public void executeQueryCatchException(String databaseId, ExpressionEvaluatingParameter<String> sqlStatement, ExamplesTable parameters) {
        executeQuery(databaseId, sqlStatement, parameters, CATCH_EXCEPTION);
    }

    private void executeQuery(String databaseId, ExpressionEvaluatingParameter<String> sqlStatement, ExamplesTable parameters, ExceptionHandling exceptionHandling) {
        checkSqlException();
        putDataIntoContext(testContext, parameters, ExampleTableConstraints.ALIAS, ExampleTableConstraints.DATA);
        String resolvedStatement = sqlStatement.getValue();

        List<Map<String, Object>> result;

        try {
            result = resolveJdbcTemplate(databaseId)
                .queryForList(resolvedStatement, ExamplesTableUtil.convertMap(parameters, ExampleTableConstraints.NAME, ExampleTableConstraints.DATA));
            testContext.put(SQL_RESULT_KEY, result);
            testContext.put(SQL_QUERY_KEY, resolvedStatement);
        } catch (DataAccessException e) {
            testContext.put(SQL_EXCEPTION_KEY, e);
            if (exceptionHandling != CATCH_EXCEPTION) {
                throw e;
            }
        }
    }

    @Given("this update is performed on [$databaseId]:$sqlStatement")
    @When("this update is performed on [$databaseId]:$sqlStatement")
    public void executeUpdate(String databaseId, ExpressionEvaluatingParameter<String> sqlStatement) {
        checkSqlException();
        executeUpdate(databaseId, sqlStatement, new ExamplesTable(""), THROW_EXCEPTION);
    }

    @Given("this update with expected exception is performed on [$databaseId]:$sqlStatement")
    @When("this update with expected exception is performed on [$databaseId]:$sqlStatement")
    public void executeUpdateCatchException(String databaseId, ExpressionEvaluatingParameter<String> sqlStatement) {
        checkSqlException();
        executeUpdate(databaseId, sqlStatement, new ExamplesTable(""), CATCH_EXCEPTION);
    }

    @Given(value = "this update is performed on [$databaseId]:$sqlStatement with parameters:$parameters", priority = 100)
    @When(value = "this update is performed on [$databaseId]:$sqlStatement with parameters:$parameters", priority = 100)
    public void executeUpdate(String databaseId, ExpressionEvaluatingParameter<String> sqlStatement, ExamplesTable parameters) {
        executeUpdate(databaseId, sqlStatement, parameters, THROW_EXCEPTION);
    }

    @Given(value = "this update with expected exception is performed on [$databaseId]:$sqlStatement with parameters:$parameters", priority = 100)
    @When(value = "this update with expected exception is performed on [$databaseId]:$sqlStatement with parameters:$parameters", priority = 100)
    public void executeUpdateCatchException(String databaseId, ExpressionEvaluatingParameter<String> sqlStatement, ExamplesTable parameters) {
        executeUpdate(databaseId, sqlStatement, parameters, CATCH_EXCEPTION);
    }

    private void executeUpdate(String databaseId, ExpressionEvaluatingParameter<String> sqlStatement, ExamplesTable parameters, ExceptionHandling exceptionHandling) {
        checkSqlException();
        putDataIntoContext(testContext, parameters, ExampleTableConstraints.ALIAS, ExampleTableConstraints.DATA);
        String resolvedStatement = sqlStatement.getValue();
        try {
            resolveJdbcTemplate(databaseId).update(resolvedStatement, ExamplesTableUtil
                .convertMap(parameters, ExampleTableConstraints.NAME, ExampleTableConstraints.DATA));
        } catch (DataAccessException e) {
            testContext.put(SQL_EXCEPTION_KEY, e);
            if (exceptionHandling != CATCH_EXCEPTION) {
                throw e;
            }
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

    private String getTestContextAliasOrFieldName(Map<String, String> row) {
        return row.containsKey(ExampleTableConstraints.ALIAS) ? row.get(ExampleTableConstraints.ALIAS) : row.get(ExampleTableConstraints.NAME);
    }

    private void checkColumnPresentInResultSet(Map<String, Object> resultRow, Map<String, String> row) {
        if (!resultRow.containsKey(row.get(ExampleTableConstraints.NAME).toUpperCase())) {
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

    private void compareExampleTableVersusListOfMaps(ExamplesTable compareData, boolean strictMatch) {
        notNull(compareData, "matching data can't be null");

        ArrayList<Map<String, Object>> queryResultsToMatch = new ArrayList<>();
        ArrayList<String> upperCaseHeaders = compareData.getHeaders().stream()
            .map(String::toUpperCase)
            .collect(Collectors.toCollection(ArrayList::new));

        for (Map<String, Object> originalRow : getSqlResult()) {
            // do not manipulate original row in case of additional saving/verification
            LinkedCaseInsensitiveMap<Object> row = copyResultRow(originalRow);
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

    private InterceptingNamedParameterJdbcTemplate resolveJdbcTemplate(String databaseId) {
        try {
            DataSource dataSource = BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, DataSource.class, databaseId);
            return new InterceptingNamedParameterJdbcTemplate(dataSource, sqlXmlReporterExtension);
        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalArgumentException("SqlSteps requires single DataSource bean with qualifier [" + databaseId + "]", e);
        }
    }

    private void compareExampleTableVersusListOfMaps(String query, ExamplesTable expectations, List<Map<String, Object>> actualData, boolean strictMatch) {
        if (strictMatch) {
            assertThat(actualData.size()).as("actual result size does not match expected result size (%d) for query %s", expectations.getRows().size(), query)
                .isEqualTo(expectations.getRows().size());
        }
        compareExpectedRows(expectations, actualData, strictMatch);
    }

    private void compareExpectedRows(ExamplesTable expectations, List<Map<String, Object>> actualData, boolean strictMatch) {
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
            List<Map<String, String>> foundData = new ArrayList<>();
            expectedData.forEach(expectedRow ->
                actualData.stream().filter(actualRow ->
                    verifyRowsEquality(foundData, expectedRow, actualRow)).findFirst()
            );

            List<Map<String, String>> notFoundData = new ArrayList<>(expectedData);
            notFoundData.removeAll(foundData);

            if (!notFoundData.isEmpty()) {
                StringBuilder foundInDbDataBuilder = getFoundInDatabaseBuilder(actualData);
                StringBuilder expectedDataBuilder = getExpectedDataBuilder(expectedData, notFoundData);

                throw new AssertionFailedError("Result set does not contain expected data"
                    + foundInDbDataBuilder.toString()
                    + expectedDataBuilder.toString());
            }
        }
    }

    private boolean verifyRowsEquality(List<Map<String, String>> foundData, Map<String, String> expectedRow, Map<String, Object> actualRow) {
        try {
            expectedRow.entrySet().forEach(entry ->
                equalsVerifier.verify(actualRow.get(entry.getKey()), entry.getValue())
            );
        } catch (AssertionError | IllegalArgumentException e) {
            return false;
        }
        foundData.add(expectedRow);
        return true;
    }

    private StringBuilder getExpectedDataBuilder(List<Map<String, String>> expectedData, List<Map<String, String>> notExpectedData) {
        StringBuilder expectedDataBuilder = new StringBuilder("\nExpected:\n");
        if (!expectedData.isEmpty()) {
            List<String> sortedHeaders = expectedData.get(0).keySet().stream()
                .sorted()
                .collect(Collectors.toList());

            expectedDataBuilder.append("| ");
            for (String header : sortedHeaders) {
                expectedDataBuilder
                    .append(header)
                    .append(" | ");
            }
            for (Map<String, String> row : expectedData) {
                expectedDataBuilder.append("\n| ");
                for (String header : sortedHeaders) {
                    expectedDataBuilder
                        .append(row.get(header))
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
            List<String> sortedHeaders = actualData.get(0).keySet().stream()
                .sorted()
                .collect(Collectors.toList());

            foundInDbDataBuilder.append("| ");
            for (String header : sortedHeaders) {
                foundInDbDataBuilder
                    .append(header)
                    .append(" | ");
            }
            for (Map<String, Object> row : actualData) {
                foundInDbDataBuilder.append("\n| ");
                for (String header : sortedHeaders) {
                    foundInDbDataBuilder
                        .append(row.get(header))
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

    private LinkedCaseInsensitiveMap<Object> copyResultRow(Map<String, Object> originalRow) {
        LinkedCaseInsensitiveMap<Object> row = new LinkedCaseInsensitiveMap<>(originalRow.size());
        row.putAll(originalRow);
        return row;
    }

    @AfterScenario
    public void checkSqlException() {
        if (testContext.contains(SQL_EXCEPTION_KEY)) {
            throw (DataAccessException) testContext.remove(SQL_EXCEPTION_KEY);
        }
    }
}
