package org.jbehavesupport.core.expression


import org.jbehavesupport.core.internal.expression.NullCommand
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class NullCommandTest extends Specification {

    def "test execute with #expression returns #expected"() {
        expect:
        new NullCommand().execute() == NullCommand.NULL_VALUE;
    }

    def "test execute with #expression throws #exception"() {
        when:
        new NullCommand().execute(*expression)

        then:
        thrown(exception)

        where:
        expression || exception
        ["1"]      || IllegalArgumentException
    }

}
