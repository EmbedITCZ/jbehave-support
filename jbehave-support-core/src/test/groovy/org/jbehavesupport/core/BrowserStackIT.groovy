package org.jbehavesupport.core

import org.jbehavesupport.test.support.TestSupportBrowserStack

import static org.jbehavesupport.test.support.TestConfig.CHROME_BROWSERSTACK
import static org.jbehavesupport.test.support.TestConfig.SAFARI_BROWSERSTACK
import static org.jbehavesupport.test.support.TestConfig.FIREFOX_BROWSERSTACK

import com.browserstack.local.Local
import org.jbehavesupport.core.TestConfig
import org.junit.runner.JUnitCore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * This test verify that our code is compatible with various browsers using BrowserStack.
 * Web and WebAction stories aren't working yet.
 */
@ContextConfiguration(classes = TestConfig.class)
class BrowserStackIT extends Specification implements TestSupportBrowserStack {

    @Autowired
    private Environment env

    @Shared
    runner = new JUnitCore()

    @Unroll
    def "Safari test"() {
        when:
        Local bsLocal = new Local()
        HashMap<String, String> bsLocalArgs = new HashMap<String, String>()
        bsLocalArgs.put("key", env.getProperty("browser-stack.key"))
        bsLocal.start(bsLocalArgs)
        System.setProperty("web.browser", SAFARI_BROWSERSTACK)
        System.setProperty("web.url", "http://bs-local.com:11110/")
        def result = runner.run(runWith(story))
        bsLocal.stop()

        then:
        result.failureCount == 0

        where:
        story | _
        //"sample/Web.story"                      | _
        "sample/WebGivenStoryUsage.story"       | _
        "sample/WebProperty.story"              | _
        "sample/WebWaitCondition.story"         | _
        //"sample/WebAction.story"                | _
        "sample/WebNavigation.story"            | _
    }

    @Unroll
    def "Firefox test"() {
        when:
        Local bsLocal = new Local()
        HashMap<String, String> bsLocalArgs = new HashMap<String, String>()
        bsLocalArgs.put("key", env.getProperty("browser-stack.key"))
        bsLocal.start(bsLocalArgs)
        System.setProperty("web.browser", FIREFOX_BROWSERSTACK)
        def result = runner.run(runWith(story))
        bsLocal.stop()

        then:
        result.failureCount == 0

        where:
        story | _
        //"sample/Web.story"                      | _
        "sample/WebGivenStoryUsage.story"       | _
        "sample/WebProperty.story"              | _
        "sample/WebWaitCondition.story"         | _
        //"sample/WebAction.story"                | _
        "sample/WebNavigation.story"            | _
    }

    @Unroll
    def "Chrome test"() {
        when:
        Local bsLocal = new Local()
        HashMap<String, String> bsLocalArgs = new HashMap<String, String>()
        bsLocalArgs.put("key", env.getProperty("browser-stack.key"))
        bsLocal.start(bsLocalArgs)
        System.setProperty("web.browser", CHROME_BROWSERSTACK)
        def result = runner.run(runWith(story))
        bsLocal.stop()

        then:
        result.failureCount == 0

        where:
        story | _
        //"sample/Web.story"                      | _
        "sample/WebGivenStoryUsage.story"       | _
        "sample/WebProperty.story"              | _
        "sample/WebWaitCondition.story"         | _
        //"sample/WebAction.story"                | _
        "sample/WebNavigation.story"            | _
    }
}
