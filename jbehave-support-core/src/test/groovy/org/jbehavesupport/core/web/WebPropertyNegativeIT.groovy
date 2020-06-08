package org.jbehavesupport.core.web

import org.jbehavesupport.test.support.TestSupport
import org.junit.runner.JUnitCore
import spock.lang.Shared
import spock.lang.Specification

class WebPropertyNegativeIT extends Specification implements TestSupport {

    @Shared
    runner = new JUnitCore()

    def "Soft assertions of web properties"() {
        when:
        def result = runner.run(runWith("web/WebPropertyNegative.story"))

        then:
        result.getFailureCount() == 1
        result.getFailures().get(0).getMessage().contains("[element [#enabled-btn], property [ENABLED]] ")
        result.getFailures().get(0).getMessage().contains("value 'true' is not equal to 'false'")
        result.getFailures().get(0).getMessage().contains("[element [#disabled-btn], property [ENABLED]]")
        result.getFailures().get(0).getMessage().contains("value 'false' is not equal to 'true'")
        result.getFailures().get(0).getMessage().contains("[element [#class], property [CLASS]]")
        result.getFailures().get(0).getMessage().contains("value 'my-class' is not equal to 'no-class'")
        result.getFailures().get(0).getMessage().contains("[element [#select], property [SELECTED_TEXT]] ")
        result.getFailures().get(0).getMessage().contains("value 'two' is not equal to 'one'")
    }
}
