package org.jbehavesupport.core.rest

import org.jbehave.core.model.ExamplesTable
import org.jbehavesupport.core.TestConfig
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static groovy.test.GroovyAssert.shouldFail

@ContextConfiguration(classes = TestConfig)
class RestIT extends Specification {

    @Autowired
    private RestServiceHandler restServiceHandler

    @Test
    void shouldRejectMultipleKeysWhenBody() {
        given:
        ExamplesTable examplesTable = new ExamplesTable(
            "| name      | data             |\n" +
            "| person.id | 22               |\n" +
            "| @body     | this is raw body |"
        )

        when:
        restServiceHandler.sendRequest("test", null, examplesTable)

        then:
        def throwable = thrown(IllegalStateException)
        throwable.getMessage().contains("If @body is present, no other keys except headers are allowed.")
    }

    @Test
    void canSendCollectionsWithDifferentNotations() {
        given:
        ExamplesTable examplesTable = new ExamplesTable(
            "| name                    | data           |\n" +
            "| addresses.0.country     | Brazil         |\n" +
            "| addresses[0].city       | Rio de Janeiro |\n" +
            "| addresses.0.details.0   | details 0 0    |\n" +
            "| addresses[0].details[1] | details 0 1    |\n" +
            "| addresses[0].details.2  | details 0 2    |\n" +
            "| addresses.0.details[3]  | details 0 3    |"
        )

        when:
        restServiceHandler.sendRequest("user/", HttpMethod.valueOf("POST"), examplesTable)

        then:
        ExamplesTable resultsTable = new ExamplesTable(
            "| name                    | expectedValue    | verifier |\n" +
            "| @header.Content-Type    | application/json | CONTAINS |\n" +
            "| id                      |                  | NOT_NULL |\n" +
            "| addresses[0].country    | Brazil           |          |\n" +
            "| addresses[0].city       | Rio de Janeiro   |          |\n" +
            "| addresses[0].details[0] | details 0 0      |          |\n" +
            "| addresses[0].details[1] | details 0 1      |          |\n" +
            "| addresses[0].details[2] | details 0 2      |          |\n" +
            "| addresses[0].details[3] | details 0 3      |          |"
        )
        restServiceHandler.verifyResponse("200", resultsTable)
    }

    @Test
    void canRecieveCollectionsWithDifferentNotations() {
        given:
        ExamplesTable examplesTable = new ExamplesTable(
            "| name                    | data           |\n" +
                "| addresses[0].country   | Brazil         |\n" +
                "| addresses[0].city      | Rio de Janeiro |\n" +
                "| addresses[0].details[0]| details 0 0    |\n" +
                "| addresses[0].details[1]| details 0 1    |\n" +
                "| addresses[0].details[2]| details 0 2    |\n" +
                "| addresses[0].details[3]| details 0 3    |"
        )

        when:
        restServiceHandler.sendRequest("user/", HttpMethod.valueOf("POST"), examplesTable)

        then:
        ExamplesTable resultsTable = new ExamplesTable(
            "| name                    | expectedValue    | verifier |\n" +
                "| @header.Content-Type   | application/json | CONTAINS |\n" +
                "| id                     |                  | NOT_NULL |\n" +
                "| addresses.0.country    | Brazil           |          |\n" +
                "| addresses[0].city      | Rio de Janeiro   |          |\n" +
                "| addresses.0.details.0  | details 0 0      |          |\n" +
                "| addresses[0].details[1]| details 0 1      |          |\n" +
                "| addresses[0].details.2 | details 0 2      |          |\n" +
                "| addresses.0.details[3] | details 0 3      |          |"
        )
        restServiceHandler.verifyResponse("200", resultsTable)
    }
}
