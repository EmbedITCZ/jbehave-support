package org.jbehavesupport.core.expression.numeric

import org.jbehavesupport.core.internal.expression.numeric.MultiplyCommand
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class MultiplyCommandTest extends Specification {

    def "Multiply command evaluation for #params wit result #expected"() {
        when:
        def actual = new MultiplyCommand().execute(*params)
        then:
        expected == actual
        where:
        params               || expected
        ["1", "1"]           || new BigDecimal("1")
        [2, 2]               || new BigDecimal("4")
        ["1.1", "1.5"]       || new BigDecimal("1.65")
        [-2.6, -2.2]         || new BigDecimal("5.72")
        ["1.1", "-1.1", "1"] || new BigDecimal("-1.21")
        [6.2D, 2.2F, 2L]     || new BigDecimal("27.28")
    }

    @Unroll
    def "Test wrong number of params for MultiplyCommand"() {
        when:
        new MultiplyCommand().execute([1])

        then:
        Exception exception = thrown()
        IllegalArgumentException.class == exception.class
        "At least two parameters were expected" == exception.message
    }
}
