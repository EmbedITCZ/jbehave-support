package org.jbehavesupport.core.internal.parameterconverters

import org.jbehave.core.steps.ParameterConverters
import org.jbehavesupport.core.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import java.time.LocalDate

@ContextConfiguration(classes = TestConfig)
class LocalDateConverterTest extends Specification {

    @Autowired
    ParameterConverters.ParameterConverter<LocalDate> converter

    def "Accept"() {
        expect:
        converter.accept(LocalDate.class) == true
    }

    def "ConvertValue"() {
        when:
        def actual = converter.convertValue(input, LocalDate.class)

        then:
        actual == expected
        expected instanceof LocalDate

        where:
        input          | expected
        "2020-05-01"   | LocalDate.of(2020, 05, 01)
    }

}
