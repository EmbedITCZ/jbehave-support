package org.jbehavesupport.core.expression


import org.jbehave.core.configuration.MostUsefulConfiguration
import org.jbehave.core.model.ExamplesTable
import org.jbehave.core.steps.ParameterConverters
import org.jbehavesupport.core.TestConfig
import org.jbehavesupport.core.internal.expression.NullCommand
import org.jbehavesupport.core.internal.parameterconverters.ExamplesEvaluationTableConverter
import org.jbehavesupport.core.internal.parameterconverters.NullStringConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
@ContextConfiguration(classes = TestConfig)
class ExamplesTableTest extends Specification {

    @Autowired
    private ExamplesEvaluationTableConverter converter

    @Autowired
    private NullStringConverter nullStringConverter

    def nullTable = "| name | value  |\n" +
                    "| test | {NULL} |"

    def "test null value conversion with getRowsAsParameters"() {
        given:
        def paramsConverters = new ParameterConverters().addConverters(nullStringConverter)
        converter.setConfiguration(new MostUsefulConfiguration().useParameterConverters(paramsConverters))

        when:
        ExamplesTable result = converter.convertValue(nullTable, ExamplesTable.class)
        def rows = result.getRowsAsParameters()

        then:
        rows.get(0).valueAs("name", String.class) == "test"
        rows.get(0).valueAs("value", String.class) == null
    }

    def "test null value conversion with getRow"() {
        given:
        def paramsConverters = new ParameterConverters().addConverters(nullStringConverter)
        converter.setConfiguration(new MostUsefulConfiguration().useParameterConverters(paramsConverters));

        when:
        ExamplesTable result = converter.convertValue(nullTable, ExamplesTable.class)
        def rows = result.getRows()

        then:
        rows.get(0).get("name") == "test"
        rows.get(0).get("value") == NullCommand.NULL_VALUE
    }

    def "test nested value conversion with getRow"() {
        given:
        converter.setConfiguration(new MostUsefulConfiguration());
        def table = "|name      | value                                             |\n" +
                    "|test      |{CONCAT:{CONCAT:1:2}: :{CONCAT:3:4:5}}             |\n" +
                    "|test      |{CONCAT:{CONCAT:1:2}: :{CONCAT:3:{CONCAT:4:5}}}    |\n" +
                    "|test2     |{CONCAT:1:2} {CONCAT:3:4:5}                        |"

        when:
        ExamplesTable result = converter.convertValue(table, ExamplesTable.class)
        def rows = result.getRows()

        then:
        rows.get(0).get("name") == "test"
        rows.get(0).get("value") == "12 345"
        rows.get(1).get("value") == "12 345"
        rows.get(2).get("value") == "12 345"
    }
}
