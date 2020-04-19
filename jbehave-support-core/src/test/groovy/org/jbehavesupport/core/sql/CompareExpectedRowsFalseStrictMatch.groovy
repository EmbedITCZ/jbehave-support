package org.jbehavesupport.core.sql

import org.jbehave.core.model.ExamplesTable
import org.jbehavesupport.core.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import java.time.Month
import java.time.LocalDateTime

@ContextConfiguration(classes = TestConfig)
class CompareExpectedRowsFalseStrictMatch extends Specification{
    @Autowired
    SqlSteps sqlSteps

    def "various Object test"() {
        given:
        def expectations = new ExamplesTable("| INT | DOUBLE | DECIMAL | STRING | DATE                |\r\n"
                                           + "|   1 |   1.01 |   10.01 | fsa#df | 2010-10-10T10:10:10 |\r\n")
        def row = new HashMap<String,Object>(){{
            put("INT",new Integer(1))
            put("DOUBLE",new Double(1.01))
            put("DECIMAL",new BigDecimal("10.01"))
            put("STRING",new String("fsa#df"))
            put("DATE", LocalDateTime.of(2010, Month.OCTOBER, 10, 10, 10, 10))}}
        def actualData = new ArrayList<Map<String,Object>>(){{add(row)}}

        when:
        sqlSteps.compareExpectedRows(expectations, actualData, false)
        then:
        noExceptionThrown()
    }
}
