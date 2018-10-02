package org.jbehavesupport.core.expression


import org.jbehavesupport.core.internal.expression.RandomEmailCommand
import spock.lang.Specification

class RandomEmailCommandTest extends Specification {

    def "test random email generating"() {
        when:
        def result = new RandomEmailCommand().execute()
        def pattern =  /[_A-Za-z0-9-]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})/

        then:
        result != null
        result instanceof String
        result ==~ pattern
    }

    def "test argument exception"() {
        when:
        new RandomEmailCommand().execute(*params)

        then:
        Exception exception = thrown()
        expected == exception.class

        where:
        params      || expected
        ["1"]       || IllegalArgumentException.class
    }
}
