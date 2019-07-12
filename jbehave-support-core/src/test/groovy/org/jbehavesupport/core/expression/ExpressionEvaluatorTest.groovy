package org.jbehavesupport.core.expression


import org.jbehavesupport.core.TestConfig
import org.jbehavesupport.core.TestContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

@ContextConfiguration(classes = TestConfig)
class ExpressionEvaluatorTest extends Specification {

    @Autowired
    ExpressionEvaluator expressionEvaluator

    @Autowired
    TestContext testContext

    @Unroll
    "Test evaluating #command should result to string with length #expected"() {
        when:
        String actual = expressionEvaluator.evaluate(command)

        then:
        actual.length() == expected

        where:
        command              || expected
        "{RANDOM_NUMBER:10}" || 10
    }

    @Unroll
    "Test evaluating {#command} should yield '#expected'"() {
        when:
        def actual = expressionEvaluator.evaluate(command)

        then:
        actual == expected

        where:
        command                                      || expected
        "{CONCAT:12: :345}"                          || "12 345"
        "{CONCAT: : }"                               || "  "
        "{DATE_PARSE:05/20/2031:MM/dd/yyyy}"         || LocalDate.of(2031, 05, 20)
        "{DP:05/20/2031:MM/dd/yyyy}"                 || LocalDate.of(2031, 05, 20)
        "{FORMAT_DATE:2031-05-20:MM/dd/yyyy}"        || "05/20/2031"
        "{FD:2031-05-20:MM/dd/yyyy}"                 || "05/20/2031"
        "{UPPER_CASE:abcdEFgh12}"                    || "ABCDEFGH12"
        "{UC:abcdEFgh12}"                            || "ABCDEFGH12"
        "{LOWER_CASE:ABcdEFgh12}"                    || "abcdefgh12"
        "{LC:ABcdEFgh12}"                            || "abcdefgh12"
        "{SUBSTR:abcdefgh:4}"                        || "efgh"
        "{SUBSTR:abc:3}"                             || ""
        "{SUBSTR:ABcdEFgh12:2:5}"                    || "cdE"
        "{SUBSTR:ABcdEFgh12:0:5}"                    || "ABcdE"
        "{PLUS:1.1:1.1}"                             || BigDecimal.valueOf(2.2)
        "{MAP:0:[0,Zero],[1,One]}"                   || "Zero"
        "{MAP:1:[0,Zero],[1,One]}"                   || "One"
        "{MAP:0:[0,Zero],[1,One],[Unknown]}"         || "Zero"
        "{MAP:1:[0,Zero],[1,One],[Unknown]}"         || "One"
        "{MAP:2:[0,Zero],[1,One],[Unknown]}"         || "Unknown"
        "{MAP:2:[0,Zero],[1,One],[2,Two],[Unknown]}" || "Two"
        "{MAP:null:[null,],[Unknown]}"               || ""
        "{MAP::[,null],[not null]}"                  || "null"
        "{MAP:1:[,null],[not null]}"                 || "not null"
//        NullCommand.NOT_APPLICABLE                   || null
    }

    @Unroll
    "Test evaluating {#command} with special characters should yield '#expected'"() {
        when:
        def actual = expressionEvaluator.evaluate(command)

        then:
        actual == expected

        where:
        command                        || expected
        '{CONCAT:12:\\::345}'          || "12:345"
        '{CONCAT:\\{Hello:, world\\}}' || "{Hello, world}"
        "{CONCAT:We:\\'re}"            || "We're"
    }

    @Unroll
    "Test evaluating {#command} finished with exception #exceptionClass"() {
        when:
        expressionEvaluator.evaluate(command)

        then:
        Exception exception = thrown()
        exception.class == exceptionClass

        where:
        command                   || exceptionClass
        "{SUBSTR:ABcdEFgh12:2:1}" || StringIndexOutOfBoundsException.class
        "{SUBSTR:abc:4}"          || StringIndexOutOfBoundsException.class
        "{SUBSTR:abc:2:4}"        || StringIndexOutOfBoundsException.class
        "{PLUS:a:1}"              || IllegalArgumentException.class
    }

    @Unroll
    "Test evaluating {#command} with special characters read from test context should yield '#expected'"() {
        given:
        testContext.put("UNESCAPED_STRING", "{x:y}")

        when:
        def actual = expressionEvaluator.evaluate(command)

        then:
        actual == expected

        where:
        command                 || expected
        '{CP:UNESCAPED_STRING}' || "{x:y}"
    }

    @Unroll
    "Test evaluating multiline expression"() {
        given:
        def multilineString = "Multi-line string with {UC:upper1}\n" +
            "and {UC:upper2} strings"

        when:
        def actual = expressionEvaluator.evaluate(multilineString)

        then:
        actual == expected

        where:
        command                                                       || expected
        "Multi-line string with {UC:upper1}\nand {UC:upper2} strings" || "Multi-line string with UPPER1\nand UPPER2 strings"
    }
}
