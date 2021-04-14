package org.jbehavesupport.core.internal.verification

import org.jbehavesupport.core.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

@ContextConfiguration(classes = TestConfig)
class EqualsVerifierTest extends Specification {

    @Autowired
    EqualsVerifier equalsVerifier

    def "Name"() {
        expect:
        equalsVerifier.name() == "EQ"
    }

    @Unroll
    "VerifyPositive #actual to #expected"() {
        when:
        equalsVerifier.verify(actual, expected)

        then:
        true

        where:
        actual                     | expected
        ""                         | ""
        null                       | null
        "tst"                      | "tst"
        LocalDate.of(2002, 7, 14)  | LocalDate.of(2002, 7, 14)
        12                         | 12
        [1,2,3,4] as int[]         | [1,2,3,4] as int[]
    }

    @Unroll
    "VerifyNegative #actual to #expected"() {
        when:
        equalsVerifier.verify(actual, expected)

        then:
        def exception = thrown(Throwable)
        exception.getMessage().contains(message)

        where:
        actual                     | expected                   || message
        LocalDate.of(2002, 7, 15)  | LocalDate.of(2002, 7, 14)  || "value '2002-07-15' is not equal to '2002-07-14'"
        7                          | 9                          || "value '7' is not equal to '9'"
        "me"                       | "you"                      || "value 'me' is not equal to 'you'"
        null                       | "we"                       || "Actual value must be provided"
        "they"                     | null                       || "value 'they' is not equal to 'null'"
        [1,2,3,4] as int[]         | [4,3,2,1] as int[]         || "is not equal to"
    }
}
