package org.jbehavesupport.core.expression


import org.jbehavesupport.core.internal.expression.NilCommand
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class NilCommandTest extends Specification {

    def "test execute with #expression returns #expected"() {
        when:
        def result = new NilCommand().execute(*expression)

        then:
        result == expected

        where:
        expression || expected
        []         || NilCommand.NIL
    }

    def "test execute with #expression throws #exception"() {
        when:
        new NilCommand().execute(*expression)

        then:
        thrown(exception)

        where:
        expression || exception
        ["1"]      || IllegalArgumentException
    }

}
