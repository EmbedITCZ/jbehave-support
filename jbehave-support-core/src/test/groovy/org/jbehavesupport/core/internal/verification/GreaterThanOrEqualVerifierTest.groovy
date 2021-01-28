package org.jbehavesupport.core.internal.verification

import org.jbehavesupport.core.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

@ContextConfiguration(classes = TestConfig)
class GreaterThanOrEqualVerifierTest extends Specification {

    @Autowired
    GreaterThanOrEqualVerifier greaterThanOrEqualVerifier

    def "Name"() {

        expect:
        greaterThanOrEqualVerifier.name() == "GE"
    }

    @Unroll
    "VerifyPositive #actual to #expected"() {
        when:
        greaterThanOrEqualVerifier.verify(actual, expected)

        then:
        true

        where:
        actual                     | expected
        20                         | 7
        20                         | 20
        456.789                    | 132.456
        new LocalDate(2002, 7, 15) | new LocalDate(2002, 7, 14)
        new LocalDate(2002, 7, 15) | new LocalDate(2002, 7, 15)
        new LocalDate(2002, 7, 15) | "2000-06-14"
    }

    @Unroll
    "VerifyNegative #actual to #expected"() {
        when:
        greaterThanOrEqualVerifier.verify(actual, expected)

        then:
        def thr = thrown(Throwable)
        thr.getMessage() == message

        where:
        actual                     | expected                   || message
        7                          | 22                         || "value '7' is not greater than or equal to '22'"
        1.000001                   | 2.000002                   || "value '1.000001' is not greater than or equal to '2.000002'"
        new LocalDate(2002, 7, 15) | "2003-01-01"               || "value '2002-07-15' is not greater than or equal to '2003-01-01'"
    }

}
