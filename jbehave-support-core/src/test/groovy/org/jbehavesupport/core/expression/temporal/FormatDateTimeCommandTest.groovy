package org.jbehavesupport.core.expression.temporal


import org.jbehavesupport.core.internal.expression.temporal.FormatDateTimeCommand
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime

@Unroll
class FormatDateTimeCommandTest extends Specification {
    def "test execute with #expression returns #expected"() {
        when:
        def result = new FormatDateTimeCommand().execute(*expression)

        then:
        result == expected

        where:
        expression                   || expected
        ["2031-05-20T10:15:30", "MM/dd/yyyy HH.mm.ss"] || "05/20/2031 10.15.30"
    }

    def "test execute with #expression throws #exception"() {
        when:
        new FormatDateTimeCommand().execute(*expression)

        then:
        thrown(exception)

        where:
        expression                          || exception
        ["1", "2", "3"]                     || IllegalArgumentException
        [LocalDateTime.now(), "MM/dd/yyyy"] || IllegalArgumentException
    }
}
