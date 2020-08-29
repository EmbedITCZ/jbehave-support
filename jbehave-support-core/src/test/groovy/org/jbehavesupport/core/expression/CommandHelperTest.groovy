package org.jbehavesupport.core.expression


import org.jbehavesupport.core.internal.expression.CommandHelper
import spock.lang.Specification
import spock.lang.Unroll

class CommandHelperTest extends Specification {

    @Unroll
    def "Test command name for wrong expression: #expression"() {
        when:
        CommandHelper.commandName(expression)

        then:
        def e = thrown(exception)

        where:
        expression || exception
        null       || NullPointerException
        ":"        || IllegalArgumentException
    }

    @Unroll
    def "Test command name for expression: #expression"() {
        when:
        def command = CommandHelper.commandName(expression)

        then:
        command == expected

        where:
        expression || expected
        ""         || ""
        "test"     || "test"
        "test:0"   || "test"
        "test:0:0" || "test"
    }

    @Unroll
    def "Test command parameters for wrong expression: #expression"() {
        when:
        CommandHelper.commandParams(expression)

        then:
        def e = thrown(exception)

        where:
        expression || exception
        null       || NullPointerException
        ""         || IllegalArgumentException
    }

    @Unroll
    def "Test command parameters for expression: #expression"() {
        when:
        def params = CommandHelper.commandParams(expression)

        then:
        params == expected

        where:
        expression                        || expected
        "test"                            || []
        "test:0"                          || ["0"]
        "test:0:1"                        || ["0", "1"]

        "keep:'A'"                        || ["A"]
        "keep:'A:B'"                      || ["A:B"]
        "keep:0:'A:B'"                    || ["0", "A:B"]
        "keep:0:'A:B:C:D'"                || ["0", "A:B:C:D"]
        "keep:0:'A:B:C:D':1"              || ["0", "A:B:C:D", "1"]
        "keep:0:'A:B:C:D':1:2"            || ["0", "A:B:C:D", "1", "2"]
        "do_not_keep:\\'0:1\\'"           || ["\\'0", "1\\'"]

        "keep_more:'A':'B'"               || ["A", "B"]
        "keep_more:'A:B':C"               || ["A:B", "C"]
        "keep_more:'A:B':'C'"             || ["A:B", "C"]
        "keep_more:0:'A:B':'C'"           || ["0", "A:B", "C"]
        "keep_more:0:'A:B:C:D':'E:F'"     || ["0", "A:B:C:D", "E:F"]
        "keep_more:0:'A:B:C:D':'E:F':1"   || ["0", "A:B:C:D", "E:F", "1"]
        "keep_more:0:'A:B:C:D':'E:F':1:2" || ["0", "A:B:C:D", "E:F", "1", "2"]
        "keep_more:'A:B':'C:D':'E:F'"     || ["A:B", "C:D", "E:F"]

        "empty:"                          || []
        "empty::"                         || []
        "empty:::"                        || []
        "empty::A"                        || ["", "A"]
        "empty::A:"                       || ["", "A"]
        "empty:A:"                        || ["A"]
        "empty:A::"                       || ["A"]
    }

    @Unroll
    def "Test wrong params for checkNumericParams: #params"() {
        when:
        CommandHelper.checkNumericParams(*params)

        then:
        Exception exception = thrown()
        expected == exception.class
        message == exception.message

        where:
        params       || expected                       || message
        ["a", "1"]   || IllegalArgumentException.class || "String parameter must be numeric: a"
        ["1,1", "1"] || IllegalArgumentException.class || "String parameter must be numeric: 1,1"
    }
}
