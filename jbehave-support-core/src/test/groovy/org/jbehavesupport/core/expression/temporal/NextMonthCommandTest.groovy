package org.jbehavesupport.core.expression.temporal


import org.jbehavesupport.core.internal.expression.temporal.NextMonthCommand
import org.jbehavesupport.core.support.TimeFacade
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeParseException

@Unroll
class NextMonthCommandTest extends Specification {

    def nextMonthCommand = new NextMonthCommand(TimeFacade.getDefault())

    def "test execute with #expression returns #expected"() {
        when:
        def result = nextMonthCommand.execute(*expression)

        then:
        result == expected

        where:
        expression || expected
        []         || LocalDate.now().plusMonths(1).withDayOfMonth(1)
        ["11"]     || LocalDate.now().plusMonths(1).withDayOfMonth(11)
        ["P2M3D"]  || LocalDate.now().plusMonths(1).withDayOfMonth(1).plus(Period.parse("P2M3D"))
    }

    def "test execute with #expression throws #exception"() {
        when:
        nextMonthCommand.execute(*expression)

        then:
        thrown(exception)

        where:
        expression || exception
        ["1", "2"] || IllegalArgumentException
        ["123foo"] || NumberFormatException
        ["P123"]   || DateTimeParseException
    }

}
