package org.jbehavesupport.core.internal.verification

import org.jbehavesupport.core.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

@ContextConfiguration(classes = TestConfig)
class GreaterThanVerifierTest extends Specification {

    @Autowired
    GreaterThanVerifier greaterThanVerifier

    def "Name"() {

        expect:
        greaterThanVerifier.name() == "GT"
    }

    @Unroll
    "VerifyPositive #actual to #expected"() {
        when:
        greaterThanVerifier.verify(actual, expected)

        then:
        true

        where:
        actual                     | expected
        20                         | 7
        456.789                    | 132.456
        LocalDate.of(2002, 7, 15)  | LocalDate.of(2002, 7, 14)
        LocalDate.of(2002, 7, 15)  | "2000-06-14"
    }


    @Unroll
    "VerifyNegative #actual to #expected"() {
        when:
        greaterThanVerifier.verify(actual, expected)

        then:
        def thr = thrown(Throwable)
        thr.getMessage() == message

        where:
        actual                     | expected                   || message
        7                          | 22                         || "value '7' is not greater than '22'"
        7                          | 7                          || "value '7' is not greater than '7'"
        1.000001                   | 2.000002                   || "value '1.000001' is not greater than '2.000002'"
        LocalDate.of(2002, 7, 15)  | LocalDate.of(2002, 7, 15)  || "value '2002-07-15' is not greater than '2002-07-15'"
        LocalDate.of(2002, 7, 15)  | "2003-01-01"               || "value '2002-07-15' is not greater than '2003-01-01'"
    }
}
