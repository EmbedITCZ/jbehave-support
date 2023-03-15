package org.jbehavesupport.core.ws

import org.jbehavesupport.test.support.TestSupport
import spock.lang.Specification

class ValidateWsResponse extends Specification implements TestSupport {

    def "Test failing context"() {
        when:
        def result = run(runWith("ws/ValidateWsResponse.story"))

        then:
        result.totalFailureCount == 0
    }
}
