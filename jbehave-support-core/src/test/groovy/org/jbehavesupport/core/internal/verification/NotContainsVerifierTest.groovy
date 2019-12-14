package org.jbehavesupport.core.internal.verification

import org.jbehavesupport.core.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(classes = TestConfig)
class NotContainsVerifierTest extends Specification {

    @Autowired
    NotContainsVerifier notContainsVerifier

    def "Name"() {
        expect:
        notContainsVerifier.name() == "NOT_CONTAINS"
    }

    @Unroll
    "VerifyPositive #actual to #expected"() {
        when:
        notContainsVerifier.verify(actual, expected)

        then:
        true

        where:
        actual   | expected
        ""       | "aa"
        "QWERTY" | "bb"
        "asd"    | "22"
    }

    @Unroll
    "VerifyNegative #actual to #expected"() {
        when:
        notContainsVerifier.verify(actual, expected)

        then:
        def exception = thrown(Throwable)
        exception.getMessage() == message

        where:
        actual   | expected || message
        ""       | ""       || "'' must not contain ''"
        "QWERTY" | "QWE"    || "'QWERTY' must not contain 'QWE'"
        null     | "asd"    || "Can not execute NOT_CONTAINS verification with null value"
    }
}
