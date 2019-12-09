package org.jbehavesupport.test.issue

import org.jbehavesupport.test.support.TestSupport
import org.junit.runner.JUnitCore
import spock.lang.Issue
import spock.lang.Shared
import spock.lang.Specification

class DifferentDatabaseResultErrorMessage extends Specification implements TestSupport {
    @Shared
        runner = new JUnitCore()

    def "differentDatabaseResult"() {
        when:
        def result = runner.run(runWith("issue/DifferentDatabaseResult.story"))

        then:
        result.failures.stream().anyMatch({ e -> e.exception.message.contains("Result set does not contain expected data") })
        result.failures.stream().anyMatch({ e -> e.exception.message.contains("| 29 | JANE | 2018-06-18 | Doe | <-- not found in database") })
        result.failures.stream().anyMatch({ e -> e.exception.message.contains("| 29 | Jane | 2018-06-18 | doe | <-- not found in database") })
        result.failures.stream().anyMatch({ e -> e.exception.message.contains("| 31 | john | 2018-06-18 | Doe | <-- not found in database") })
        result.failures.stream().anyMatch({ e -> e.exception.message.contains("| 31 | John | 2018-06-17 | Doe | <-- not found in database") })
        result.failures.stream().anyMatch({ e -> e.exception.message.contains("| 31 | John | 18/06/2018 | Doe | <-- not found in database") })
        result.failures.stream().noneMatch({ e -> e.exception.message.contains("| 31 | John | 2018-06-18 | Doe | <-- not found in database") })
        result.failures.stream().noneMatch({ e -> e.exception.message.contains("| 29 | Jane | 2018-06-18 | Doe | <-- not found in database") })
        result.failures.stream().noneMatch({ e -> e.exception.message.contains("| null | Michael | null | Doe | <-- not found in database") })

    }

}
