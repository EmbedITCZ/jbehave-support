package org.jbehavesupport.core.internal.verification

import org.jbehavesupport.core.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(classes = TestConfig)
class ContainsVerifierTest extends Specification {

    @Autowired
    ContainsVerifier containsVerifier

    def "Name"() {
        expect:
        containsVerifier.name().equals("CONTAINS")
    }

    @Unroll
    "VerifyPositive #actual to #expected"() {
        when:
        containsVerifier.verify(actual, expected)

        then:
        true

        where:
        actual   | expected
        ""       | ""
        "QWERTY" | "QWE"
    }

    @Unroll
    "VerifyNegative #actual to #expected"() {
        when:
        containsVerifier.verify(actual, expected)

        then:
        def exception = thrown(Throwable)
        exception.getMessage() == message

        where:
        actual   | expected || message
        ""       | "aa"     || "'' must contain 'aa'"
        "QWERTY" | "bb"     || "'QWERTY' must contain 'bb'"
        null     | null     || "Expected value must be provided"
        "cc"     | null     || "Expected value must be provided"
        null     | "dd"     || "Can not execute CONTAINS verification with null value"
    }
}
