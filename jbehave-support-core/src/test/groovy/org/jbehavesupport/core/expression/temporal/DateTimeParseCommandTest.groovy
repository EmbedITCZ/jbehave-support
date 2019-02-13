package org.jbehavesupport.core.expression.temporal


import org.jbehavesupport.core.internal.expression.temporal.DateTimeParseCommand
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime

@Unroll
class DateTimeParseCommandTest extends Specification {
    def "test execute with #expression returns #expected"() {
        when:
        def result = new DateTimeParseCommand().execute(*expression)

        then:
        result == expected

        where:
        expression                   || expected
        ["10:15:30 05/20/2031", "HH:mm:ss MM/dd/yyyy"] || LocalDateTime.of(2031, 05, 20, 10, 15, 30)
    }

    def "test execute with #expression throws #exception"() {
        when:
        new DateTimeParseCommand().execute(*expression)

        then:
        thrown(exception)

        where:
        expression                          || exception
        ["1", "2", "3"]                     || IllegalArgumentException
        [LocalDateTime.now(), "MM/dd/yyyy"] || IllegalArgumentException
    }

}
