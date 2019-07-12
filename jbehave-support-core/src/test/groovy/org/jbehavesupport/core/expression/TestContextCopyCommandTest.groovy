package org.jbehavesupport.core.expression


import org.jbehavesupport.core.internal.TestContextImpl
import org.jbehavesupport.core.internal.expression.NullCommand
import org.jbehavesupport.core.internal.expression.TestContextCopyCommand
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class TestContextCopyCommandTest extends Specification {

    def "test execute with #params returns #expected"() {
        setup:
        def testContext = new TestContextImpl()
        testContext.put("test", "TEST")
        testContext.put("nullTest", null)

        when:
        def result = new TestContextCopyCommand(testContext).execute(*params)

        then:
        result == expected

        where:
        params          || expected
        ["test"]        || "TEST"
        ["test", "new"] || "newTEST"
        ["nullTest"]    || NullCommand.NULL_VALUE
    }

    def "test argument exception #expected"() {
        setup:
        def testContext = new TestContextImpl()
        testContext.put("test", "TEST")

        when:
        new TestContextCopyCommand().execute(*params)

        then:
        Exception exception = thrown()
        expected == exception.class

        where:
        params         || expected
        [1]            || IllegalArgumentException.class
        ["test", 1, 2] || IllegalArgumentException.class
        ["1", "1", 2]  || IllegalArgumentException.class
    }
}
