package org.jbehavesupport.core.expression

import org.jbehavesupport.core.TestConfig
import org.jbehavesupport.core.TestContext
import org.jbehavesupport.core.internal.expression.BytesCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = TestConfig)
class BytesCommandTest extends Specification {

    @Autowired
    private BytesCommand bytesCommand

    @Autowired
    private TestContext testContext

    void ConvertsCorrectly() {
        when:
        def result = testContext.get(bytesCommand.execute(*params))

        then:
        result == expected.getBytes()

        where:
        params          || expected
        ["Tested test"] || "Tested test"
    }

}
