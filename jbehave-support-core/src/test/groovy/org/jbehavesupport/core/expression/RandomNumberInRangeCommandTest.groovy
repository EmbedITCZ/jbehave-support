package org.jbehavesupport.core.expression


import org.jbehavesupport.core.internal.expression.RandomNumberInRangeCommand
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class RandomNumberInRangeCommandTest extends Specification {

    def "test random number generating in range #params"() {
        when:
        def result = new RandomNumberInRangeCommand().execute(*params)

        then:
        result >= expectedMin && result <= expectedMax

        where:
        params          || expectedMin || expectedMax
        ["5", "5"]      || 5           || 5
        [5, 5]          || 5           || 5
        ["10", "10000"] || 10          || 10000
        [10, 10000]     || 10          || 10000

    }

    def "test argument exception #expected"() {
        when:
        new RandomNumberInRangeCommand().execute(*params)

        then:
        Exception exception = thrown()
        expected == exception.class

        where:
        params           || expected
        ["1", "4", "10"] || IllegalArgumentException.class
    }
}
