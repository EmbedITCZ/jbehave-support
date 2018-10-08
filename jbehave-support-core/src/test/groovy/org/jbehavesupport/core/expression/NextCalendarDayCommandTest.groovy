package org.jbehavesupport.core.expression


import org.jbehavesupport.core.internal.expression.NextCalendarDayCommand
import org.jbehavesupport.core.support.TimeFacade
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate
import java.time.ZoneId

@Unroll
class NextCalendarDayCommandTest extends Specification {

    def toInstant = { year, month, dayOfMonth -> new LocalDate(year, month, dayOfMonth).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant() }
    def monthWith31daysTimeFacade = [getCurrentInstant: { -> toInstant(2005, 5, 20) }] as TimeFacade
    def monthWith30daysTimeFacade = [getCurrentInstant: { -> toInstant(2005, 4, 20) }] as TimeFacade
    def monthWith28daysTimeFacade = [getCurrentInstant: { -> toInstant(2005, 2, 20) }] as TimeFacade

    def "for a 31 days long month test execute with #expression returns #expected"() {
        when:
        def nextCalendarDayCommand = new NextCalendarDayCommand(monthWith31daysTimeFacade)
        def result = nextCalendarDayCommand.execute(*expression)

        then:
        result == expected

        where:
        expression || expected
        ["1"]      || LocalDate.of(2005, 06, 01)
        ["19"]     || LocalDate.of(2005, 06, 19)
        ["20"]     || LocalDate.of(2005, 05, 20)
        ["21"]     || LocalDate.of(2005, 05, 21)
        ["31"]     || LocalDate.of(2005, 05, 31)
    }

    def "for a 30 days long month test execute with #expression returns #expected"() {
        when:
        def nextCalendarDayCommand = new NextCalendarDayCommand(monthWith30daysTimeFacade)
        def result = nextCalendarDayCommand.execute(*expression)

        then:
        result == expected

        where:
        expression || expected
        ["1"]      || LocalDate.of(2005, 05, 01)
        ["19"]     || LocalDate.of(2005, 05, 19)
        ["20"]     || LocalDate.of(2005, 04, 20)
        ["21"]     || LocalDate.of(2005, 04, 21)
        ["31"]     || LocalDate.of(2005, 05, 01)
    }

    def "for a 28 days long month test execute with #expression returns #expected"() {
        when:
        def nextCalendarDayCommand = new NextCalendarDayCommand(monthWith28daysTimeFacade)
        def result = nextCalendarDayCommand.execute(*expression)

        then:
        result == expected

        where:
        expression || expected
        ["1"]      || LocalDate.of(2005, 03, 01)
        ["19"]     || LocalDate.of(2005, 03, 19)
        ["20"]     || LocalDate.of(2005, 02, 20)
        ["21"]     || LocalDate.of(2005, 02, 21)
        ["28"]     || LocalDate.of(2005, 02, 28)
        ["29"]     || LocalDate.of(2005, 03, 01)
        ["31"]     || LocalDate.of(2005, 03, 01)
    }

    def "test execute with #expression throws #exception"() {
        when:
        def nextCalendarDayCommand = new NextCalendarDayCommand(monthWith31daysTimeFacade)
        nextCalendarDayCommand.execute(*expression)

        then:
        thrown(exception)

        where:
        expression || exception
        ["1", "2"] || IllegalArgumentException
        ["0"]      || IllegalArgumentException
        ["32"]     || IllegalArgumentException
    }

}
