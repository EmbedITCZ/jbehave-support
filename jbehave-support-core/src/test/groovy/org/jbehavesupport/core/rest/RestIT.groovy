package org.jbehavesupport.core.rest

import org.jbehave.core.model.ExamplesTable
import org.jbehavesupport.core.TestConfig
import org.jbehavesupport.core.TestContext
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

    @Autowired
    private TestContext testContext

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
    void canSaveCollectionsWithDifferentNotations() {
        given:
        ExamplesTable requestTable = new ExamplesTable(
            "| name                    | data           |\n" +
            "| addresses.0.country     | Brazil         |\n" +
            "| addresses[0].city       | Rio de Janeiro |\n" +
            "| addresses.0.details.0   | details 0 0    |\n" +
            "| addresses[0].details[1] | details 0 1    |\n" +
            "| addresses[0].details.2  | details 0 2    |\n" +
            "| addresses.0.details[3]  | details 0 3    |"
        )

        ExamplesTable saveTable = new ExamplesTable(
            "| name                    | contextAlias        |\n" +
            "| addresses.0.country     | ADDRESS_0_COUNTRY   |\n" +
            "| addresses[0].city       | ADDRESS_0_CITY      |\n" +
            "| addresses.0.details.0   | ADDRESS_0_DETAILS_0 |\n" +
            "| addresses[0].details[1] | ADDRESS_0_DETAILS_1 |\n" +
            "| addresses[0].details.2  | ADDRESS_0_DETAILS_2 |\n" +
            "| addresses.0.details[3]  | ADDRESS_0_DETAILS_3 |"
        )

        when:
        restServiceHandler.sendRequest("user/", HttpMethod.valueOf("POST"), requestTable)
        restServiceHandler.saveResponse(saveTable)

        then:
        testContext.get("ADDRESS_0_COUNTRY") == "Brazil"
        testContext.get("ADDRESS_0_CITY") == "Rio de Janeiro"
        testContext.get("ADDRESS_0_DETAILS_0") == "details 0 0"
        testContext.get("ADDRESS_0_DETAILS_1") == "details 0 1"
        testContext.get("ADDRESS_0_DETAILS_2") == "details 0 2"
        testContext.get("ADDRESS_0_DETAILS_3") == "details 0 3"
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

    void canVerifyRawBody() {
        given:
        ExamplesTable examplesTable = new ExamplesTable(
            "| name      | data    |\n" +
                "| firstName | Pedro   |\n" +
                "| lastName  | Salgado |"
        )

        when:
        restServiceHandler.sendRequest("user/", HttpMethod.valueOf("POST"), examplesTable)

        then:
        ExamplesTable resultsTable = new ExamplesTable(
            "| name  | expectedValue                                    | verifier |\n" +
                "| @body | \"firstName\":\"Pedro\",\"lastName\":\"Salgado\" | CONTAINS |"
        )
        restServiceHandler.verifyResponse("200", resultsTable)
    }

    void canSaveRawBody() {
        given:
        ExamplesTable requestTable = new ExamplesTable(
            "| name      | data    |\n" +
                "| firstName | Pedro   |\n" +
                "| lastName  | Salgado |"
        )

        ExamplesTable saveTable = new ExamplesTable(
            "| name  | contextAlias |\n" +
                "| @body | JSON_BODY    |"
        )

        when:
        restServiceHandler.sendRequest("user/", HttpMethod.valueOf("POST"), requestTable)
        restServiceHandler.saveResponse(saveTable)

        then:
        testContext.get("JSON_BODY") == testContext.get(RestServiceHandler.REST_RESPONSE_JSON)
    }

}
