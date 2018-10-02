package org.jbehavesupport.core.internal.expression

import spock.lang.Specification

class RandomEmailCommandTest extends Specification {

    def "Execute correct mail"() {

        when:
        RandomEmailCommand rec = new RandomEmailCommand()
        String mail = rec.execute()

        then:
        mail.contains("@") && mail.contains(".")
    }

    def "Execute wrong arguments"() {

        when:
        RandomEmailCommand rec = new RandomEmailCommand()
        String mail = rec.execute("my@mail.org")

        then:
        thrown(IllegalArgumentException)

    }
}
