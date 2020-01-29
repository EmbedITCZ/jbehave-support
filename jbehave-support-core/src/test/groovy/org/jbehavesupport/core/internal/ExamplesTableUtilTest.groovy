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

    def "should accept mandatory columns"() {
        setup:
        def table = new ExamplesTable("| name | data | other |")

        when:
        ExamplesTableUtil.assertMandatoryColumns(table, expectedColumns as String[])

        then:
        notThrown(Error)

        where:
        expectedColumns           | _
        ["name"]                  | _
        ["name", "data"]          | _
        ["data", "other"]         | _
        ["other", "name"]         | _
        ["other"]                 | _
        ["name", "data", "other"] | _
    }

    def "should spot missing mandatory columns"() {
        setup:
        def table = new ExamplesTable("| name | data | other |")

        when:
        ExamplesTableUtil.assertMandatoryColumns(table, expectedColumns as String[])

        then:
        def exception = thrown(Error)
        exception.getMessage().contains(containsMessage)

        where:
        expectedColumns  | containsMessage
        ["nick"]         | "nick"
        ["nick", "name"] | "nick"
    }

    def "should fail when second parameter is missing"() {
        setup:
        def table = new ExamplesTable("| name | data | other |")

        when:
        ExamplesTableUtil.assertMandatoryColumns(table)

        then:
        def exception = thrown(IllegalArgumentException)
        exception.getMessage().contains("expectedColumns must not be empty")
    }
}
