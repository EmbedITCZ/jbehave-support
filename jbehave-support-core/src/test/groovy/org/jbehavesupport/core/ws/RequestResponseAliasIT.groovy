package org.jbehavesupport.core.ws

import org.jbehavesupport.test.support.TestSupport
import org.junit.runner.JUnitCore
import spock.lang.Issue
import spock.lang.Shared
import spock.lang.Specification

class RequestResponseAliasIT extends Specification implements TestSupport {

    @Shared
        runner = new JUnitCore()

    def "Test us request response aliases"() {
        when:
        def result = runner.run(runWith("ws/RequestResponseAlias.story"))

        then:
        result.failures.isEmpty()
    }
}
