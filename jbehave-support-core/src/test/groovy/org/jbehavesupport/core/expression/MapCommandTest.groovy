package org.jbehavesupport.core.expression


import org.jbehavesupport.core.internal.expression.MapCommand
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class MapCommandTest extends Specification {

    def "test execute with #expression returns #expected"() {
        when:
        def actual = new MapCommand().execute(*expression)
        then:
        expected == actual
        where:
        expression              || expected
        ["1", "[0,OK],[1,NOK]"] || "NOK"
    }

    def "test execute with #expression throws #expected"() {
        when:
        new MapCommand().execute(*expression)
        then:
        Exception exception = thrown()
        expected == exception.class
        message == exception.getMessage()
        where:
        expression || expected                       || message
        []         || IllegalArgumentException.class || "Two parameters were expected"
        ["a", "1"] || IllegalArgumentException.class || "Pattern of '1' doesn't match expected pattern"
    }

}
