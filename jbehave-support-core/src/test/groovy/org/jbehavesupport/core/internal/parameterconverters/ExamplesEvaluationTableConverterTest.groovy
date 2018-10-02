package org.jbehavesupport.core.internal.parameterconverters

import org.jbehavesupport.core.TestConfig
import org.jbehave.core.model.ExamplesTable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(classes = TestConfig.class)
class ExamplesEvaluationTableConverterTest extends Specification {
    private static final String NEW_LINE = System.getProperty("line.separator")

    @Autowired
    ExamplesEvaluationTableConverter converter

    @Unroll
    def "test convertValue"() {
        when:
            ExamplesTable value = converter.convertValue(exampleTable, null)

        then:
            value.getHeaders() == headers
            value.getRow(0) == row

        where:
            exampleTable                        | headers                | row
            "|name|value|" + NEW_LINE +
            "|test|1    |"                      | ["name", "value"]      | ["name":"test", "value":"1"]
        "{ignorableSeparator=!--,headerSeparator=!,valueSeparator=!,commentSeparator=#}" + NEW_LINE +
            "!name!value!" + NEW_LINE +
            "!test!1    !"                      | ["name", "value"]      | ["name":"test", "value":"1"]
        "{headerSeparator=-,valueSeparator=!}" + NEW_LINE +
            "-name-value-" + NEW_LINE +
            "!test!1    !"                      | ["name", "value"]      | ["name":"test", "value":"1"]
        NEW_LINE +
        "{headerSeparator=-,valueSeparator=!}" + NEW_LINE +
            "-name-value-" + NEW_LINE +
            "!test!1    !"                      | ["name", "value"]      | ["name":"test", "value":"1"]
    }
}
