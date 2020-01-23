package org.jbehavesupport.core.rest

import org.jbehavesupport.test.support.TestSupport
import org.junit.runner.JUnitCore
import spock.lang.Shared
import spock.lang.Specification

class NegativeRestScenariosIT extends Specification implements TestSupport {

    @Shared
        runner = new JUnitCore()

    def "Should validate REST response via success handlers"() {
        when:
        def result = runner.run(runWith("rest/NegativeRestHandling.story"))

        then:
        true
        result.getFailureCount() == 3
        result.getFailures().get(0).getMessage().contains("Expected response code is CREATED but was BAD_REQUEST")
        result.getFailures().get(1).getMessage() == "value 'sad' is not equal to 'happy'"
        result.getFailures().get(2).getMessage() == "value 'astronaut' is not equal to 'hippopotamus'"
    }
}
