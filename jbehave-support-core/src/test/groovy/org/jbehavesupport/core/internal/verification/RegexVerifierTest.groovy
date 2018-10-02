package org.jbehavesupport.core.internal.verification

import org.jbehavesupport.core.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(classes = TestConfig)
class RegexVerifierTest extends Specification {

    @Autowired
    RegexVerifier regexVerifier;

    def "Name"() {
        expect:
        regexVerifier.name().equals("REGEX_MATCH");
    }

    @Unroll
    "VerifyPositive #actual to #expected"() {
        when:
        regexVerifier.verify(actual, expected);

        then:
        true

        where:
        actual     | expected
        "XXtestXX" | ".*test.*"
        "test"     | ".*test.*"
        "te45st"   | ".*\\d+.*"

    }

    @Unroll
    "VerifyNegative #actual to #expected"() {
        when:
        regexVerifier.verify(actual, expected);

        then:
        def exception = thrown(Throwable)
        exception.getMessage() == message

        where:
        actual  | expected || message
        null    | "asd"    || "Actual value must be provided"
        "wrong" | ".*good" || "'wrong' does not match on regex: .*good"
    }


}
