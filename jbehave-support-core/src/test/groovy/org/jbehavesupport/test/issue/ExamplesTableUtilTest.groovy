package org.jbehavesupport.test.issue

import org.jbehave.core.model.ExamplesTable
import org.jbehavesupport.core.internal.ExamplesTableUtil
import spock.lang.Specification

class ExamplesTableUtilTest extends Specification {

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
