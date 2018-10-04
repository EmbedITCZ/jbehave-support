package org.jbehavesupport.core.expression


import org.jbehavesupport.core.internal.expression.RandomDateCommand
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
        params || expected
        ["1"]  || IllegalArgumentException.class
    }
}
