package org.jbehavesupport.core.ssh

import org.jbehavesupport.core.TestConfig
import org.jbehavesupport.core.TestContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import java.time.ZonedDateTime

@ContextConfiguration(classes = TestConfig)
class SshStepsIT extends Specification {

    @Autowired
    SshSteps sshSteps

    @Autowired
    TestContext testContext

    def "test soft assertions in logContainsData"() {
        given:

        def table = "| header               | \n" +
                    "| invalidValue         | \n" +
                    "| anotherInvalidValue  | "

        testContext.put("START_TIME", ZonedDateTime.now())

        when:
        sshSteps.logContainsData("TEST", "START_TIME", table)

        then:
        def throwable = thrown(AssertionError)
        throwable.getMessage().contains("The following 2 assertions failed")
    }
}
