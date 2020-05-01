package org.jbehavesupport.core.internal.parameterconverters

import org.jbehave.core.steps.ParameterConverters
import org.jbehavesupport.core.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = TestConfig)
class BigDecimalConverterTest extends Specification {

    @Autowired
    ParameterConverters.ParameterConverter<BigDecimal> converter

    def "Accept"() {
        expect:
        converter.accept(BigDecimal.class) == true
    }

    def "ConvertValue"() {
        when:
        def actual = converter.convertValue(input, BigDecimal.class)

        then:
        actual == expected
        expected instanceof BigDecimal

        where:
        input | expected
        "1"   | BigDecimal.ONE
        "1.5" | new BigDecimal("1.5")
    }

}
