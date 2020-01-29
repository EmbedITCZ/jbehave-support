package org.jbehavesupport.core.ws

import org.jbehavesupport.test.support.TestSupport
import org.junit.runner.JUnitCore
import spock.lang.Issue
import spock.lang.Shared
import spock.lang.Specification

class SimpleListIT extends Specification implements TestSupport {

    @Shared
        runner = new JUnitCore()

    def "Test simple string list"() {
        when:
        def result = runner.run(runWith("ws/SimpleList.story"))

        then:
        result.failures.isEmpty()
    }
}
