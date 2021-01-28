package org.jbehavesupport.core.internal.verification

import org.jbehavesupport.core.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(classes = TestConfig)
class SizeGreaterThanOrEqualVerifierTest extends Specification {

    @Autowired
    SizeGreaterThanOrEqualVerifier sizeGreaterThanOrEqualVerifier

    def "Name"() {
        expect:
        sizeGreaterThanOrEqualVerifier.name() == "SIZE_GE"
    }

    @Unroll
    "VerifyPositive #actual to #expected"() {
        when:
        sizeGreaterThanOrEqualVerifier.verify(actual, expected)

        then:
        true

        where:
        actual     | expected
        [1]        | 0
        ["a", "b"] | 1
        ["a", "b"] | 2
        ["1", "2"] | "1"
        ["1", "2"] | "2"

    }

    @Unroll
    "VerifyNegative #actual to #expected"() {
        when:
        sizeGreaterThanOrEqualVerifier.verify(actual, expected)

        then:
        def exception = thrown(Throwable)
        exception.getMessage() == message

        where:
        actual  | expected || message
        null    | 0        || "Actual value must be provided."
        []      | null     || "Expected value must be provided."
        []      | 9        || "Collection size 0 is not greater than or equal to expected: 9."
        "input" | 0        || "Object of class [java.lang.String] must be an instance of interface java.util.Collection"
        ["a"]   | "one"    || "Expected value must be numeric"
    }

}
