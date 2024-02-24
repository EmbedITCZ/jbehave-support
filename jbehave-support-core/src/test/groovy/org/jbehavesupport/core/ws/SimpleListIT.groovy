package org.jbehavesupport.core.ws

import org.jbehavesupport.test.support.TestAppSupport
import org.jbehavesupport.test.support.TestSupport
import spock.lang.Specification

class SimpleListIT extends Specification implements TestSupport, TestAppSupport {

    def "Test simple string list"() {
        when:
        def result = run(runWith("ws/SimpleList.story"))

        then:
        result.failures.isEmpty()
    }
}
