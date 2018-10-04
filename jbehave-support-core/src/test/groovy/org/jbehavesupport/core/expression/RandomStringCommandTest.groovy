package org.jbehavesupport.core.expression


import org.jbehavesupport.core.internal.expression.RandomStringCommand
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class RandomStringCommandTest extends Specification {

    def "test random string generating with length #params"() {
        when:
        def result = new RandomStringCommand().execute(*params)

        then:
        result.length() == expected

        where:
        params    || expected
        ["5"]     || 5
        [5]       || 5
        ["10000"] || 10000
        [10000]   || 10000

    }

    def "test argument exception #expected"() {
        when:
        new RandomStringCommand().execute(*params)

        then:
        Exception exception = thrown()
        expected == exception.class

        where:
        params     || expected
        ["1", "4"] || IllegalArgumentException.class
    }
}
