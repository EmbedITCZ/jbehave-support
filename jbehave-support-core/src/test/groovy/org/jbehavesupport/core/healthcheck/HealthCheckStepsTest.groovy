package org.jbehavesupport.core.healthcheck


import org.jbehave.core.model.ExamplesTable
import org.jbehavesupport.core.TestConfig
import org.junit.runners.model.MultipleFailureException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static groovy.test.GroovyAssert.shouldFail

@ContextConfiguration(classes = TestConfig)
class HealthCheckStepsTest extends Specification {

    @Autowired
    HealthCheckSteps healthCheckSteps

    def "CheckComponentsAreHealthy"() {

        expect:
        ExamplesTable examplesTable = new ExamplesTable(
                "| component | \n" +
                "| HEALTHY   |")
        healthCheckSteps.checkComponentsAreHealthy(examplesTable)
    }

    def "CheckComponentsAreSick"() {

        when:
        ExamplesTable examplesTable = new ExamplesTable(
                "| component | \n" +
                "| SICK      |")
        MultipleFailureException fail = shouldFail(MultipleFailureException.class) {
            healthCheckSteps.checkComponentsAreHealthy(examplesTable)
        }

        then:
        fail.getFailures().size() == 1
        fail.getFailures().get(0).message.contains("I am very sick")
    }

    def "should miss bean"() {

        when:
        ExamplesTable examplesTable = new ExamplesTable(
                "| component | \n" +
                "| MISS      |")
        def fail = shouldFail(IllegalArgumentException.class) {
            healthCheckSteps.checkComponentsAreHealthy(examplesTable)
        }

        then:
        fail.getMessage() == "HealthCheckSteps requires single HealthCheck bean with qualifier [MISS]"
    }
}
