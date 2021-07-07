package org.jbehavesupport.core.expression.numeric

import org.jbehavesupport.core.internal.expression.numeric.RoundCommand
import org.springframework.core.convert.support.DefaultConversionService
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class RoundCommandTest extends Specification {

    def conversionService = new DefaultConversionService()

    def "Round command evaluation for #params wit result #expected"() {
        when:
        def actual = new RoundCommand(conversionService).execute(*params)
        then:
        expected == actual
        where:
        params                 || expected
        ["1", "1"]             || new BigDecimal("1")
        [1.1111111111, 0]      || new BigDecimal("1")
        ["1.1111111111", "10"] || new BigDecimal("1.1111111111")
        [3.3F, 1]              || new BigDecimal("3.3")
        [6.6D, 1]              || new BigDecimal("6.6")
        ["111.111111111", "1"] || new BigDecimal("111.1")
    }

    @Unroll
    def "Test wrong number of params for DivideCommand"() {
        when:
        new RoundCommand(conversionService).execute([1])

        then:
        Exception exception = thrown()
        IllegalArgumentException.class == exception.class
        "Two parameters were expected" == exception.message
    }

    @Unroll
    def "Test wrong format of scale parameter"() {
        when:
        new RoundCommand(conversionService).execute([1, 1.5].toArray())

        then:
        Exception exception = thrown()
        IllegalArgumentException.class == exception.class
        "Scale must be integer" == exception.message
    }

}
