package org.jbehavesupport.core.internal.verification

import org.jbehavesupport.core.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(classes = TestConfig)
class SizeEqualsVerifierTest extends Specification {

    @Autowired
    SizeEqualsVerifier sizeEqualsVerifier;

    def "Name"() {
        expect:
        sizeEqualsVerifier.name().equals("SIZE_EQ");
    }

    @Unroll
    "VerifyPositive #actual to #expected"() {
        when:
        sizeEqualsVerifier.verify(actual, expected);

        then:
        true

        where:
        actual     | expected
        [1]        | 1
        []         | 0
        ["a", "b"] | 2
        ["a"]      | "1"

    }

    @Unroll
    "VerifyNegative #actual to #expected"() {
        when:
        sizeEqualsVerifier.verify(actual, expected);

        then:
        def exception = thrown(Throwable)
        exception.getMessage() == message

        where:
        actual  | expected || message
        null    | 0        || "Actual value must be provided."
        []      | null     || "Expected value must be provided."
        []      | 9        || "Collection has 0 elements, but 9 was expected."
        [1]     | 0        || "Collection has 1 elements, but 0 was expected."
        "input" | 0        || "Object of class [java.lang.String] must be an instance of interface java.util.Collection"
        ["a"]   | "one"    || "Expected value must be numeric"
    }
}
