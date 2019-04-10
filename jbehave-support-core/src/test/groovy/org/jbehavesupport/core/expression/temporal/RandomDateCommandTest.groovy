package org.jbehavesupport.core.expression.temporal


import org.jbehavesupport.core.internal.expression.temporal.RandomDateCommand
import spock.lang.Specification

import java.time.LocalDate

class RandomDateCommandTest extends Specification {

    def "test random date generating"() {
        when:
        def result = new RandomDateCommand().execute()

        then:
        result != null
        result instanceof LocalDate
    }

    def "test argument exception"() {
        when:
        new RandomDateCommand().execute(*params)

        then:
        Exception exception = thrown()
        expected == exception.class

        where:
        params           || expected
        ["a", "b", "c"]  || IllegalArgumentException.class
        ["1999", "1988"] || IllegalArgumentException.class
    }

    def "Generated date should be in range"() {
        when:
        LocalDate randomDate = new RandomDateCommand().execute(*params) as LocalDate

        then:
        randomDate.isBefore(*before)
        randomDate.isAfter(*after)

        where:
        params                   || before                     | after
        ["1988", "1999"]         || [LocalDate.of(1999, 1, 2)] | [LocalDate.of(1987, 12, 31)]
        ["1995", "1995"]         || [LocalDate.of(1995, 1, 2)] | [LocalDate.of(1954, 12, 31)]
        ["1988-6", "1999-7"]     || [LocalDate.of(1999, 7, 2)] | [LocalDate.of(1988, 5, 31)]
        ["2000-2-2", "2000-2-2"] || [LocalDate.of(2000, 2, 3)] | [LocalDate.of(2000, 2, 1)]
        ["2000-3-4", "2000-3-5"] || [LocalDate.of(2000, 3, 6)] | [LocalDate.of(2000, 3, 3)]
    }
}
