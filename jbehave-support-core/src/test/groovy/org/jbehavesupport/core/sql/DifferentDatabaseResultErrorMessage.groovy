package org.jbehavesupport.core.sql

import org.jbehavesupport.test.support.TestSupport
import spock.lang.Specification

class DifferentDatabaseResultErrorMessage extends Specification implements TestSupport {

    def "differentDatabaseResultPresent"() {
        when:
        def result = run(runWith("sql/DifferentDatabaseResultPresent.story"))

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
        result.failures.stream().anyMatch({e -> e.exception.message.contains("Found in database:")})
        result.failures.stream().anyMatch({e -> e.exception.message.contains("| AGE | FN | LAST_UPDATE | LN | \n")})
        result.failures.stream().anyMatch({e -> e.exception.message.contains("| 31 | John | 2018-06-18 | Doe | \n")})
        result.failures.stream().anyMatch({e -> e.exception.message.contains("| 29 | Jane | 2018-06-18 | Doe | \n")})
        result.failures.stream().anyMatch({e -> e.exception.message.contains("| null | Michael | null | Doe | \n")})

    }

    def "differentDatabaseResultMatch"() {
        when:
        def result = run(runWith("sql/DifferentDatabaseResultMatch.story"))

        then:
        result.getTotalFailureCount() == 1
        result.getFailures().get(0).exception.getMessage().contains("[row [0], column [AGE]]")
        result.getFailures().get(0).exception.getMessage().contains("value '31' is not equal to '1'")
        result.getFailures().get(0).exception.getMessage().contains("[row [0], column [FN]]")
        result.getFailures().get(0).exception.getMessage().contains("value 'John' is not equal to 'Dummy'")
        result.getFailures().get(0).exception.getMessage().contains("[row [0], column [LAST_UPDATE]]")
        result.getFailures().get(0).exception.getMessage().contains("value '2018-06-18' is not equal to '2001-06-18")
        result.getFailures().get(0).exception.getMessage().contains("[row [0], column [LN]]")
        result.getFailures().get(0).exception.getMessage().contains("value 'Doe' is not equal to 'Does't'")
        result.getFailures().get(0).exception.getMessage().contains("[row [2], column [FN]]")
        result.getFailures().get(0).exception.getMessage().contains("value 'Michael' is not equal to 'null'")
        result.getFailures().get(0).exception.getMessage().contains("[row [2], column [LN]]")
        result.getFailures().get(0).exception.getMessage().contains("value 'Doe' is not equal to 'null'")

    }

}
