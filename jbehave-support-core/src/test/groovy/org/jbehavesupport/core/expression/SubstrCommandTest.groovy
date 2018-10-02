package org.jbehavesupport.core.expression


import org.jbehavesupport.core.internal.expression.SubstrCommand
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class SubstrCommandTest extends Specification {

    def "test execute with #params returns #expected"() {
        when:
        def result = new SubstrCommand().execute(*params)

        then:
        result == expected

        where:
        params              || expected
        ["test", "1"]       || "est"
        ["test", 1]         || "est"
        ["test", "1", "3"]  || "es"
        ["test", 1, 3]      || "es"
    }

    def "test argument exception #expected"() {
        when:
        new SubstrCommand().execute(*params)

        then:
        Exception exception = thrown()
        expected == exception.class

        where:
        params            || expected
        ["1"]             || IllegalArgumentException.class
        ["1", 1, 2, "3"]  || IllegalArgumentException.class
    }
}
