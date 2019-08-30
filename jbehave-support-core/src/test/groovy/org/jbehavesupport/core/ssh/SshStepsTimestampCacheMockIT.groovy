package org.jbehavesupport.core.ssh

import org.jbehavesupport.core.TestConfig
import org.jbehavesupport.core.expression.ExpressionEvaluatingParameter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import java.time.ZonedDateTime

@ContextConfiguration(classes = TestConfig)
class SshStepsTimestampCacheMockIT extends Specification {

    @Autowired
    SshSteps sshSteps

    def "test cache logContainsData"() {
        given:
        def start = ZonedDateTime.now()
        def end = start.plusSeconds(5)
        def cachedTable =  "| header                | verifier     | \n" +
                           "| Log is from cache     | CONTAINS     | \n" +
                           "| New log without cache | NOT_CONTAINS | \n"

        def fetchedTable = "| header                | verifier     | \n" +
                           "| New log without cache | CONTAINS     | \n" +
                           "| Log is from cache     | NOT_CONTAINS | \n"

        when:
        sshSteps.saveLogStartTimeOnSaved(new ExpressionEvaluatingParameter<String>(start.toString()))
        sshSteps.setLogEndTimeOnSaved(new ExpressionEvaluatingParameter<String>(end.toString()))
        sshSteps.logContainsData("MOCK_TEMPLATE", cachedTable)
        sshSteps.logContainsData("MOCK_TEMPLATE", cachedTable)
        sshSteps.setLogEndTimeOnSaved(new ExpressionEvaluatingParameter<String>(end.plusSeconds(5).toString()))
        sshSteps.logContainsData("MOCK_TEMPLATE", fetchedTable)

        then:
        noExceptionThrown()
    }
}

