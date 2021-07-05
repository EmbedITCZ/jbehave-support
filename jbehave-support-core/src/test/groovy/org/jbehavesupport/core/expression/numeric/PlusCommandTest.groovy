package org.jbehavesupport.core.expression.numeric

import org.jbehavesupport.core.internal.expression.numeric.PlusCommand
import org.springframework.core.convert.support.DefaultConversionService
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class PlusCommandTest extends Specification {

    def conversionService = new DefaultConversionService()

    def "Plus command evaluation for #params wit result #expected"() {
        when:
        def actual = new PlusCommand(conversionService).execute(*params)
        then:
        expected == actual
        where:
        params              || expected
        ["1", "1"]          || new BigDecimal("2")
        [2, 2]              || new BigDecimal("4")
        ["1.1", "1.1"]      || new BigDecimal("2.2")
        [2.2, 2.2]          || new BigDecimal("4.4")
        ["1.1", "1.1", "1"] || new BigDecimal("3.2")
        [2.2D, 2.2F, 2L]    || new BigDecimal("6.4")
    }

    @Unroll
    def "Test wrong number of params for PlusCommand"() {
        when:
        new PlusCommand(conversionService).execute([1])

        then:
        Exception exception = thrown()
        IllegalArgumentException.class == exception.class
        "At least two parameters were expected" == exception.message
    }

}
