package org.jbehavesupport.core.internal.web

import org.jbehavesupport.core.TestConfig
import org.jbehavesupport.core.web.WebActionBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static org.apache.commons.lang3.StringUtils.deleteWhitespace

@ContextConfiguration(classes = TestConfig)
class WebActionBuilderTest extends Specification {

    @Autowired
    WebActionBuilder actionBuilder

    def "should build general action table"() {
        given:
        def b = actionBuilder.builder()

        when:
        b.on("a1").perform("b1").value("c1").alias("d1").and()
            .on("a2").perform("b2").value("c2").alias("d2")

        then:
        deleteWhitespace(b.buildExamplesTable().asString()) ==
            deleteWhitespace("""|element|action|value|alias|
                                |a1     |b1    |c1   |d1   |
                                |a2     |b2    |c2   |d2   |""")
    }

    def "should build common action table"() {
        given:
        def b = actionBuilder.builder()

        when:
        b.acceptAlert()
            .dismissAlert()
            .on("1").clear()
            .on("2").click()
            .on("3").doubleClick()
            .on("4").fill("a")
            .on("5").press("b")
            .on("6").select("c")

        then:
        deleteWhitespace(b.buildExamplesTable().asString()) ==
            deleteWhitespace("""|element|action      |value|alias|
                                |@alert |ACCEPT      |     |     |
                                |@alert |DISMISS     |     |     |
                                |1      |CLEAR       |     |     |
                                |2      |CLICK       |     |     |
                                |3      |DOUBLE_CLICK|     |     |
                                |4      |FILL        |a    |     |
                                |5      |PRESS       |b    |     |
                                |6      |SELECT      |c    |     |""")
    }

    def "should use custom separator"() {
        given:
        def b = actionBuilder.builder()

        when:
        b.headerSeparator("!")
            .valueSeparator("?")
            .on("a1").perform("b1").and()

        then:
        deleteWhitespace(b.buildExamplesTable().asString()) ==
            deleteWhitespace("""{headerSeparator=!,valueSeparator=?}
                                !element!action!value!alias!
                                ?a1     ?b1    ?     ?     ?""")
    }

}
