package org.jbehavesupport.core.expression


import org.jbehavesupport.core.internal.expression.RandomNumberCommand
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class RandomNumberCommandTest extends Specification {

    def "test random number generating with length #params"() {
        when:
        def result = new RandomNumberCommand().execute(*params)

        then:
        result.toString().length() == expected

        where:
        params || expected
        ["5"]  || 5
        [5]    || 5
        ["10"] || 10
        [10]   || 10
        [20]   || 20
        [30]   || 30

    }

    def "test argument exception"() {
        when:
        new RandomNumberCommand().execute(*params)

        then:
        Exception exception = thrown()
        expected == exception.class

        where:
        params      || expected
        ["1", "4"]  || IllegalArgumentException.class
    }
}
