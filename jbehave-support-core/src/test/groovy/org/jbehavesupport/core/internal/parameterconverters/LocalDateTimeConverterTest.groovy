package org.jbehavesupport.core.internal.parameterconverters

import org.jbehave.core.steps.ParameterConverters
import org.jbehavesupport.core.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import java.time.LocalDateTime

@ContextConfiguration(classes = TestConfig)
class LocalDateTimeConverterTest extends Specification {

    @Autowired
    ParameterConverters.ParameterConverter<LocalDateTime> converter

    def "Accept"() {
        expect:
        converter.accept(LocalDateTime.class) == true
    }

    def "ConvertValue"() {
        when:
        def actual = converter.convertValue(input, LocalDateTime.class)

        then:
        actual == expected
        expected instanceof LocalDateTime

        where:
        input                   | expected
        "2020-05-01T01:02:03"   | LocalDateTime.of(2020, 05, 01, 01, 02, 03)
        "2020-05-01T11:22"      | LocalDateTime.of(2020, 05, 01, 11, 22)
    }

}
