package org.jbehavesupport.core.expression


import org.jbehavesupport.core.internal.expression.PlusCommand
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class PlusCommandTest extends Specification {

    def "Plus command evaluation for #params wit result #expected"() {
        when:
        def actual = new PlusCommand().execute(*params)
        then:
        expected == actual
        where:
        params              || expected
        ["1", "1"]          || new BigDecimal("2")
        [2, 2]              || new BigDecimal("4")
        ["1.1", "1.1"]      || new BigDecimal("2.2")
        [2.2, 2.2]          || new BigDecimal("4.4")
        ["1.1", "1.1", "1"] || new BigDecimal("3.2")
        [2.2D, 2.2F, 2L]    || new BigDecimal("6.4")
    }

    def "Plus command evaluation for #params should throw #expected"() {
        when:
        new PlusCommand().execute(*params)

        then:
        Exception exception = thrown()
        expected == exception.class
        message == exception.message

        where:
        params       || expected                       || message
        ["1"]        || IllegalArgumentException.class || "At least two parameters were expected"
        ["a", "1"]   || IllegalArgumentException.class || "String parameter must be numeric: a"
        ["1,1", "1"] || IllegalArgumentException.class || "String parameter must be numeric: 1,1"
    }

}
