package org.jbehavesupport.core.internal.verification

import org.jbehavesupport.core.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

@ContextConfiguration(classes = TestConfig)
class NotEqualsVerifierTest extends Specification {

    @Autowired
    NotEqualsVerifier notEgualsVerifier;

    def "Name"() {
        expect:
        notEgualsVerifier.name().equals("NE");
    }

    @Unroll
    "VerifyPositive #actual to #expected"() {
        when:
        notEgualsVerifier.verify(actual, expected);

        then:
        true

        where:
        actual                     | expected
        new LocalDate(2002, 7, 15) | new LocalDate(2002, 7, 14)
        7                          | 9
        "me"                       | "you"
    }

    @Unroll
    "VerifyNegative #actual to #expected"() {
        when:
        notEgualsVerifier.verify(actual, expected);

        then:
        def exception = thrown(Throwable)
        exception.getMessage() == message

        where:
        actual                     | expected                   || message
        ""                         | ""                         || "'' must be different from ''"
        null                       | null                       || "'null' must be different from 'null'"
        "tst"                      | "tst"                      || "'tst' must be different from 'tst'"
        new LocalDate(2002, 7, 14) | new LocalDate(2002, 7, 14) || "'2002-07-14' must be different from '2002-07-14'"
        null                       | "asd"                      || "Actual value must be provided"
    }
}
