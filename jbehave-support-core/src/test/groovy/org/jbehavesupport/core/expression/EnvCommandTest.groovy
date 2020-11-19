package org.jbehavesupport.core.expression

import org.jbehavesupport.core.TestConfig
import org.jbehavesupport.core.internal.expression.EnvCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@ContextConfiguration(classes = TestConfig)
@TestPropertySource(properties = "prop-key=prop-value")
class EnvCommandTest extends Specification {

    @Autowired
    EnvCommand envCommand

    def "env command execute with #param returns #expected"() {
        when:
        def result = envCommand.execute(param)

        then:
        result == expected

        where:
        param           || expected
        "prop-key"      || "prop-value"
        "not-used-key"  || null
    }

    def "test argument exception"() {
        when:
        envCommand.execute(params)

        then:
        Exception exception = thrown()
        expected == exception.class

        where:
        params                         || expected
        ["firstParam", "secondParam"]  || IllegalArgumentException.class
        []                             || IllegalArgumentException.class
    }

}
