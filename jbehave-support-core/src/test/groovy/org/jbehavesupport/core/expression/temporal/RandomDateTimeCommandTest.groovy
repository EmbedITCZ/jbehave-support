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
        params || expected
        ["1"]  || IllegalArgumentException.class
    }
}
