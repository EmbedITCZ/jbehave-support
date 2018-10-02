package org.jbehavesupport.test.issue

import org.jbehavesupport.test.support.TestSupport
import org.junit.runner.JUnitCore
import org.junit.runner.Result
import spock.lang.Issue
import spock.lang.Shared
import spock.lang.Specification

class CheckSqlError extends Specification implements TestSupport {

    @Shared
        runner = new JUnitCore()

    def "check sql error for step: these columns from the single-row query result are saved:\$storedData"() {
        when:
        def result = runner.run(runWith("issue/CheckSqlErrorForSingleRowSaved.story"))

        then:
        result.failures.stream().anyMatch({e -> e.exception.message.contains("Table \"PERSON_\" not found")})
    }

    def "check sql error for step: these columns from the multi-row query result are saved:\$storedData"() {
        when:
        def result = runner.run(runWith("issue/CheckSqlErrorForMultiRowSaved.story"))

        then:
        result.failures.stream().anyMatch({e -> e.exception.message.contains("Table \"PERSON_\" not found")})
    }

    def "check sql error for step: these columns from the query result are equal:\$columnsToCompare"() {
        when:
        def result = runner.run(runWith("issue/CheckSqlErrorForColumnsFromQueryAreEqual.story"))

        then:
        result.failures.stream().anyMatch({e -> e.exception.message.contains("Table \"PERSON_\" not found")})
    }

    def "check sql error for step: these rows match the query result:\$matchingData"() {
        when:
        def result = runner.run(runWith("issue/CheckSqlErrorForRowsMatchQueryResult.story"))

        then:
        result.failures.stream().anyMatch({e -> e.exception.message.contains("Table \"PERSON_\" not found")})
    }

    def "check sql error for step: these rows are present in the query result:\$presentData"() {
        when:
        def result = runner.run(runWith("issue/CheckSqlErrorForRowsArePresentInQueryResult.story"))

        then:
        result.failures.stream().anyMatch({e -> e.exception.message.contains("Table \"PERSON_\" not found")})
    }

    def "check sql error for step: the result set has \$rowCount row(s)"() {
        when:
        def result = runner.run(runWith("issue/CheckSqlResultHasCountRow.story"))

        then:
        'assert error message'(result)
    }

    def "this query is performed on [\$databaseId]:\$sqlStatement"() {
        when:
        def result = runner.run(runWith("issue/CheckSqlErrorForQueryIsPerformed.story"))

        then:
        'assert error message'(result)
    }

    def "this update is performed on [\$databaseId]:\$sqlStatement"() {
        when:
        def result = runner.run(runWith("issue/CheckSqlErrorForQueryIsPerformed.story"))

        then:
        'assert error message'(result)
    }

    private boolean 'assert error message'(Result result) {
        result.failures.stream().anyMatch({ e -> e.exception.message.contains("Table \"PERSON_\" not found") })
    }

    def "check sql error for step: after scenario"() {
        when:
        def result = runner.run(runWith("issue/CheckSqlErrorAfterScenario.story"))

        then:
        result.failures.stream().anyMatch({e -> e.exception.message.contains("Table \"PERSON_\" not found")})
    }
}
