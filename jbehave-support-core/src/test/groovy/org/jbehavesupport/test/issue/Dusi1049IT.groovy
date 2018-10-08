package org.jbehavesupport.test.issue

import org.jbehavesupport.test.support.TestSupport
import org.junit.runner.JUnitCore
import spock.lang.Issue
import spock.lang.Shared
import spock.lang.Specification


class Dusi1049IT extends Specification implements TestSupport {
    @Shared
        runner = new JUnitCore()

    def "Test failing context"() {
        when:
        def result = runner.run(runWith("issue/DUSI-1049.story"))

        then:
        result.failureCount == 0
    }
}
