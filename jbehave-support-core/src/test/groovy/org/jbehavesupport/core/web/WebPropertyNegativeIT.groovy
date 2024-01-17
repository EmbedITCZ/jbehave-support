package org.jbehavesupport.core.web

import org.jbehavesupport.test.support.TestAppSupport
import org.jbehavesupport.test.support.TestSupport
import spock.lang.Specification

class WebPropertyNegativeIT extends Specification implements TestSupport, TestAppSupport {

    def "Soft assertions of web properties"() {
        when:
        def result = run(runWith("web/WebPropertyNegative.story"))

        then:
        result.getTotalFailureCount() == 1
        result.getFailures().get(0).exception.getMessage().contains("[element [#enabled-btn], property [ENABLED]] ")
        result.getFailures().get(0).exception.getMessage().contains("value 'true' is not equal to 'false'")
        result.getFailures().get(0).exception.getMessage().contains("[element [#disabled-btn], property [ENABLED]]")
        result.getFailures().get(0).exception.getMessage().contains("value 'false' is not equal to 'true'")
        result.getFailures().get(0).exception.getMessage().contains("[element [#class], property [CLASS]]")
        result.getFailures().get(0).exception.getMessage().contains("value 'my-class' is not equal to 'no-class'")
        result.getFailures().get(0).exception.getMessage().contains("[element [#select], property [SELECTED_TEXT]] ")
        result.getFailures().get(0).exception.getMessage().contains("value 'two' is not equal to 'one'")
    }
}
