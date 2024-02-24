package org.jbehavesupport.core.internal.parameterconverters

import org.jbehave.core.steps.ParameterConverters
import org.jbehavesupport.core.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

@ContextConfiguration(classes = TestConfig)
class XMLGregorianCalendarConverterTest extends Specification {

    @Autowired
    ParameterConverters.ParameterConverter<String, XMLGregorianCalendar> converter

    def "Accept"() {
        expect:
        converter.canConvertTo(XMLGregorianCalendar.class) == true
    }

    def "ConvertValue"() {
        when:
        def actual = converter.convertValue(input, XMLGregorianCalendar.class)

        then:
        actual == expected
        expected instanceof XMLGregorianCalendar

        where:
        input                   | expected
        "2020-05-01"            | DatatypeFactory.newInstance().newXMLGregorianCalendar("2020-05-01")
    }

}
