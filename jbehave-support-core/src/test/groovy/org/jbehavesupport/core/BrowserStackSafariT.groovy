package org.jbehavesupport.core

import org.jbehavesupport.test.support.TestAppSupport
import org.jbehavesupport.test.support.TestSupportBrowserStack
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static org.jbehavesupport.test.TestConfig.SAFARI_BROWSERSTACK

/**
 * This test verifies that our code is compatible with Safari using BrowserStack.
 * It assumes BrowserStack Local binary is already running.
 */
@ContextConfiguration(classes = TestConfig.class)
class BrowserStackSafariT extends Specification implements TestSupportBrowserStack, TestAppSupport {

    @Autowired
    private Environment env

    def setup() {
        System.setProperty("web.browser", SAFARI_BROWSERSTACK)
        System.setProperty("web.url", "http://bs-local.com:$uiPort/")
        System.setProperty("browser-stack.build", getBuildName())
    }

    def "Safari test #story"() {
        when:
        System.setProperty("browser-stack.name", "Safari test " + story)
        def result = run(runWith(story))

        then:
        result.totalFailureCount == 0

        where:
        story | _
        "sample/Web.story"                      | _
        "sample/WebGivenStoryUsage.story"       | _
        "sample/WebProperty.story"              | _
        "sample/WebWaitCondition.story"         | _
        "sample/WebAction.story"                | _
        "sample/WebNavigation.story"            | _
        "sample/WebScrollAction.story"          | _
    }

}
