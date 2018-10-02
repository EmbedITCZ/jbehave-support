package org.jbehavesupport.core.expression

import spock.lang.Specification

class ExpressionEvaluatingParameterTest extends Specification {
    def "GetValue"() {
        when:
        def expEvalParameter = new ExpressionEvaluatingParameter<String>(value)

        then:
        expected == expEvalParameter.getValue()

        where:
        value || expected
        "hi"  || "hi"
        null  || null
        ""    || ""
    }

    def "Equals"() {
        when:
        def expEvalParameter = new ExpressionEvaluatingParameter<String>(value)

        then:
        expected == expEvalParameter

        where:
        value || expected
        "hi"  || new ExpressionEvaluatingParameter<String>("hi")
        null  || new ExpressionEvaluatingParameter<String>(null)
        ""    || new ExpressionEvaluatingParameter<String>("")
    }

    def "HashCode"() {
        when:
        def expEvalParameter = new ExpressionEvaluatingParameter<String>(value)

        then:
        expected == expEvalParameter.hashCode()

        where:
        value || expected
        "hi"  || new ExpressionEvaluatingParameter<String>("hi").hashCode()
        null  || new ExpressionEvaluatingParameter<String>(null).hashCode()
        ""    || new ExpressionEvaluatingParameter<String>("").hashCode()
    }

    def "ToString"() {
        when:
        def expEvalParameter = new ExpressionEvaluatingParameter<String>(value)

        then:
        expected == expEvalParameter.toString()

        where:
        value || expected
        "hi"  || "ExpressionEvaluatingParameter(value=hi)"
        null  || "ExpressionEvaluatingParameter(value=null)"
        ""    || "ExpressionEvaluatingParameter(value=)"
    }
}
