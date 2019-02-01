package org.jbehavesupport.core.expression.temporal


import org.jbehavesupport.core.internal.expression.temporal.DateParseCommand
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

@Unroll
class DateParseCommandTest extends Specification {
    def "test execute with #expression returns #expected"() {
        when:
        def result = new DateParseCommand().execute(*expression)

        then:
        result == expected

        where:
        expression                   || expected
        ["05/20/2031", "MM/dd/yyyy"] || LocalDate.of(2031, 05, 20)
    }

    def "test execute with #expression throws #exception"() {
        when:
        new DateParseCommand().execute(*expression)

        then:
        thrown(exception)

        where:
        expression                      || exception
        ["1", "2", "3"]                 || IllegalArgumentException
        [LocalDate.now(), "MM/dd/yyyy"] || IllegalArgumentException
    }

}
