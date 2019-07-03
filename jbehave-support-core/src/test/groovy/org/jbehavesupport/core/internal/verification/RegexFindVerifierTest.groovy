package org.jbehavesupport.core.internal.verification

import org.jbehavesupport.core.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(classes = TestConfig)
class RegexFindVerifierTest extends Specification {

    @Autowired
    RegexFindVerifier regexFindVerifier;

    def "Name"() {
        expect:
        regexFindVerifier.name().equals("REGEX_FIND")
    }

    @Unroll
    "VerifyPositive #actual to #expected"() {
        when:
        regexFindVerifier.verify(actual, expected)

        then:
        true

        where:
        actual     | expected
        "XXtestXX" | ".*test.*"
        "test"     | ".*test.*"
        "te45st"   | ".*\\d+.*"
        "testdata" | "e[stuv]t"

    }

    @Unroll
    "VerifyNegative #actual to #expected"() {
        when:
        regexFindVerifier.verify(actual, expected)

        then:
        def exception = thrown(Throwable)
        exception.getMessage() == message

        where:
        actual  | expected || message
        null    | "asd"    || "Actual value must be provided"
        "asd"   | null     || "Expected value must be provided"
        "wrong" | ".*good" || "regex '.*good' wasn't found in: \nwrong"
    }


}
