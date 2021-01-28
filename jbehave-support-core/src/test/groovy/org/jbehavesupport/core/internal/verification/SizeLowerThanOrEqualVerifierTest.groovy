package org.jbehavesupport.core.internal.verification

import org.jbehavesupport.core.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(classes = TestConfig)
class SizeLowerThanOrEqualVerifierTest extends Specification {

    @Autowired
    SizeLowerThanOrEqualVerifier sizeLowerThanOrEqualVerifier

    def "Name"() {
        expect:
        sizeLowerThanOrEqualVerifier.name() == "SIZE_LE"
    }

    @Unroll
    "VerifyPositive #actual to #expected"() {
        when:
        sizeLowerThanOrEqualVerifier.verify(actual, expected)

        then:
        true

        where:
        actual     | expected
        [1]        | 2
        ["a", "b"] | 3
        ["a", "b"] | 2
        ["1", "2"] | "4"
        ["1", "2"] | "2"

    }

    @Unroll
    "VerifyNegative #actual to #expected"() {
        when:
        sizeLowerThanOrEqualVerifier.verify(actual, expected)

        then:
        def exception = thrown(Throwable)
        exception.getMessage() == message

        where:
        actual    | expected || message
        null      | 0        || "Actual value must be provided."
        []        | null     || "Expected value must be provided."
        [1, 2, 3] | 0        || "Collection size 3 is not lower than or equal to expected: 0."
        "input"   | 0        || "Object of class [java.lang.String] must be an instance of interface java.util.Collection"
        ["a"]     | "one"    || "Expected value must be numeric"
    }

}
