package org.jbehavesupport.core.internal.verification

import org.jbehavesupport.core.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(classes = TestConfig)
class SizeLowerThanVerifierTest extends Specification {

    @Autowired
    SizeLowerThanVerifier sizeLowerThanVerifier

    def "Name"() {
        expect:
        sizeLowerThanVerifier.name().equals("SIZE_LT")
    }

    @Unroll
    "VerifyPositive #actual to #expected"() {
        when:
        sizeLowerThanVerifier.verify(actual, expected)

        then:
        true

        where:
        actual     | expected
        [1]        | 2
        ["a", "b"] | 3
        ["1", "2"] | "4"

    }

    @Unroll
    "VerifyNegative #actual to #expected"() {
        when:
        sizeLowerThanVerifier.verify(actual, expected)

        then:
        def exception = thrown(Throwable)
        exception.getMessage() == message

        where:
        actual    | expected || message
        null      | 0        || "Actual value must be provided."
        []        | null     || "Expected value must be provided."
        [1, 2, 3] | 0        || "Collection size 3 is not lower than expected: 0."
        []        | 0        || "Collection size 0 is not lower than expected: 0."
        "input"   | 0        || "Object of class [java.lang.String] must be an instance of interface java.util.Collection"
        ["a"]     | "one"    || "Expected value must be numeric"
    }
}
