package org.jbehavesupport.core.ws

import org.jbehavesupport.test.support.TestSupport
import spock.lang.Specification

class RequestResponseAliasIT extends Specification implements TestSupport {

    def "Test us request response aliases"() {
        when:
        def result = run(runWith("ws/RequestResponseAlias.story"))

        then:
        result.failures.isEmpty()
    }
}
