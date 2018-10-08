package org.jbehavesupport.core.expression


import org.jbehavesupport.core.internal.expression.DateParseCommand
import org.jbehavesupport.core.internal.expression.EmptyStringCommand
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class EmptyStringCommandTest extends Specification {
    def "test empty string command"() {
        when:
        def result = new EmptyStringCommand().execute()

        then:
        result == ""
    }

    def "test execute with #expression throws #exception"() {
        when:
        new DateParseCommand().execute(*expression)

        then:
        thrown(exception)

        where:
        expression || exception
        ["1"]      || IllegalArgumentException
    }

}
