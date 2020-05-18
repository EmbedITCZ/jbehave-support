package org.jbehavesupport.core

import org.jbehavesupport.test.support.TestSupportBrowserStack
import org.junit.runner.JUnitCore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

import static org.jbehavesupport.test.support.TestConfig.CHROME_BROWSERSTACK

/**
 * This test verifies that our code is compatible with Chrome using BrowserStack.
 */
@ContextConfiguration(classes = TestConfig.class)
class BrowserStackChromeT extends Specification implements TestSupportBrowserStack {

    @Autowired
    private Environment env

    @Shared
    runner = new JUnitCore()

    def setup() {
        System.setProperty("web.browser", CHROME_BROWSERSTACK)
        System.setProperty("browser-stack.build", getBuildName())
        setupBrowserStackLocal(env.getProperty("browser-stack.key"))
    }

    def cleanupSpec() {
        stopBrowserStackLocal()
    }

    def "Chrome test #story"() {
        when:
        System.setProperty("browser-stack.name", "Chrome test " + story)
        def result = runner.run(runWith(story))

        then:
        result.failureCount == 0

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
