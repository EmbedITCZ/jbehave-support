package org.jbehavesupport.core.expression.numeric

import org.jbehavesupport.core.internal.expression.numeric.DivideCommand
import org.springframework.core.convert.support.DefaultConversionService
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class DivideCommandTest extends Specification {

    def conversionService = new DefaultConversionService()

    def "Divide command evaluation for #params wit result #expected"() {
        when:
        def actual = new DivideCommand(conversionService).execute(*params)
        then:
        expected == actual
        where:
        params               || expected
        ["1", "1", "1"]      || new BigDecimal("1")
        [4, 2]               || new BigDecimal("2")
        ["1.1", "1.5"]       || new BigDecimal("0.7333333333")
        ["1.1", "1.5", "1"]  || new BigDecimal("0.7333333333")
        ["1.1", "1.5", "5"]  || new BigDecimal("0.1466666667")
        [-2.6, -1.3, 1]      || new BigDecimal("2")
        ["1.1", "-1.1", "1"] || new BigDecimal("-1")
        [6.6D, 3.3F, 1]      || new BigDecimal("2")
    }

    @Unroll
    def "Test wrong number of params for DivideCommand"() {
        when:
        new DivideCommand(conversionService).execute([1])

        then:
        Exception exception = thrown()
        IllegalArgumentException.class == exception.class
        "At least two parameters were expected" == exception.message
    }

    @Unroll
    def "Test dividing by zero"() {
        when:
        new DivideCommand(conversionService).execute([1, 0, 1].toArray())

        then:
        Exception exception = thrown()
        IllegalArgumentException.class == exception.class
        "Can not divide by zero" == exception.message
    }

}
