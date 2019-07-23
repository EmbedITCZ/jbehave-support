package org.jbehavesupport.core.context

import spock.lang.Specification

class ContextStepsTest extends Specification {

    def "LoadDataFromResource negative"() {

        when:
        new ContextSteps().loadDataFromResource("notYaml")

        then:
        def e = thrown(IllegalArgumentException)
        e.getMessage() == "Only yml extension is supported"
    }
}
