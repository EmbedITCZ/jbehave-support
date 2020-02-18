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

    def "should not spot duplicity in column"() {

        setup:
        def table = new ExamplesTable(
            "| name  | data   |\n" +
            "| test  | value1 |\n" +
            "| test1 | value2 |\n" +
            "| test2 | value1 |")

        when:
        ExamplesTableUtil.assertDuplicatesInColumns(table, "name")

        then:
        notThrown(Error)
    }

    def "should spot multiple duplicates in column"() {

        setup:
        def table = new ExamplesTable(
             "| name  | data   |\n" +
             "| test1 | value1 |\n" +
             "| test  | value2 |\n" +
             "| test1 | value2 |\n" +
             "| test3 | value2 |\n" +
             "| test2 | value2 |\n" +
             "| test1 | value1 |")

        when:
        ExamplesTableUtil.assertDuplicatesInColumns(table, "name")

        then:
        def error = thrown(Error)
        error.getMessage().contains("Examples table contains duplicate value: [test1] in a column [name] in rows: [0, 2, 5]")
    }

    def "should spot multiple duplicates in multiple columns"() {

        setup:
        def table = new ExamplesTable(
             "| name  | data   |\n" +
             "| test1 | value1 |\n" +
             "| test  | value2 |\n" +
             "| test1 | value2 |\n" +
             "| test2 | value2 |\n" +
             "| test2 | value2 |\n" +
             "| test1 | value1 |")

        when:
        ExamplesTableUtil.assertDuplicatesInColumns(table, "name", "data")

        then:
        def error = thrown(Error)
        error.getMessage().contains("Examples table contains duplicate value: [test1] in a column [name] in rows: [0, 2, 5]")
        error.getMessage().contains("Examples table contains duplicate value: [test2] in a column [name] in rows: [3, 4]")
        error.getMessage().contains("Examples table contains duplicate value: [value1] in a column [data] in rows: [0, 5]")
        error.getMessage().contains("Examples table contains duplicate value: [value2] in a column [data] in rows: [1, 2, 3, 4]")
    }
}
