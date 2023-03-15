package org.jbehavesupport.core

import org.jbehavesupport.test.support.TestSupportBrowserStack
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static org.jbehavesupport.test.support.TestConfig.FIREFOX_BROWSERSTACK

/**
 * This test verifies that our code is compatible with Firefox using BrowserStack.
 * It assumes BrowserStack Local binary is already running.
 */
@ContextConfiguration(classes = TestConfig.class)
class BrowserStackFirefoxT extends Specification implements TestSupportBrowserStack {

    @Autowired
    private Environment env

    def setup() {
        System.setProperty("web.browser", FIREFOX_BROWSERSTACK)
        System.setProperty("browser-stack.build", getBuildName())
    }

    def "Firefox test #story"() {
        when:
        System.setProperty("browser-stack.name", "Firefox test " + story)
        def result = run(runWith(story))

        then:
        result.totalFailureCount == 0

        where:
        story                             | _
        "sample/Web.story"                | _
        "sample/WebGivenStoryUsage.story" | _
        "sample/WebProperty.story"        | _
        "sample/WebWaitCondition.story"   | _
        "sample/WebAction.story"          | _
        "sample/WebNavigation.story"      | _
        "sample/WebScrollAction.story"    | _
    }

}
