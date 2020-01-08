package org.jbehavesupport.core.expression.temporal

import org.jbehavesupport.core.internal.expression.temporal.CurrentDateTimeCommand
import org.jbehavesupport.core.support.TimeFacade
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

@Unroll
class CurrentDateTimeCommandTest extends Specification {

    def currentDateTimeCommand = new CurrentDateTimeCommand(TimeFacade.getDefault())

    def "test execute with #expression returns #expected"() {
        when:
        def result = currentDateTimeCommand.execute(*expression)

        then:
        ChronoUnit.SECONDS.between(expected, result) < 1

        where:
        expression || expected
        []         || LocalDateTime.now()
        ["0"]      || LocalDateTime.now()
        [0]        || LocalDateTime.now()
        ["99"]     || LocalDateTime.now().plusSeconds(99)
        [99]       || LocalDateTime.now().plusSeconds(99)
        ["-20"]    || LocalDateTime.now().minusSeconds(20)
        [-20]      || LocalDateTime.now().minusSeconds(20)
        ["P2M3D"]  || LocalDateTime.now() + Period.parse("P2M3D")
    }

    def "test execute with #expression throws #exception"() {
        when:
        currentDateTimeCommand.execute(*expression)

        then:
        thrown(exception)

        where:
        expression || exception
        ["1", "2"] || IllegalArgumentException
        ["123foo"] || NumberFormatException
        ["P123"]   || DateTimeParseException
    }

}
