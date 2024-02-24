package org.jbehavesupport.core.sql

import org.jbehavesupport.test.support.TestSupport
import org.junit.platform.launcher.listeners.TestExecutionSummary
import spock.lang.Specification

class CheckSqlError extends Specification implements TestSupport {

    def "check sql error for step: these columns from the single-row query result are saved:\$storedData"() {
        when:
        def result = run(runWith("sql/CheckSqlErrorForSingleRowSaved.story"))

        then:
        result.failures.stream().anyMatch({ e -> e.exception.cause.message.contains("Table \"PERSON_\" not found") })
    }

    def "check sql error for step: these columns from the multi-row query result are saved:\$storedData"() {
        when:
        def result = run(runWith("sql/CheckSqlErrorForMultiRowSaved.story"))

        then:
        result.failures.stream().anyMatch({ e -> e.exception.cause.message.contains("Table \"PERSON_\" not found") })
    }

    def "check sql error for step: these columns from the query result are equal:\$columnsToCompare"() {
        when:
        def result = run(runWith("sql/CheckSqlErrorForColumnsFromQueryAreEqual.story"))

        then:
        result.failures.stream().anyMatch({ e -> e.exception.cause.message.contains("Table \"PERSON_\" not found") })
    }

    def "check sql error for step: these rows match the query result:\$matchingData"() {
        when:
        def result = run(runWith("sql/CheckSqlErrorForRowsMatchQueryResult.story"))

        then:
        result.failures.stream().anyMatch({ e -> e.exception.cause.message.contains("Table \"PERSON_\" not found") })
    }

    def "check sql error for step: these rows are present in the query result:\$presentData"() {
        when:
        def result = run(runWith("sql/CheckSqlErrorForRowsArePresentInQueryResult.story"))

        then:
        result.failures.stream().anyMatch({ e -> e.exception.cause.message.contains("Table \"PERSON_\" not found") })
    }

    def "check sql error for step: the result set has \$rowCount row(s)"() {
        when:
        def result = run(runWith("sql/CheckSqlResultHasCountRow.story"))

        then:
        'assert error message'(result)
    }

    def "this query is performed on [\$databaseId]:\$sqlStatement"() {
        when:
        def result = run(runWith("sql/CheckSqlErrorForQueryIsPerformed.story"))

        then:
        'assert error message'(result)
    }

    def "this update is performed on [\$databaseId]:\$sqlStatement"() {
        when:
        def result = run(runWith("sql/CheckSqlErrorForQueryIsPerformed.story"))

        then:
        'assert error message'(result)
    }

    private boolean 'assert error message'(TestExecutionSummary result) {
        result.failures.stream().anyMatch({ e -> e.exception.cause.message.contains("Table \"PERSON_\" not found") })
    }

    def "check sql error for step: after scenario"() {
        when:
        def result = run(runWith("sql/CheckSqlErrorAfterScenario.story"))

        then:
        result.failures.stream().anyMatch({ e -> e.exception.cause.message.contains("Table \"PERSON_\" not found") })
    }
}
