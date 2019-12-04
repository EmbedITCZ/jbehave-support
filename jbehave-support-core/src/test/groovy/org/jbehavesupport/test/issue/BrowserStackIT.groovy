package org.jbehavesupport.test.issue

import org.jbehavesupport.core.TestConfig
import org.jbehavesupport.core.web.WebDriverFactoryResolver
import org.jbehavesupport.test.support.TestSupport
import org.junit.runner.JUnitCore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(classes = TestConfig)
class BrowserStackIT extends Specification implements TestSupport {

    @Autowired
    WebDriverFactoryResolver webDriverFactoryResolver

    @Shared
    runner = new JUnitCore()

    @Unroll
    def "Firefox test"() {
        when:
        webDriverFactoryResolver.setBrowserName(TestConfig.FIREFOX_BROWSERSTACK)
        def result = runner.run(runWith(story))

        then:
        result.failureCount == 0

        where:
        story | _
        "sample/Web.story"                      | _
        "sample/WebGivenStoryUsage.story"       | _
        "sample/WebProperty.story"              | _
        "sample/WebWaitCondition.story"         | _
        "sample/WebAction.story"                | _
        "sample/WebNavigation.story"            | _
    }



}
