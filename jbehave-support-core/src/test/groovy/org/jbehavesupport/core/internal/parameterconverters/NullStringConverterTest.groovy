package org.jbehavesupport.core.internal.parameterconverters

import org.jbehave.core.steps.ParameterConverters
import org.jbehavesupport.core.TestConfig
import org.jbehavesupport.core.internal.expression.NullCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = TestConfig)
class NullStringConverterTest extends Specification {

    @Autowired
    ParameterConverters.ParameterConverter<String, String> converter

    def "Accept"() {
        expect:
        converter.canConvertTo(String.class) == true
    }

    def "ConvertValue"() {
        when:
        def actual = converter.convertValue(input, String.class)

        then:
        actual == expected

        where:
        input                  | expected
        NullCommand.NULL_VALUE | null
    }

}
