package org.jbehavesupport.core.expression


import org.jbehavesupport.core.internal.expression.ConcatCommand
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

@Unroll
class ConcatCommandTest extends Specification {

    def "test execute with #params returns #expected"() {
        when:
        def result = new ConcatCommand().execute(*params)

        then:
        result == expected

        where:
        params                                     || expected
        ["test", "TEST"]                           || "testTEST"
        ["test", "TEST", 1L]                       || "testTEST1"
        ["test", LocalDate.of(2000, 12, 31), 1.54] || "test2000-12-311.54"
    }

    def "test argument exception for params #params"() {
        when:
        new ConcatCommand().execute(*params)

        then:
        Exception exception = thrown()
        expected == exception.class

        where:
        params || expected
        []     || IllegalArgumentException.class
        ["1"]  || IllegalArgumentException.class
    }
}
