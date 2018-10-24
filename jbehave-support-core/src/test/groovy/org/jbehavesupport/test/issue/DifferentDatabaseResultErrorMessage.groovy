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
        result.failures.stream().anyMatch({ e -> e.exception.message.contains("| 2018-06-18 | Doe | JANE | 29 | <-- not found in database") })
        result.failures.stream().anyMatch({ e -> e.exception.message.contains("| 2018-06-18 | doe | Jane | 29 | <-- not found in database") })
        result.failures.stream().anyMatch({ e -> e.exception.message.contains("| 2018-06-18 | Doe | john | 31 | <-- not found in database") })
        result.failures.stream().anyMatch({ e -> e.exception.message.contains("| 2018-06-17 | Doe | John | 31 | <-- not found in database") })
        result.failures.stream().anyMatch({ e -> e.exception.message.contains("| 18/06/2018 | Doe | John | 31 | <-- not found in database") })
        result.failures.stream().noneMatch({ e -> e.exception.message.contains("| 2018-06-18 | Doe | John | 31 | <-- not found in database") })
        result.failures.stream().noneMatch({ e -> e.exception.message.contains("| 2018-06-18 | Doe | Jane | 29 | <-- not found in database") })
        result.failures.stream().noneMatch({ e -> e.exception.message.contains("| null | Doe | Michael | null | <-- not found in database") })

    }

}
