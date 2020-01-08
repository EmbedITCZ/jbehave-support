package org.jbehavesupport.core.internal

import org.jbehave.core.model.ExamplesTable
import spock.lang.Specification

class ExamplesTableUtilTest extends Specification {


    def "lookup for value in example table"() {

        setup:
        def table = new ExamplesTable(  "| name  | data   |\n" +
                                        "| test  | value1 |\n" +
                                        "| test  | value2 |\n" +
                                        "| test2 | value3 |")

        when:
        def result = ExamplesTableUtil.tableContains(table, column, { s -> (value == s) })

        then:
        result == expected

        where:
        column | value    || expected
        null   | "name"   || false
        null   | "value"  || false
        null   | "test"   || true
        null   | "value1" || true
        null   | "valueX" || false
        null   | "value"  || false
        "name" | "test"   || true
        "name" | "value1" || false
    }

    def "convertMap"() {

        setup:
        def table = new ExamplesTable(  "| name  | surname  | value |\n" +
                                        "| cat   | home     | meow  |\n" +
                                        "| dog   | street   | huf   |")

        when:
        def result = ExamplesTableUtil.convertMap(table, key, value)

        then:
        result == expected

        where:
        key    | value     || expected
        "name" | "value"   || ['cat': 'meow', 'dog': 'huf']
        "name" | "surname" || ['cat': 'home', 'dog': 'street']
    }
}
