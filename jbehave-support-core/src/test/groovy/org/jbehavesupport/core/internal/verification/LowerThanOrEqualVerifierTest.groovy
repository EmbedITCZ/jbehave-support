package org.jbehavesupport.core.internal.verification

import org.jbehavesupport.core.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

@ContextConfiguration(classes = TestConfig)
class LowerThanOrEqualVerifierTest extends Specification {

    @Autowired
    LowerThanOrEqualVerifier lowerThanOrEqualVerifier

    def "Name"() {
        expect:
        lowerThanOrEqualVerifier.name() == "LE"
    }

    @Unroll
    "Verify positive #actual to #expected"() {
        when:
        lowerThanOrEqualVerifier.verify(actual, expected)

        then:
        true

        where:
        actual                     | expected
        9                          | 13
        22                         | 22
        3.88                       | 4.11
        LocalDate.of(2002, 7, 14)  | LocalDate.of(2002, 7, 15)
        LocalDate.of(2002, 7, 15)  | LocalDate.of(2002, 7, 15)
        LocalDate.of(2002, 7, 14)  | "2002-08-14"
    }

    @Unroll
    "Verify negative #actual to #expected"() {
        when:
        lowerThanOrEqualVerifier.verify(actual, expected)

        then:
        def thr = thrown(Throwable)
        thr.getMessage() == message

        where:
        actual                     | expected                   || message
        22                         | 7                          || "value '22' is not lower than or equal to '7'"
        100000.1                   | 0.000001                   || "value '100000.1' is not lower than or equal to '0.000001'"
        LocalDate.of(2002, 7, 15)  | "2000-01-01"               || "value '2002-07-15' is not lower than or equal to '2000-01-01'"
    }

}
