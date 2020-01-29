package org.jbehavesupport.core.ws

import org.jbehavesupport.test.support.TestSupport
import org.junit.runner.JUnitCore
import spock.lang.Issue
import spock.lang.Shared
import spock.lang.Specification


class ValidateWsResponse extends Specification implements TestSupport {
    @Shared
        runner = new JUnitCore()

    def "Test failing context"() {
        when:
        def result = runner.run(runWith("ws/ValidateWsResponse.story"))

        then:
        result.failureCount == 0
    }
}
