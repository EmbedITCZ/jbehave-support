package org.jbehavesupport.core.expression


import org.jbehavesupport.core.TestConfig
import org.jbehavesupport.core.internal.expression.FileCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@ContextConfiguration(classes = TestConfig.class)
class FileCommandTest extends Specification {

    @Autowired
    FileCommand fileCommand

    def "File command evaluation for #params with result #expected"() {

        when:
        def actual = fileCommand.execute(*params)

        then:
        actual.toString().contains(expected)

        where:
        params                                                                 || expected
        ["org/jbehavesupport/core/expression/FileCommandTest.class"]           || "FileCommandTest.class"
        ["org/jbehavesupport/core/expression/FileCommandTest.class", "myFile"] || "myFile"
    }

    def "File command evaluation for #params should throw #expected"() {
        when:
        fileCommand.execute(*params)

        then:
        Exception exception = thrown()
        expected == exception.class

        where:
        params                                   || expected
        ["org/jbehavesupport/core/doesNotExist"] || IllegalStateException.class
    }

}
