package org.jbehavesupport.core.expression.numeric

import org.jbehavesupport.core.internal.expression.numeric.MinusCommand
import org.springframework.core.convert.support.DefaultConversionService
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class MinusCommandTest extends Specification {

    def conversionService = new DefaultConversionService()

    def "Minus command evaluation for #params wit result #expected"() {
        when:
        def actual = new MinusCommand(conversionService).execute(*params)
        then:
        expected == actual
        where:
        params              || expected
        ["1", "1"]          || new BigDecimal("0")
        [2, 2]              || new BigDecimal("0")
        ["1.1", "1.5"]      || new BigDecimal("-0.4")
        [2.6, 2.2]          || new BigDecimal("0.4")
        ["1.1", "1.1", "1"] || new BigDecimal("-1")
        [6.2D, 2.2F, 2L]    || new BigDecimal("2")
    }

    @Unroll
    def "Test wrong number of params for MinusCommand"() {
        when:
        new MinusCommand(conversionService).execute([1])

        then:
        Exception exception = thrown()
        IllegalArgumentException.class == exception.class
        "At least two parameters were expected" == exception.message
    }
}
