package org.jbehavesupport.core.expression


import org.jbehavesupport.core.internal.expression.FormatDateCommand
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

@Unroll
class FormatDateCommandTest extends Specification {
    def "test execute with #expression returns #expected"() {
        when:
        def result = new FormatDateCommand().execute(*expression)

        then:
        result == expected

        where:
        expression                      || expected
        ["2031-05-20", "MM/dd/yyyy"]    || "05/20/2031"
    }

    def "test execute with #expression throws #exception"() {
        when:
        new FormatDateCommand().execute(*expression)

        then:
        thrown(exception)

        where:
        expression                      || exception
        ["1", "2", "3"]                 || IllegalArgumentException
        [LocalDate.now(), "MM/dd/yyyy"] || IllegalArgumentException
    }
}
