package org.jbehavesupport.core.sql

import org.jbehavesupport.test.support.TestSupport
import org.junit.runner.JUnitCore
import spock.lang.Shared
import spock.lang.Specification

class BackUpUpdateIT extends Specification implements TestSupport {
    @Shared
        runner = new JUnitCore()

    def "keyAlreadyUsed"() {
        when:
        def result = runner.run(runWith("sql/BackUpUpdateKeyAlreadyUsed.story"))

        then:
        result.failures.stream().anyMatch({ e -> e.exception.message.contains("Key [DELETE_LUCIFER] already exist.") })

    }

    def "backUpUpdateFails"() {
        when:
        def result = runner.run(runWith("sql/BackUpUpdateKeyFails.story"))

        then:
        result.failures[0].exception.cause.errors.stream().anyMatch({ e -> e.contains("Table \"PERSONA\" not found") })
        result.failures[0].exception.cause.errors.stream().anyMatch({ e -> e.contains("Table \"PERSONEL\" not found") })

    }

}
