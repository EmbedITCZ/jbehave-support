package org.jbehavesupport.core.internal

import spock.lang.Specification
import spock.lang.Unroll

class TableParameterAlignerTest extends Specification {

    @Unroll
    def "output of table parameter aligner for #input is #expected"() {
        when:
        def output = TableParameterAligner.alignTableInString(input)

        then:
        output == expected

        where:
        input                 | expected
        ""                    | ""
        "|a|"                 | "|a|"
        "|    b     |"        | "|b|"
        "|a|aa|\n|b|b|"       | "|a|aa|\n|b|b |"
        "|a|\n|b|"            | "|a|\n|b|"
        "|  a  |\n|b|"        | "|a|\n|b|"
        "|   a   |\n|  b   |" | "|a|\n|b|"
        "|a|\n|--b|"          | "|a|\n|--b|"
        "|a|        |"        | "|a||"
        "|a|b|\n||b|"         | "|a|b|\n| |b|"
    }
}
