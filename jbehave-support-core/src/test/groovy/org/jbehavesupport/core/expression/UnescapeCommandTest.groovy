package org.jbehavesupport.core.expression


import org.jbehavesupport.core.internal.expression.UnescapeCommand
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

@Unroll
class UnescapeCommandTest extends Specification {

    def "test execute with #expression returns #expected"() {
        when:
        def actual = new UnescapeCommand().execute(*expression)
        then:
        expected == actual
        where:
        expression     || expected
        ["\\t"]        || "\t"
        ["foo\\nbar"] || "foo\nbar"
    }

    def "test execute with #expression throws #expected"() {
        when:
        new UnescapeCommand().execute(*expression)
        then:
        Exception exception = thrown()
        expected == exception.class
        where:
        expression          || expected
        []                  || IllegalArgumentException.class
        ["a", "1"]          || IllegalArgumentException.class
        [LocalDate.now()]   || IllegalArgumentException.class
    }

}
