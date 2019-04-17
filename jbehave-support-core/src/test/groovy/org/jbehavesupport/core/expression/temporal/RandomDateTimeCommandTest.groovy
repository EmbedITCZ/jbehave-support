package org.jbehavesupport.core.expression.temporal

import org.jbehavesupport.core.internal.expression.temporal.RandomDateTimeCommand
import spock.lang.Specification

import java.time.LocalDateTime

class RandomDateTimeCommandTest extends Specification {

    def "test random date generating"() {
        when:
        def result = new RandomDateTimeCommand().execute()

        then:
        result != null
        result instanceof LocalDateTime
    }

    def "test argument exception"() {
        when:
        new RandomDateTimeCommand().execute(*params)

        then:
        Exception exception = thrown()
        expected == exception.class

        where:
        params           || expected
        ["1", "2", "3"]  || IllegalArgumentException.class
        ["1999", "1988"] || IllegalArgumentException.class
    }

    def "Generated date should be in range"() {
        when:
        LocalDateTime randomDateTime = new RandomDateTimeCommand().execute(*params) as LocalDateTime

        then:
        randomDateTime.isBefore(*before)
        randomDateTime.isAfter(*after)

        where:
        params                   || before                                 | after
        ["1988", "1999"]         || [LocalDateTime.of(1999, 1, 2, 23, 59)] | [LocalDateTime.of(1987, 12, 31, 0, 0)]
        ["1995", "1995"]         || [LocalDateTime.of(1995, 1, 2, 23, 59)] | [LocalDateTime.of(1954, 12, 31, 0, 0)]
        ["1988-6", "1999-7"]     || [LocalDateTime.of(1999, 7, 2, 23, 59)] | [LocalDateTime.of(1988, 5, 31, 0, 0)]
        ["2000-2-2", "2000-2-2"] || [LocalDateTime.of(2000, 2, 3, 23, 59)] | [LocalDateTime.of(2000, 2, 1, 0, 0)]
        ["2000-3-4", "2000-3-5"] || [LocalDateTime.of(2000, 3, 6, 23, 59)] | [LocalDateTime.of(2000, 3, 3, 0, 0)]
    }
}
