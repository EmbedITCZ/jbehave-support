package org.jbehavesupport.core.internal.verification

import org.jbehavesupport.core.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll


@ContextConfiguration(classes = TestConfig)
class MathEqualsVerifierTest extends Specification {

    @Autowired
    MathEqualsVerifier mathEqualsVerifier

    def "Name"() {
        expect:
        mathEqualsVerifier.name() == "MATH_EQ"
    }

    @Unroll
    "VerifyPositive #actual to #expected"() {
        when:
        mathEqualsVerifier.verify(actual, expected)

        then:
        true

        where:
        actual     | expected
        20         | 20
        456.789    | 456.789
        "1.234E3"  | 1234
        "-1.234E3" | "-1234"
        1.234E+3   | "1234"
        -1.234E+3  | -1234
    }

    @Unroll
    "VerifyNegative #actual to #expected"() {
        when:
        mathEqualsVerifier.verify(actual, expected)

        then:
        def thr = thrown(Throwable)
        thr.getMessage() == message

        where:
        actual             | expected      || message
        7                  | 22            || "value 7.0 (originally written as '7') is not mathematically equal to 22.0 (originally written as '22')"
        1.000001           | 2.000002      || "value 1.000001 (originally written as '1.000001') is not mathematically equal to 2.000002 (originally written as '2.000002')"
        1.2E+3             | 1201          || "value 1200.0 (originally written as '1.2E+3') is not mathematically equal to 1201.0 (originally written as '1201')"
        -1.2E+3            | 1200          || "value -1200.0 (originally written as '-1.2E+3') is not mathematically equal to 1200.0 (originally written as '1200')"
        "two"              | 2             || "couldn't parse either or both value/-s to a number: 'two', '2'"
        "Anakin Skywalker" | "Darth Vader" || "couldn't parse either or both value/-s to a number: 'Anakin Skywalker', 'Darth Vader'"
    }

}
