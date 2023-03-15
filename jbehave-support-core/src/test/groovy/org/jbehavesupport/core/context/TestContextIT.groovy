package org.jbehavesupport.core.context

import spock.lang.Specification

import org.jbehavesupport.test.support.TestSupport

class TestContextIT extends Specification implements TestSupport {

    def "Test failing context"() {
        when:
        def result = run(runWith("context/DataNotInContext.story"))

        then:
        result.failures.stream().anyMatch({ e -> e.exception.message.contains("Context must contains [test dat] under key [TEST_DATA]") })
    }

    def "Test typo in context data copy"() {
        when:
        def result = run(runWith("context/TypoInCopyContextData.story"))

        then:
        result.failures.stream().anyMatch({ e -> e.exception.message.contains("Test context doesn't contain key: TTEST_DATA_FOR_CP") })
    }

    def "Test context"() {
        when:
        def result = run(runWith("context/Context.story"))

        then:
        result.totalFailureCount == 0
    }

    def "Clear test context before each scenario"() {
        when:
        def result = run(runWith("context/ImplicitContextClearing.story"))

        then:
        result.totalFailureCount == 0
    }
}
