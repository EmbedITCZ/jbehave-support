package org.jbehavesupport.core.expression.temporal


import org.jbehavesupport.core.internal.expression.temporal.CurrentDateCommand
import org.jbehavesupport.core.support.TimeFacade
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeParseException

@Unroll
class CurrentDateCommandTest extends Specification {

    def currentDateCommand = new CurrentDateCommand(TimeFacade.getDefault())

    def "test execute with #expression returns #expected"() {
        when:
        def result = currentDateCommand.execute(*expression)

        then:
        result == expected

        where:
        expression || expected
        []         || LocalDate.now()
        ["0"]      || LocalDate.now()
        [0]        || LocalDate.now()
        ["99"]     || LocalDate.now().plusDays(99)
        [99]       || LocalDate.now().plusDays(99)
        ["-20"]    || LocalDate.now().minusDays(20)
        [-20]      || LocalDate.now().minusDays(20)
        ["P2M3D"]  || LocalDate.now().plus(Period.parse("P2M3D"))
    }

    def "test execute with #expression throws #exception"() {
        when:
        currentDateCommand.execute(*expression)

        then:
        thrown(exception)

        where:
        expression || exception
        ["1", "2"] || IllegalArgumentException
        ["123foo"] || NumberFormatException
        ["P123"]   || DateTimeParseException
    }

}
